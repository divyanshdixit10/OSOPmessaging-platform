import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  GridItem,
  VStack,
  HStack,
  Text,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
  StatArrow,
  Icon,
  Button,
  useColorModeValue,
  Card,
  CardBody,
  Heading,
  Badge,
  Avatar,
  Divider,
  Progress,
  Spinner,
  Alert,
  AlertIcon,
} from '@chakra-ui/react';
import { IconType } from 'react-icons';
import {
  FiMail,
  FiUsers,
  FiBarChart,
  FiTrendingUp,
  FiClock,
  FiCheckCircle,
  FiAlertCircle,
  FiSend,
} from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import AnalyticsService from '../api/analyticsService';

interface StatCardProps {
  title: string;
  value: string | number;
  change: number;
  icon: IconType;
  color: string;
}

const StatCard: React.FC<StatCardProps> = ({ title, value, change, icon, color }) => (
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
            <HStack spacing={1}>
              <StatArrow type={change >= 0 ? 'increase' : 'decrease'} />
              <StatHelpText color={change >= 0 ? 'green.500' : 'red.500'} mb={0}>
                {Math.abs(change)}% from last month
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

interface RecentActivityProps {
  type: 'email' | 'template' | 'campaign';
  title: string;
  description: string;
  time: string;
  status: 'success' | 'pending' | 'error';
}

const RecentActivity: React.FC<RecentActivityProps> = ({ type, title, description, time, status }) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'success': return 'green';
      case 'pending': return 'yellow';
      case 'error': return 'red';
      default: return 'gray';
    }
  };

  const getStatusIcon = (status: string): IconType => {
    switch (status) {
      case 'success': return FiCheckCircle;
      case 'pending': return FiClock;
      case 'error': return FiAlertCircle;
      default: return FiClock;
    }
  };

  return (
    <HStack spacing={4} p={4} borderRadius="lg" bg="white" border="1px solid" borderColor="gray.200">
      <Avatar size="sm" name={title} bg="brand.100" color="brand.600" />
      <Box flex={1}>
        <Text fontWeight="medium" color="gray.800" fontSize="sm">
          {title}
        </Text>
        <Text fontSize="xs" color="gray.600">
          {description}
        </Text>
        <Text fontSize="xs" color="gray.500" mt={1}>
          {time}
        </Text>
      </Box>
      <Badge colorScheme={getStatusColor(status)} variant="subtle" size="sm">
        <HStack spacing={1}>
          <Icon as={getStatusIcon(status) as any} boxSize={3} />
          <Text>{status}</Text>
        </HStack>
      </Badge>
    </HStack>
  );
};

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const cardBg = useColorModeValue('white', 'gray.800');
  const [dashboardData, setDashboardData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        const data = await AnalyticsService.getDashboardStatsWithFallback();
        setDashboardData(data);
        setError(null);
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError('Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const stats = [
    {
      title: 'Total Emails Sent',
      value: dashboardData?.totalEmailsSent?.toLocaleString() || '0',
      change: 12.5,
      icon: FiMail,
      color: 'blue',
    },
    {
      title: 'Active Subscribers',
      value: dashboardData?.activeSubscribers?.toLocaleString() || '0',
      change: 8.2,
      icon: FiUsers,
      color: 'green',
    },
    {
      title: 'Open Rate',
      value: dashboardData?.openRate ? `${dashboardData.openRate}%` : '0%',
      change: -2.1,
      icon: FiBarChart,
      color: 'purple',
    },
    {
      title: 'Click Rate',
      value: dashboardData?.clickRate ? `${dashboardData.clickRate}%` : '0%',
      change: 15.7,
      icon: FiTrendingUp,
      color: 'orange',
    },
  ];

  const campaignProgress = [
    { name: 'Newsletter Campaign', progress: 75, sent: 1500, total: 2000 },
    { name: 'Product Updates', progress: 45, sent: 900, total: 2000 },
    { name: 'Welcome Series', progress: 90, sent: 1800, total: 2000 },
  ];

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minH="400px">
        <VStack spacing={4}>
          <Spinner size="xl" color="brand.500" />
          <Text color="gray.600">Loading dashboard...</Text>
        </VStack>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert status="error" borderRadius="lg">
        <AlertIcon />
        <Box>
          <Text fontWeight="bold">Error loading dashboard</Text>
          <Text fontSize="sm">{error}</Text>
        </Box>
      </Alert>
    );
  }

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <Heading size="lg" color="gray.800" mb={2}>
          Dashboard
        </Heading>
        <Text color="gray.600">
          Welcome back! Here's what's happening with your messaging platform.
        </Text>
      </Box>

      {/* Quick Actions */}
      <HStack spacing={4}>
        <Button
          leftIcon={<Icon as={FiSend as any} />}
          colorScheme="brand"
          onClick={() => navigate('/send-email')}
        >
          Send Email
        </Button>
        <Button
          leftIcon={<Icon as={FiUsers as any} />}
          variant="outline"
          onClick={() => navigate('/templates')}
        >
          Create Template
        </Button>
        <Button
          leftIcon={<Icon as={FiBarChart as any} />}
          variant="outline"
          onClick={() => navigate('/analytics')}
        >
          View Analytics
        </Button>
      </HStack>

      {/* Stats Grid */}
      <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }} gap={6}>
        {stats.map((stat, index) => (
          <GridItem key={index}>
            <StatCard {...stat} />
          </GridItem>
        ))}
      </Grid>

      {/* Main Content Grid */}
      <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={8}>
        {/* Campaign Progress */}
        <GridItem>
          <Card>
            <CardBody>
              <Heading size="md" mb={6} color="gray.800">
                Active Campaigns
              </Heading>
              <VStack spacing={6} align="stretch">
                {campaignProgress.map((campaign, index) => (
                  <Box key={index}>
                    <HStack justify="space-between" mb={2}>
                      <Text fontWeight="medium" color="gray.700">
                        {campaign.name}
                      </Text>
                      <Text fontSize="sm" color="gray.500">
                        {campaign.sent}/{campaign.total}
                      </Text>
                    </HStack>
                    <Progress
                      value={campaign.progress}
                      colorScheme="brand"
                      borderRadius="full"
                      size="sm"
                    />
                    <Text fontSize="xs" color="gray.500" mt={1}>
                      {campaign.progress}% complete
                    </Text>
                  </Box>
                ))}
              </VStack>
            </CardBody>
          </Card>
        </GridItem>

        {/* Recent Activity */}
        <GridItem>
          <Card>
            <CardBody>
              <Heading size="md" mb={6} color="gray.800">
                Recent Activity
              </Heading>
              <VStack spacing={3} align="stretch">
                {dashboardData?.recentActivity?.map((activity: any, index: number) => (
                  <RecentActivity 
                    key={index} 
                    type={activity.type} 
                    title={activity.title} 
                    description={activity.description} 
                    time={activity.time} 
                    status={activity.status} 
                  />
                )) || (
                  <Text color="gray.500" textAlign="center" py={4}>
                    No recent activity
                  </Text>
                )}
              </VStack>
            </CardBody>
          </Card>
        </GridItem>
      </Grid>

      {/* Performance Chart Placeholder */}
      <Card>
        <CardBody>
          <Heading size="md" mb={6} color="gray.800">
            Email Performance Over Time
          </Heading>
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
                Chart Component Coming Soon
              </Text>
              <Text color="gray.400" fontSize="sm">
                Integration with charting library in progress
              </Text>
            </VStack>
          </Box>
        </CardBody>
      </Card>
    </VStack>
  );
};
