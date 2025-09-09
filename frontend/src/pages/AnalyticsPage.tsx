import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Button,
  Grid,
  GridItem,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Select,
  Badge,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
  StatArrow,
  Icon,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  Progress,
  useColorModeValue,
  Flex,
  Spacer,
  Divider,
  Tag,
  TagLabel,
  Switch,
  FormControl,
  FormLabel,
  Spinner,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
  useToast,
} from '@chakra-ui/react';
import { IconType } from 'react-icons';
import {
  FiTrendingUp,
  FiTrendingDown,
  FiMail,
  FiUsers,
  FiBarChart,
  FiPieChart,
  FiCalendar,
  FiDownload,
  FiFilter,
  FiEye,
  FiTarget,
  FiClock,
  FiRefreshCw,
  FiActivity,
  FiLayers,
  FiZap,
} from 'react-icons/fi';
import AnalyticsService, { 
  LiveStats, 
  CampaignAnalytics, 
  TemplateAnalytics, 
  RecentActivity,
  AnalyticsFilters 
} from '../api/analyticsService';
import { useWebSocket } from '../contexts/WebSocketContext';

// Custom hook to safely use WebSocket context
export const useWebSocketSafe = () => {
  try {
    return useWebSocket();
  } catch (error) {
    console.warn('WebSocket not available:', error);
    return {
      connected: false,
      subscribe: (topic: string, callback: (message: any) => void) => {},
      send: (destination: string, body: any) => {},
      lastMessage: null
    };
  }
};

interface MetricCardProps {
  title: string;
  value: string | number;
  change?: number;
  icon: IconType;
  color: string;
  description?: string;
  isLoading?: boolean;
}

const MetricCard: React.FC<MetricCardProps> = ({ 
  title, 
  value, 
  change, 
  icon, 
  color, 
  description, 
  isLoading = false 
}) => (
  <Card>
    <CardBody>
      <HStack justify="space-between" align="flex-start">
        <VStack align="flex-start" spacing={2}>
          <Text fontSize="sm" color="gray.600" fontWeight="medium">
            {title}
          </Text>
          <Stat>
            <StatNumber fontSize="3xl" fontWeight="bold" color="gray.800">
              {isLoading ? <Spinner size="sm" /> : value}
            </StatNumber>
            {description && (
              <Text fontSize="sm" color="gray.500">
                {description}
              </Text>
            )}
            {change !== undefined && (
              <HStack spacing={1}>
                <StatArrow type={change >= 0 ? 'increase' : 'decrease'} />
                <StatHelpText color={change >= 0 ? 'green.500' : 'red.500'} mb={0}>
                  {Math.abs(change)}% from last period
                </StatHelpText>
              </HStack>
            )}
          </Stat>
        </VStack>
        <Box
          p={3}
          bg={`${color}.100`}
          borderRadius="lg"
          color={`${color}.600`}
        >
          <Icon as={icon as any} boxSize={6} />
        </Box>
      </HStack>
    </CardBody>
  </Card>
);

const getStatusColor = (status: string) => {
  switch (status.toLowerCase()) {
    case 'running':
    case 'active': return 'green';
    case 'completed': return 'blue';
    case 'scheduled': return 'yellow';
    case 'draft': return 'gray';
    case 'paused': return 'orange';
    default: return 'gray';
  }
};

const getActivityIcon = (type: string) => {
  switch (type.toLowerCase()) {
    case 'campaign_created':
    case 'campaign_started':
    case 'campaign_completed': return FiTarget;
    case 'email_sent':
    case 'email_opened':
    case 'email_clicked': return FiMail;
    case 'template_created':
    case 'template_updated': return FiLayers;
    case 'subscriber_added':
    case 'subscriber_unsubscribed': return FiUsers;
    default: return FiActivity;
  }
};

