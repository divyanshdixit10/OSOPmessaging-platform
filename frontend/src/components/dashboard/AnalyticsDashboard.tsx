import React from 'react';
import {
  Box,
  Grid,
  GridItem,
  Card,
  CardBody,
  Text,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
  StatArrow,
  VStack,
  HStack,
  Icon,
  Progress,
  Badge,
  useColorModeValue,
} from '@chakra-ui/react';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import {
  FiMail,
  FiUsers,
  FiTrendingUp,
  FiDollarSign,
  FiEye,
  FiMousePointer,
  FiX,
  FiDownload,
} from 'react-icons/fi';
import { useAppStore } from '../../store/useAppStore';

interface AnalyticsData {
  totalEmails: number;
  totalCampaigns: number;
  totalSubscribers: number;
  totalRevenue: number;
  emailStats: {
    sent: number;
    delivered: number;
    opened: number;
    clicked: number;
    bounced: number;
    unsubscribed: number;
  };
  monthlyStats: Array<{
    month: string;
    emails: number;
    opens: number;
    clicks: number;
    revenue: number;
  }>;
  channelStats: Array<{
    channel: string;
    count: number;
    percentage: number;
  }>;
  topCampaigns: Array<{
    name: string;
    sent: number;
    opened: number;
    clicked: number;
    openRate: number;
    clickRate: number;
  }>;
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

export const AnalyticsDashboard: React.FC = () => {
  const { currentTenant, usageStats } = useAppStore();
  const cardBg = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.600');

  // Mock data - replace with real API calls
  const analyticsData: AnalyticsData = {
    totalEmails: 12543,
    totalCampaigns: 89,
    totalSubscribers: 2341,
    totalRevenue: 12500,
    emailStats: {
      sent: 12543,
      delivered: 12100,
      opened: 8900,
      clicked: 2100,
      bounced: 443,
      unsubscribed: 45,
    },
    monthlyStats: [
      { month: 'Jan', emails: 1200, opens: 800, clicks: 200, revenue: 1200 },
      { month: 'Feb', emails: 1500, opens: 1000, clicks: 250, revenue: 1500 },
      { month: 'Mar', emails: 1800, opens: 1200, clicks: 300, revenue: 1800 },
      { month: 'Apr', emails: 2100, opens: 1400, clicks: 350, revenue: 2100 },
      { month: 'May', emails: 2400, opens: 1600, clicks: 400, revenue: 2400 },
      { month: 'Jun', emails: 2700, opens: 1800, clicks: 450, revenue: 2700 },
    ],
    channelStats: [
      { channel: 'Email', count: 12543, percentage: 85 },
      { channel: 'SMS', count: 1200, percentage: 8 },
      { channel: 'WhatsApp', count: 800, percentage: 5 },
      { channel: 'Push', count: 200, percentage: 2 },
    ],
    topCampaigns: [
      { name: 'Welcome Series', sent: 500, opened: 400, clicked: 100, openRate: 80, clickRate: 25 },
      { name: 'Newsletter', sent: 1000, opened: 700, clicked: 150, openRate: 70, clickRate: 21 },
      { name: 'Promotional', sent: 800, opened: 500, clicked: 80, openRate: 62, clickRate: 16 },
    ],
  };

  const StatCard: React.FC<{
    title: string;
    value: string | number;
    change?: number;
    icon: React.ElementType;
    color: string;
  }> = ({ title, value, change, icon, color }) => (
    <Card bg={cardBg} borderColor={borderColor}>
      <CardBody>
        <HStack justify="space-between" align="start">
          <VStack align="start" spacing={2}>
            <Text fontSize="sm" color="gray.500" fontWeight="medium">
              {title}
            </Text>
            <Stat>
              <StatNumber fontSize="2xl" fontWeight="bold">
                {typeof value === 'number' ? value.toLocaleString() : value}
              </StatNumber>
              {change !== undefined && (
                <StatHelpText>
                  <StatArrow type={change >= 0 ? 'increase' : 'decrease'} />
                  {Math.abs(change)}%
                </StatHelpText>
              )}
            </Stat>
          </VStack>
          <Icon as={icon} boxSize={8} color={color} />
        </HStack>
      </CardBody>
    </Card>
  );

  const QuotaCard: React.FC<{
    title: string;
    used: number;
    limit: number;
    color: string;
  }> = ({ title, used, limit, color }) => {
    const percentage = (used / limit) * 100;
    return (
      <Card bg={cardBg} borderColor={borderColor}>
        <CardBody>
          <VStack align="start" spacing={3}>
            <HStack justify="space-between" w="full">
              <Text fontSize="sm" fontWeight="medium">
                {title}
              </Text>
              <Badge colorScheme={percentage > 90 ? 'red' : percentage > 75 ? 'yellow' : 'green'}>
                {used.toLocaleString()} / {limit.toLocaleString()}
              </Badge>
            </HStack>
            <Progress
              value={percentage}
              size="sm"
              colorScheme={percentage > 90 ? 'red' : percentage > 75 ? 'yellow' : 'green'}
              w="full"
            />
            <Text fontSize="xs" color="gray.500">
              {percentage.toFixed(1)}% used
            </Text>
          </VStack>
        </CardBody>
      </Card>
    );
  };

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        {/* Header */}
        <Box>
          <Text fontSize="2xl" fontWeight="bold" mb={2}>
            Analytics Dashboard
          </Text>
          <Text color="gray.600">
            Welcome back! Here's what's happening with your messaging campaigns.
          </Text>
        </Box>

        {/* Key Metrics */}
        <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }} gap={6}>
          <StatCard
            title="Total Emails Sent"
            value={analyticsData.totalEmails}
            change={12.5}
            icon={FiMail}
            color="blue.500"
          />
          <StatCard
            title="Active Campaigns"
            value={analyticsData.totalCampaigns}
            change={8.2}
            icon={FiTrendingUp}
            color="green.500"
          />
          <StatCard
            title="Total Subscribers"
            value={analyticsData.totalSubscribers}
            change={15.3}
            icon={FiUsers}
            color="purple.500"
          />
          <StatCard
            title="Revenue"
            value={`$${analyticsData.totalRevenue.toLocaleString()}`}
            change={22.1}
            icon={FiDollarSign}
            color="orange.500"
          />
        </Grid>

        {/* Usage Quotas */}
        {usageStats && (
          <Box>
            <Text fontSize="lg" fontWeight="semibold" mb={4}>
              Usage & Quotas
            </Text>
            <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(3, 1fr)' }} gap={4}>
              <QuotaCard
                title="Emails This Month"
                used={usageStats.emailsUsed}
                limit={usageStats.emailsUsed + usageStats.emailsRemaining}
                color="blue"
              />
              <QuotaCard
                title="SMS This Month"
                used={usageStats.smsUsed}
                limit={usageStats.smsUsed + usageStats.smsRemaining}
                color="green"
              />
              <QuotaCard
                title="Storage Used"
                used={Math.round(usageStats.storageUsed / (1024 * 1024))}
                limit={Math.round((usageStats.storageUsed + usageStats.storageRemaining) / (1024 * 1024))}
                color="purple"
              />
            </Grid>
          </Box>
        )}

        {/* Charts */}
        <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={6}>
          {/* Monthly Trends */}
          <Card bg={cardBg} borderColor={borderColor}>
            <CardBody>
              <Text fontSize="lg" fontWeight="semibold" mb={4}>
                Monthly Performance
              </Text>
              <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={analyticsData.monthlyStats}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Area
                    type="monotone"
                    dataKey="emails"
                    stackId="1"
                    stroke="#8884d8"
                    fill="#8884d8"
                    name="Emails Sent"
                  />
                  <Area
                    type="monotone"
                    dataKey="opens"
                    stackId="2"
                    stroke="#82ca9d"
                    fill="#82ca9d"
                    name="Opens"
                  />
                  <Area
                    type="monotone"
                    dataKey="clicks"
                    stackId="3"
                    stroke="#ffc658"
                    fill="#ffc658"
                    name="Clicks"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </CardBody>
          </Card>

          {/* Channel Distribution */}
          <Card bg={cardBg} borderColor={borderColor}>
            <CardBody>
              <Text fontSize="lg" fontWeight="semibold" mb={4}>
                Channel Distribution
              </Text>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={analyticsData.channelStats}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ channel, percentage }) => `${channel} ${percentage}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="count"
                  >
                    {analyticsData.channelStats.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardBody>
          </Card>
        </Grid>

        {/* Email Performance Metrics */}
        <Card bg={cardBg} borderColor={borderColor}>
          <CardBody>
            <Text fontSize="lg" fontWeight="semibold" mb={4}>
              Email Performance Metrics
            </Text>
            <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(3, 1fr)' }} gap={6}>
              <VStack align="start" spacing={2}>
                <HStack>
                  <Icon as={FiMail} color="blue.500" />
                  <Text fontSize="sm" fontWeight="medium">Delivered</Text>
                </HStack>
                <Text fontSize="2xl" fontWeight="bold">
                  {analyticsData.emailStats.delivered.toLocaleString()}
                </Text>
                <Text fontSize="xs" color="gray.500">
                  {((analyticsData.emailStats.delivered / analyticsData.emailStats.sent) * 100).toFixed(1)}% delivery rate
                </Text>
              </VStack>

              <VStack align="start" spacing={2}>
                <HStack>
                  <Icon as={FiEye} color="green.500" />
                  <Text fontSize="sm" fontWeight="medium">Opened</Text>
                </HStack>
                <Text fontSize="2xl" fontWeight="bold">
                  {analyticsData.emailStats.opened.toLocaleString()}
                </Text>
                <Text fontSize="xs" color="gray.500">
                  {((analyticsData.emailStats.opened / analyticsData.emailStats.delivered) * 100).toFixed(1)}% open rate
                </Text>
              </VStack>

              <VStack align="start" spacing={2}>
                <HStack>
                  <Icon as={FiMousePointer} color="purple.500" />
                  <Text fontSize="sm" fontWeight="medium">Clicked</Text>
                </HStack>
                <Text fontSize="2xl" fontWeight="bold">
                  {analyticsData.emailStats.clicked.toLocaleString()}
                </Text>
                <Text fontSize="xs" color="gray.500">
                  {((analyticsData.emailStats.clicked / analyticsData.emailStats.opened) * 100).toFixed(1)}% click rate
                </Text>
              </VStack>

              <VStack align="start" spacing={2}>
                <HStack>
                  <Icon as={FiX} color="red.500" />
                  <Text fontSize="sm" fontWeight="medium">Bounced</Text>
                </HStack>
                <Text fontSize="2xl" fontWeight="bold">
                  {analyticsData.emailStats.bounced.toLocaleString()}
                </Text>
                <Text fontSize="xs" color="gray.500">
                  {((analyticsData.emailStats.bounced / analyticsData.emailStats.sent) * 100).toFixed(1)}% bounce rate
                </Text>
              </VStack>

              <VStack align="start" spacing={2}>
                <HStack>
                  <Icon as={FiDownload} color="orange.500" />
                  <Text fontSize="sm" fontWeight="medium">Unsubscribed</Text>
                </HStack>
                <Text fontSize="2xl" fontWeight="bold">
                  {analyticsData.emailStats.unsubscribed.toLocaleString()}
                </Text>
                <Text fontSize="xs" color="gray.500">
                  {((analyticsData.emailStats.unsubscribed / analyticsData.emailStats.sent) * 100).toFixed(1)}% unsubscribe rate
                </Text>
              </VStack>
            </Grid>
          </CardBody>
        </Card>

        {/* Top Campaigns */}
        <Card bg={cardBg} borderColor={borderColor}>
          <CardBody>
            <Text fontSize="lg" fontWeight="semibold" mb={4}>
              Top Performing Campaigns
            </Text>
            <VStack spacing={4} align="stretch">
              {analyticsData.topCampaigns.map((campaign, index) => (
                <Box key={index} p={4} borderWidth={1} borderRadius="md" borderColor={borderColor}>
                  <HStack justify="space-between" mb={2}>
                    <Text fontWeight="medium">{campaign.name}</Text>
                    <Badge colorScheme="blue">{campaign.sent} sent</Badge>
                  </HStack>
                  <Grid templateColumns="repeat(4, 1fr)" gap={4}>
                    <VStack spacing={1}>
                      <Text fontSize="sm" color="gray.500">Open Rate</Text>
                      <Text fontWeight="bold">{campaign.openRate}%</Text>
                    </VStack>
                    <VStack spacing={1}>
                      <Text fontSize="sm" color="gray.500">Click Rate</Text>
                      <Text fontWeight="bold">{campaign.clickRate}%</Text>
                    </VStack>
                    <VStack spacing={1}>
                      <Text fontSize="sm" color="gray.500">Opens</Text>
                      <Text fontWeight="bold">{campaign.opened}</Text>
                    </VStack>
                    <VStack spacing={1}>
                      <Text fontSize="sm" color="gray.500">Clicks</Text>
                      <Text fontWeight="bold">{campaign.clicked}</Text>
                    </VStack>
                  </Grid>
                </Box>
              ))}
            </VStack>
          </CardBody>
        </Card>
      </VStack>
    </Box>
  );
};
