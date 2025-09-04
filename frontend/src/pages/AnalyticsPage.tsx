import React, { useState } from 'react';
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
} from 'react-icons/fi';

interface MetricCardProps {
  title: string;
  value: string | number;
  change: number;
  icon: IconType;
  color: string;
  description?: string;
}

const MetricCard: React.FC<MetricCardProps> = ({ title, value, change, icon, color, description }) => (
  <Card>
    <CardBody>
      <HStack justify="space-between" align="flex-start">
        <VStack align="flex-start" spacing={2}>
          <Text fontSize="sm" color="gray.600" fontWeight="medium">
            {title}
          </Text>
          <Stat>
            <StatNumber fontSize="3xl" fontWeight="bold" color="gray.800">
              {value}
            </StatNumber>
            {description && (
              <Text fontSize="sm" color="gray.500">
                {description}
              </Text>
            )}
            <HStack spacing={1}>
              <StatArrow type={change >= 0 ? 'increase' : 'decrease'} />
              <StatHelpText color={change >= 0 ? 'green.500' : 'red.500'} mb={0}>
                {Math.abs(change)}% from last period
              </StatHelpText>
            </HStack>
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

interface CampaignData {
  id: string;
  name: string;
  sent: number;
  delivered: number;
  opened: number;
  clicked: number;
  bounced: number;
  unsubscribed: number;
  openRate: number;
  clickRate: number;
  status: 'active' | 'completed' | 'scheduled' | 'draft';
  date: string;
}

const mockCampaignData: CampaignData[] = [
  {
    id: '1',
    name: 'Newsletter - Q4 2024',
    sent: 2500,
    delivered: 2450,
    opened: 612,
    clicked: 98,
    bounced: 50,
    unsubscribed: 12,
    openRate: 24.5,
    clickRate: 4.0,
    status: 'completed',
    date: '2024-12-15',
  },
  {
    id: '2',
    name: 'Product Launch Announcement',
    sent: 1800,
    delivered: 1760,
    opened: 528,
    clicked: 158,
    bounced: 40,
    unsubscribed: 8,
    openRate: 30.0,
    clickRate: 9.0,
    status: 'completed',
    date: '2024-12-10',
  },
  {
    id: '3',
    name: 'Welcome Series - Day 1',
    sent: 500,
    delivered: 490,
    opened: 245,
    clicked: 49,
    bounced: 10,
    unsubscribed: 2,
    openRate: 50.0,
    clickRate: 10.0,
    status: 'active',
    date: '2024-12-20',
  },
  {
    id: '4',
    name: 'Holiday Promotion',
    sent: 3200,
    delivered: 3100,
    opened: 620,
    clicked: 186,
    bounced: 100,
    unsubscribed: 15,
    openRate: 20.0,
    clickRate: 6.0,
    status: 'scheduled',
    date: '2024-12-25',
  },
];

const getStatusColor = (status: string) => {
  switch (status) {
    case 'active': return 'green';
    case 'completed': return 'blue';
    case 'scheduled': return 'yellow';
    case 'draft': return 'gray';
    default: return 'gray';
  }
};

export const AnalyticsPage: React.FC = () => {
  const [selectedPeriod, setSelectedPeriod] = useState('30d');
  const [selectedMetric, setSelectedMetric] = useState('overview');

  const overviewMetrics = [
    {
      title: 'Total Emails Sent',
      value: '8,000',
      change: 15.2,
      icon: FiMail,
      color: 'blue',
      description: 'This month',
    },
    {
      title: 'Open Rate',
      value: '26.8%',
      change: 8.5,
      icon: FiEye,
      color: 'green',
      description: 'Industry avg: 21.5%',
    },
    {
      title: 'Click Rate',
      value: '4.2%',
      change: 12.3,
              icon: FiTarget,
      color: 'purple',
      description: 'Industry avg: 2.8%',
    },
    {
      title: 'Bounce Rate',
      value: '2.1%',
      change: -5.2,
      icon: FiTrendingDown,
      color: 'orange',
      description: 'Industry avg: 2.5%',
    },
  ];

  const engagementMetrics = [
    {
      title: 'Average Time to Open',
      value: '2.4 hrs',
      change: -12.5,
      icon: FiClock,
      color: 'teal',
    },
    {
      title: 'Click-to-Open Rate',
      value: '15.7%',
      change: 18.2,
              icon: FiBarChart,
      color: 'cyan',
    },
    {
      title: 'Unsubscribe Rate',
      value: '0.3%',
      change: -8.1,
      icon: FiUsers,
      color: 'red',
    },
    {
      title: 'Spam Complaints',
      value: '0.02%',
      change: -25.0,
      icon: FiTrendingDown,
      color: 'green',
    },
  ];

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <Heading size="lg" color="gray.800" mb={2}>
          Analytics & Performance
        </Heading>
        <Text color="gray.600">
          Track your email campaign performance and gain insights to improve engagement.
        </Text>
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
            <option value="engagement">Engagement</option>
            <option value="campaigns">Campaign Performance</option>
          </Select>
        </HStack>

        <Button leftIcon={<Icon as={FiDownload as any} />} variant="outline">
          Export Report
        </Button>
      </HStack>

      {/* Metrics Grid */}
      <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }} gap={6}>
        {(selectedMetric === 'overview' ? overviewMetrics : engagementMetrics).map((metric, index) => (
          <GridItem key={index}>
            <MetricCard {...metric} />
          </GridItem>
        ))}
      </Grid>

      {/* Charts and Tables */}
      <Tabs index={['overview', 'engagement', 'campaigns'].indexOf(selectedMetric)} onChange={(index) => setSelectedMetric(['overview', 'engagement', 'campaigns'][index])}>
        <TabList>
          <Tab>Overview</Tab>
          <Tab>Engagement</Tab>
          <Tab>Campaign Performance</Tab>
        </TabList>

        <TabPanels>
          {/* Overview Tab */}
          <TabPanel p={0} pt={6}>
            <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={8}>
              {/* Chart Placeholder */}
              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      Email Performance Trends
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <Box
                      h="300px"
                      bg="gray.50"
                      borderRadius="lg"
                      display="flex"
                      alignItems="center"
                      justifyContent="center"
                      border="2px dashed"
                      borderColor="gray.200"
                    >
                      <VStack spacing={2}>
                        <Icon as={FiBarChart as any} boxSize={12} color="gray.400" />
                        <Text color="gray.500" fontSize="lg">
                          Performance Chart
                        </Text>
                        <Text color="gray.400" fontSize="sm">
                          Chart component integration coming soon
                        </Text>
                      </VStack>
                    </Box>
                  </CardBody>
                </Card>
              </GridItem>

              {/* Quick Stats */}
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
                          🎯 Best Performing
                        </Text>
                        <Text fontSize="lg" fontWeight="bold" color="green.800">
                          Welcome Series
                        </Text>
                        <Text fontSize="sm" color="green.700">
                          50% open rate, 10% click rate
                        </Text>
                      </Box>

                      <Box p={4} bg="blue.50" borderRadius="lg" border="1px solid" borderColor="blue.200">
                        <Text fontSize="sm" fontWeight="medium" color="blue.800">
                          📈 Trending Up
                        </Text>
                        <Text fontSize="lg" fontWeight="bold" color="blue.800">
                          Click Rate
                        </Text>
                        <Text fontSize="sm" color="blue.700">
                          +12.3% from last period
                        </Text>
                      </Box>

                      <Box p={4} bg="orange.50" borderRadius="lg" border="1px solid" borderColor="orange.200">
                        <Text fontSize="sm" fontWeight="medium" color="orange.800">
                          ⚠️ Needs Attention
                        </Text>
                        <Text fontSize="lg" fontWeight="bold" color="orange.800">
                          Bounce Rate
                        </Text>
                        <Text fontSize="sm" color="orange.700">
                          Slightly above industry average
                        </Text>
                      </Box>
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>
            </Grid>
          </TabPanel>

          {/* Engagement Tab */}
          <TabPanel p={0} pt={6}>
            <Grid templateColumns={{ base: '1fr', lg: '1fr 1fr' }} gap={8}>
              {/* Engagement Chart */}
              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      Engagement Over Time
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <Box
                      h="300px"
                      bg="gray.50"
                      borderRadius="lg"
                      display="flex"
                      alignItems="center"
                      justifyContent="center"
                      border="2px dashed"
                      borderColor="gray.200"
                    >
                      <VStack spacing={2}>
                        <Icon as={FiPieChart as any} boxSize={12} color="gray.400" />
                        <Text color="gray.500" fontSize="lg">
                          Engagement Chart
                        </Text>
                        <Text color="gray.400" fontSize="sm">
                          Visualize engagement patterns
                        </Text>
                      </VStack>
                    </Box>
                  </CardBody>
                </Card>
              </GridItem>

              {/* Subscriber Activity */}
              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      Subscriber Activity
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <VStack spacing={4} align="stretch">
                      <Box>
                        <HStack justify="space-between" mb={2}>
                          <Text fontSize="sm" color="gray.600">Active Subscribers</Text>
                          <Text fontSize="sm" fontWeight="medium">8,234</Text>
                        </HStack>
                        <Progress value={82} colorScheme="green" size="sm" borderRadius="full" />
                      </Box>

                      <Box>
                        <HStack justify="space-between" mb={2}>
                          <Text fontSize="sm" color="gray.600">Engaged (Opened last 30 days)</Text>
                          <Text fontSize="sm" fontWeight="medium">6,587</Text>
                        </HStack>
                        <Progress value={65} colorScheme="blue" size="sm" borderRadius="full" />
                      </Box>

                      <Box>
                        <HStack justify="space-between" mb={2}>
                          <Text fontSize="sm" color="gray.600">Highly Engaged (Clicked last 30 days)</Text>
                          <Text fontSize="sm" fontWeight="medium">2,456</Text>
                        </HStack>
                        <Progress value={24} colorScheme="purple" size="sm" borderRadius="full" />
                      </Box>
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>
            </Grid>
          </TabPanel>

          {/* Campaign Performance Tab */}
          <TabPanel p={0} pt={6}>
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Campaign Performance
                </Heading>
              </CardHeader>
              <CardBody>
                <Table variant="simple">
                  <Thead>
                    <Tr>
                      <Th>Campaign</Th>
                      <Th>Sent</Th>
                      <Th>Open Rate</Th>
                      <Th>Click Rate</Th>
                      <Th>Bounce Rate</Th>
                      <Th>Status</Th>
                      <Th>Date</Th>
                    </Tr>
                  </Thead>
                  <Tbody>
                    {mockCampaignData.map((campaign) => (
                      <Tr key={campaign.id}>
                        <Td>
                          <VStack align="flex-start" spacing={1}>
                            <Text fontWeight="medium" color="gray.800">
                              {campaign.name}
                            </Text>
                            <Text fontSize="sm" color="gray.500">
                              {campaign.delivered.toLocaleString()} delivered
                            </Text>
                          </VStack>
                        </Td>
                        <Td>{campaign.sent.toLocaleString()}</Td>
                        <Td>
                          <HStack spacing={2}>
                            <Text fontWeight="medium">{campaign.openRate}%</Text>
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
                            <Text fontWeight="medium">{campaign.clickRate}%</Text>
                            <Badge
                              colorScheme={campaign.clickRate > 5 ? 'green' : 'yellow'}
                              size="sm"
                            >
                              {campaign.clickRate > 5 ? 'Good' : 'Average'}
                            </Badge>
                          </HStack>
                        </Td>
                        <Td>
                          <Text color={campaign.bounced / campaign.sent > 0.05 ? 'red.500' : 'gray.600'}>
                            {((campaign.bounced / campaign.sent) * 100).toFixed(1)}%
                          </Text>
                        </Td>
                        <Td>
                          <Badge colorScheme={getStatusColor(campaign.status)} size="sm">
                            {campaign.status}
                          </Badge>
                        </Td>
                        <Td>
                          <Text fontSize="sm" color="gray.500">
                            {new Date(campaign.date).toLocaleDateString()}
                          </Text>
                        </Td>
                      </Tr>
                    ))}
                  </Tbody>
                </Table>
              </CardBody>
            </Card>
          </TabPanel>
        </TabPanels>
      </Tabs>
    </VStack>
  );
};