export const AnalyticsPage: React.FC = () => {
  const [selectedPeriod, setSelectedPeriod] = useState('30d');
  const [selectedMetric, setSelectedMetric] = useState('overview');
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);
  
  // Data states
  const [liveStats, setLiveStats] = useState<LiveStats | null>(null);
  const [campaignAnalytics, setCampaignAnalytics] = useState<CampaignAnalytics[]>([]);
  const [templateAnalytics, setTemplateAnalytics] = useState<TemplateAnalytics[]>([]);
  const [recentActivities, setRecentActivities] = useState<RecentActivity[]>([]);
  
  const toast = useToast();
  const { connected, subscribe } = useWebSocketSafe();

  const getDateRange = useCallback(() => {
    const now = new Date();
    const startDate = new Date();
    
    switch (selectedPeriod) {
      case '7d':
        startDate.setDate(now.getDate() - 7);
        break;
      case '30d':
        startDate.setDate(now.getDate() - 30);
        break;
      case '90d':
        startDate.setDate(now.getDate() - 90);
        break;
      case '1y':
        startDate.setFullYear(now.getFullYear() - 1);
        break;
      default:
        startDate.setDate(now.getDate() - 30);
    }
    
    return {
      startDate: startDate.toISOString(),
      endDate: now.toISOString()
    };
  }, [selectedPeriod]);

  const fetchAllData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      const dateRange = getDateRange();
      const filters: AnalyticsFilters = {
        startDate: dateRange.startDate,
        endDate: dateRange.endDate
      };

      // Fetch all data in parallel
      const [liveStatsData, campaignsData, templatesData, activitiesData] = await Promise.all([
        AnalyticsService.getLiveStats(filters),
        AnalyticsService.getCampaignAnalytics(filters),
        AnalyticsService.getTemplateAnalytics(filters),
        AnalyticsService.getRecentActivities(10)
      ]);

      setLiveStats(liveStatsData);
      setCampaignAnalytics(campaignsData);
      setTemplateAnalytics(templatesData);
      setRecentActivities(activitiesData);
      setLastUpdated(new Date());
      
    } catch (err) {
      console.error('Error fetching analytics data:', err);
      setError('Failed to load analytics data from backend');
      toast({
        title: 'Error',
        description: 'Failed to load analytics data',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  }, [getDateRange, toast]);

  // WebSocket subscription for real-time updates
  useEffect(() => {
    if (connected && autoRefresh) {
      subscribe('/topic/analytics_live', (message) => {
        if (message.type === 'live_analytics_broadcast' && message.data) {
          setLiveStats(message.data);
          setLastUpdated(new Date());
        }
      });
    }
  }, [connected, autoRefresh, subscribe]);

  // Auto-refresh effect
  useEffect(() => {
    fetchAllData();
    
    if (autoRefresh && !connected) {
      const interval = setInterval(fetchAllData, 10000); // 10 seconds
      return () => clearInterval(interval);
    }
  }, [fetchAllData, autoRefresh, connected]);

  // Manual refresh
  const handleRefresh = () => {
    fetchAllData();
  };

  const liveStatsMetrics = [
    {
      title: 'Total Emails Sent',
      value: liveStats?.totalEmailsSent?.toLocaleString() || '0',
      change: 15.2,
      icon: FiMail,
      color: 'blue',
      description: selectedPeriod === '7d' ? 'This week' : selectedPeriod === '30d' ? 'This month' : 'This quarter',
    },
    {
      title: 'Active Subscribers',
      value: liveStats?.activeSubscribers?.toLocaleString() || '0',
      change: 8.5,
      icon: FiUsers,
      color: 'green',
      description: 'Currently subscribed',
    },
    {
      title: 'Open Rate',
      value: liveStats?.openRate ? `${liveStats.openRate.toFixed(1)}%` : '0%',
      change: 12.3,
      icon: FiEye,
      color: 'purple',
      description: 'Industry avg: 21.5%',
    },
    {
      title: 'Click Rate',
      value: liveStats?.clickRate ? `${liveStats.clickRate.toFixed(1)}%` : '0%',
      change: 18.2,
      icon: FiTarget,
      color: 'orange',
      description: 'Industry avg: 2.8%',
    },
  ];

  if (error) {
    return (
      <VStack spacing={4} align="stretch">
        <Alert status="error">
          <AlertIcon />
          <AlertTitle>Error loading analytics!</AlertTitle>
          <AlertDescription>{error}</AlertDescription>
        </Alert>
        <Button onClick={handleRefresh} leftIcon={<Icon as={FiRefreshCw as any} />}>
          Retry
        </Button>
      </VStack>
    );
  }

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <HStack justify="space-between" align="flex-start" mb={2}>
          <Box>
            <Heading size="lg" color="gray.800" mb={2}>
              Analytics & Performance
            </Heading>
            <Text color="gray.600">
              Track your email campaign performance and gain insights to improve engagement.
            </Text>
          </Box>
          <VStack spacing={2} align="flex-end">
            <HStack spacing={4}>
              <HStack spacing={2}>
                <Box
                  w={2}
                  h={2}
                  borderRadius="full"
                  bg={connected ? 'green.500' : 'red.500'}
                />
                <Text fontSize="xs" color="gray.500">
                  {connected ? 'Live' : 'Offline'}
                </Text>
              </HStack>
              <FormControl display="flex" alignItems="center">
                <FormLabel htmlFor="auto-refresh" mb="0" fontSize="sm">
                  Auto-refresh
                </FormLabel>
                <Switch
                  id="auto-refresh"
                  isChecked={autoRefresh}
                  onChange={(e) => setAutoRefresh(e.target.checked)}
                  colorScheme="blue"
                />
              </FormControl>
              <Button
                size="sm"
                variant="outline"
                leftIcon={<Icon as={FiRefreshCw as any} />}
                onClick={handleRefresh}
                isLoading={loading}
              >
                Refresh
              </Button>
            </HStack>
            {lastUpdated && (
              <Text fontSize="xs" color="gray.500">
                Last updated: {lastUpdated.toLocaleTimeString()}
              </Text>
            )}
          </VStack>
        </HStack>
      </Box>

      {/* Controls */}
      <HStack justify="space-between" align="center">
        <HStack spacing={4}>
          <Select
            value={selectedPeriod}
            onChange={(e) => setSelectedPeriod(e.target.value)}
            w="150px"
          >
            <option value="7d">Last 7 days</option>
            <option value="30d">Last 30 days</option>
            <option value="90d">Last 90 days</option>
            <option value="1y">Last year</option>
          </Select>

          <Select
            value={selectedMetric}
            onChange={(e) => setSelectedMetric(e.target.value)}
            w="180px"
          >
            <option value="overview">Overview</option>
            <option value="campaigns">Campaign Analytics</option>
            <option value="templates">Template Analytics</option>
            <option value="activity">Recent Activity</option>
          </Select>
        </HStack>

        <Button leftIcon={<Icon as={FiDownload as any} />} variant="outline">
          Export Report
        </Button>
      </HStack>

      {/* Live Stats Cards */}
      <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }} gap={6}>
        {liveStatsMetrics.map((metric, index) => (
          <GridItem key={index}>
            <MetricCard {...metric} isLoading={loading} />
          </GridItem>
        ))}
      </Grid>

      {/* Main Content Tabs */}
      <Tabs index={['overview', 'campaigns', 'templates', 'activity'].indexOf(selectedMetric)} 
            onChange={(index) => setSelectedMetric(['overview', 'campaigns', 'templates', 'activity'][index])}>
        <TabList>
          <Tab>Overview</Tab>
          <Tab>Campaign Analytics</Tab>
          <Tab>Template Analytics</Tab>
          <Tab>Recent Activity</Tab>
        </TabList>

        <TabPanels>
          {/* Overview Tab */}
          <TabPanel p={0} pt={6}>
            <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={8}>
              {/* Campaign Performance Summary */}
              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      Campaign Performance Summary
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <VStack spacing={4} align="stretch">
                      {campaignAnalytics.slice(0, 5).map((campaign) => (
                        <Box key={campaign.id} p={4} border="1px solid" borderColor="gray.200" borderRadius="lg">
                          <HStack justify="space-between" mb={2}>
                            <Text fontWeight="medium" color="gray.800">
                              {campaign.name}
                            </Text>
                            <Badge colorScheme={getStatusColor(campaign.status)} size="sm">
                              {campaign.status}
                            </Badge>
                          </HStack>
                          <VStack spacing={2} align="stretch">
                            <HStack justify="space-between">
                              <Text fontSize="sm" color="gray.600">Progress</Text>
                              <Text fontSize="sm" fontWeight="medium">
                                {campaign.sentCount} / {campaign.totalRecipients}
                              </Text>
                            </HStack>
                            <Progress 
                              value={campaign.progressPercentage} 
                              colorScheme="blue" 
                              size="sm" 
                              borderRadius="full" 
                            />
                            <HStack justify="space-between" fontSize="sm">
                              <Text color="green.600">Open: {campaign.openRate.toFixed(1)}%</Text>
                              <Text color="blue.600">Click: {campaign.clickRate.toFixed(1)}%</Text>
                            </HStack>
                          </VStack>
                        </Box>
                      ))}
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>

              {/* Quick Insights */}
              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      Quick Insights
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <VStack spacing={4} align="stretch">
                      <Box p={4} bg="green.50" borderRadius="lg" border="1px solid" borderColor="green.200">
                        <Text fontSize="sm" fontWeight="medium" color="green.800">
                          🎯 Best Performing Campaign
                        </Text>
                        <Text fontSize="lg" fontWeight="bold" color="green.800">
                          {campaignAnalytics.length > 0 
                            ? campaignAnalytics.reduce((best, current) => 
                                current.openRate > best.openRate ? current : best
                              ).name
                            : 'No campaigns yet'
                          }
                        </Text>
                        <Text fontSize="sm" color="green.700">
                          {campaignAnalytics.length > 0 
                            ? `${campaignAnalytics.reduce((best, current) => 
                                current.openRate > best.openRate ? current : best
                              ).openRate.toFixed(1)}% open rate`
                            : 'Start your first campaign'
                          }
                        </Text>
                      </Box>

                      <Box p={4} bg="blue.50" borderRadius="lg" border="1px solid" borderColor="blue.200">
                        <Text fontSize="sm" fontWeight="medium" color="blue.800">
                          📈 Total Campaigns
                        </Text>
                        <Text fontSize="lg" fontWeight="bold" color="blue.800">
                          {liveStats?.totalCampaigns || 0}
                        </Text>
                        <Text fontSize="sm" color="blue.700">
                          {liveStats?.activeCampaigns || 0} active
                        </Text>
                      </Box>

                      <Box p={4} bg="purple.50" borderRadius="lg" border="1px solid" borderColor="purple.200">
                        <Text fontSize="sm" fontWeight="medium" color="purple.800">
                          📊 Template Usage
                        </Text>
                        <Text fontSize="lg" fontWeight="bold" color="purple.800">
                          {templateAnalytics.length} templates
                        </Text>
                        <Text fontSize="sm" color="purple.700">
                          {templateAnalytics.reduce((sum, t) => sum + t.totalUsage, 0)} total uses
                        </Text>
                      </Box>
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>
            </Grid>
          </TabPanel>

          {/* Campaign Analytics Tab */}
          <TabPanel p={0} pt={6}>
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Campaign Performance Analytics
                </Heading>
              </CardHeader>
              <CardBody>
                <Table variant="simple">
                  <Thead>
                    <Tr>
                      <Th>Campaign</Th>
                      <Th>Status</Th>
                      <Th>Progress</Th>
                      <Th>Sent</Th>
                      <Th>Open Rate</Th>
                      <Th>Click Rate</Th>
                      <Th>Created</Th>
                    </Tr>
                  </Thead>
                  <Tbody>
                    {campaignAnalytics.map((campaign) => (
                      <Tr key={campaign.id}>
                        <Td>
                          <VStack align="flex-start" spacing={1}>
                            <Text fontWeight="medium" color="gray.800">
                              {campaign.name}
                            </Text>
                            <Text fontSize="sm" color="gray.500">
                              {campaign.description}
                            </Text>
                          </VStack>
                        </Td>
                        <Td>
                          <Badge colorScheme={getStatusColor(campaign.status)} size="sm">
                            {campaign.status}
                          </Badge>
                        </Td>
                        <Td>
                          <VStack spacing={1} align="stretch">
                            <HStack justify="space-between" fontSize="sm">
                              <Text>{campaign.progressPercentage.toFixed(1)}%</Text>
                              <Text color="gray.500">
                                {campaign.sentCount}/{campaign.totalRecipients}
                              </Text>
                            </HStack>
                            <Progress 
                              value={campaign.progressPercentage} 
                              colorScheme="blue" 
                              size="sm" 
                              borderRadius="full" 
                            />
                          </VStack>
                        </Td>
                        <Td>{campaign.sentCount.toLocaleString()}</Td>
                        <Td>
                          <HStack spacing={2}>
                            <Text fontWeight="medium">{campaign.openRate.toFixed(1)}%</Text>
                            <Badge
                              colorScheme={campaign.openRate > 25 ? 'green' : 'yellow'}
                              size="sm"
                            >
                              {campaign.openRate > 25 ? 'Good' : 'Average'}
                            </Badge>
                          </HStack>
                        </Td>
                        <Td>
                          <HStack spacing={2}>
                            <Text fontWeight="medium">{campaign.clickRate.toFixed(1)}%</Text>
                            <Badge
                              colorScheme={campaign.clickRate > 5 ? 'green' : 'yellow'}
                              size="sm"
                            >
                              {campaign.clickRate > 5 ? 'Good' : 'Average'}
                            </Badge>
                          </HStack>
                        </Td>
                        <Td>
                          <Text fontSize="sm" color="gray.500">
                            {new Date(campaign.createdAt).toLocaleDateString()}
                          </Text>
                        </Td>
                      </Tr>
                    ))}
                  </Tbody>
                </Table>
              </CardBody>
            </Card>
          </TabPanel>

          {/* Template Analytics Tab */}
          <TabPanel p={0} pt={6}>
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Template Usage Analytics
                </Heading>
              </CardHeader>
              <CardBody>
                <Table variant="simple">
                  <Thead>
                    <Tr>
                      <Th>Template</Th>
                      <Th>Total Usage</Th>
                      <Th>Open Rate</Th>
                      <Th>Click Rate</Th>
                      <Th>Last Used</Th>
                    </Tr>
                  </Thead>
                  <Tbody>
                    {templateAnalytics.map((template) => (
                      <Tr key={template.id}>
                        <Td>
                          <VStack align="flex-start" spacing={1}>
                            <Text fontWeight="medium" color="gray.800">
                              {template.name}
                            </Text>
                            <Text fontSize="sm" color="gray.500">
                              {template.description}
                            </Text>
                          </VStack>
                        </Td>
                        <Td>
                          <Text fontWeight="medium">{template.totalUsage}</Text>
                        </Td>
                        <Td>
                          <HStack spacing={2}>
                            <Text fontWeight="medium">{template.openRate.toFixed(1)}%</Text>
                            <Badge
                              colorScheme={template.openRate > 20 ? 'green' : 'yellow'}
                              size="sm"
                            >
                              {template.openRate > 20 ? 'Good' : 'Average'}
                            </Badge>
                          </HStack>
                        </Td>
                        <Td>
                          <HStack spacing={2}>
                            <Text fontWeight="medium">{template.clickRate.toFixed(1)}%</Text>
                            <Badge
                              colorScheme={template.clickRate > 3 ? 'green' : 'yellow'}
                              size="sm"
                            >
                              {template.clickRate > 3 ? 'Good' : 'Average'}
                            </Badge>
                          </HStack>
                        </Td>
                        <Td>
                          <Text fontSize="sm" color="gray.500">
                            {template.lastUsedAt 
                              ? new Date(template.lastUsedAt).toLocaleDateString()
                              : 'Never'
                            }
                          </Text>
                        </Td>
                      </Tr>
                    ))}
                  </Tbody>
                </Table>
              </CardBody>
            </Card>
          </TabPanel>

          {/* Recent Activity Tab */}
          <TabPanel p={0} pt={6}>
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Recent Activity Feed
                </Heading>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  {recentActivities.map((activity) => {
                    const ActivityIcon = getActivityIcon(activity.type);
                    return (
                      <Box key={activity.id} p={4} border="1px solid" borderColor="gray.200" borderRadius="lg">
                        <HStack spacing={3} align="flex-start">
                          <Box
                            p={2}
                            bg="blue.100"
                            borderRadius="lg"
                            color="blue.600"
                          >
                            <Icon as={ActivityIcon as any} boxSize={4} />
                          </Box>
                          <VStack align="flex-start" spacing={1} flex={1}>
                            <Text fontWeight="medium" color="gray.800">
                              {activity.title}
                            </Text>
                            <Text fontSize="sm" color="gray.600">
                              {activity.description}
                            </Text>
                            <HStack spacing={2}>
                              <Tag size="sm" colorScheme="gray">
                                <TagLabel>{activity.type.replace('_', ' ')}</TagLabel>
                              </Tag>
                              <Text fontSize="xs" color="gray.500">
                                {activity.timeAgo}
                              </Text>
                            </HStack>
                          </VStack>
                        </HStack>
                      </Box>
                    );
                  })}
                </VStack>
              </CardBody>
            </Card>
          </TabPanel>
        </TabPanels>
      </Tabs>
    </VStack>
  );
};