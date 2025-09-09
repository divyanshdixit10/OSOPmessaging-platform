import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Button,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Badge,
  Progress,
  useColorModeValue,
  Flex,
  Spacer,
  Divider,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  FormControl,
  FormLabel,
  Input,
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
  useDisclosure,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
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
  Spinner,
} from '@chakra-ui/react';
import { IconType } from 'react-icons';
import {
  FiPlay,
  FiPause,
  FiSquare,
  FiRefreshCw,
  FiClock,
  FiSend,
  FiCalendar,
  FiBarChart,
  FiUsers,
  FiMail,
  FiCheckCircle,
  FiXCircle,
  FiAlertCircle,
} from 'react-icons/fi';
import CampaignService, { 
  CampaignProgress, 
  CampaignAnalytics,
  SendCampaignRequest,
  ScheduleCampaignRequest 
} from '../api/campaignService';
import { useWebSocketSafe } from '../pages/AnalyticsPage';

interface CampaignCardProps {
  campaign: {
    id: number;
    name: string;
    description: string;
    status: string;
    totalRecipients: number;
    createdAt: string;
  };
  progress?: CampaignProgress;
  onSend: (campaignId: number) => void;
  onSchedule: (campaignId: number) => void;
  onPause: (campaignId: number) => void;
  onResume: (campaignId: number) => void;
  onCancel: (campaignId: number) => void;
  onRetry: (campaignId: number) => void;
  onViewAnalytics: (campaignId: number) => void;
}

const CampaignCard: React.FC<CampaignCardProps> = ({
  campaign,
  progress,
  onSend,
  onSchedule,
  onPause,
  onResume,
  onCancel,
  onRetry,
  onViewAnalytics
}) => {
  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'running': return 'green';
      case 'completed': return 'blue';
      case 'scheduled': return 'yellow';
      case 'draft': return 'gray';
      case 'paused': return 'orange';
      case 'failed': return 'red';
      case 'cancelled': return 'red';
      default: return 'gray';
    }
  };

  const getStatusIcon = (status: string): IconType => {
    switch (status.toLowerCase()) {
      case 'running': return FiPlay;
      case 'completed': return FiCheckCircle;
      case 'scheduled': return FiClock;
      case 'draft': return FiMail;
      case 'paused': return FiPause;
      case 'failed': return FiXCircle;
      case 'cancelled': return FiSquare;
      default: return FiMail;
    }
  };

  const canSend = campaign.status === 'DRAFT';
  const canSchedule = campaign.status === 'DRAFT';
  const canPause = campaign.status === 'RUNNING';
  const canResume = campaign.status === 'PAUSED';
  const canCancel = ['RUNNING', 'SCHEDULED', 'PAUSED'].includes(campaign.status);
  const canRetry = campaign.status === 'FAILED' || campaign.status === 'COMPLETED';
  const canViewAnalytics = ['COMPLETED', 'FAILED', 'CANCELLED'].includes(campaign.status);

  return (
    <Card>
      <CardHeader>
        <HStack justify="space-between" align="flex-start">
          <VStack align="flex-start" spacing={2}>
            <Heading size="md" color="gray.800">
              {campaign.name}
            </Heading>
            <Text fontSize="sm" color="gray.600">
              {campaign.description}
            </Text>
            <HStack spacing={2}>
              <Badge colorScheme={getStatusColor(campaign.status)} size="sm">
                <Icon as={getStatusIcon(campaign.status) as any} mr={1} />
                {campaign.status}
              </Badge>
              <Text fontSize="xs" color="gray.500">
                {campaign.totalRecipients} recipients
              </Text>
            </HStack>
          </VStack>
        </HStack>
      </CardHeader>
      
      <CardBody>
        {/* Progress Section */}
        {progress && (
          <VStack spacing={4} align="stretch" mb={4}>
            <Box>
              <HStack justify="space-between" mb={2}>
                <Text fontSize="sm" fontWeight="medium">Progress</Text>
                <Text fontSize="sm" color="gray.600">
                  {progress.emailsSent} / {progress.totalRecipients} sent
                </Text>
              </HStack>
              <Progress 
                value={progress.progressPercentage} 
                colorScheme="blue" 
                size="lg" 
                borderRadius="full"
              />
              <Text fontSize="xs" color="gray.500" mt={1}>
                {progress.progressPercentage.toFixed(1)}% complete
              </Text>
            </Box>

            {/* Stats */}
            <HStack spacing={4} justify="space-between">
              <Stat size="sm">
                <StatLabel>Success</StatLabel>
                <StatNumber color="green.500">{progress.emailsSuccess}</StatNumber>
                <StatHelpText>
                  <StatArrow type="increase" />
                  {progress.successRate.toFixed(1)}%
                </StatHelpText>
              </Stat>
              <Stat size="sm">
                <StatLabel>Failed</StatLabel>
                <StatNumber color="red.500">{progress.emailsFailed}</StatNumber>
                <StatHelpText>
                  <StatArrow type="decrease" />
                  {progress.failureRate.toFixed(1)}%
                </StatHelpText>
              </Stat>
              <Stat size="sm">
                <StatLabel>Batch</StatLabel>
                <StatNumber color="blue.500">{progress.currentBatchNumber}/{progress.totalBatches}</StatNumber>
              </Stat>
            </HStack>

            {/* Error Message */}
            {progress.errorMessage && (
              <Alert status="error" size="sm">
                <AlertIcon />
                <AlertDescription fontSize="sm">
                  {progress.errorMessage}
                </AlertDescription>
              </Alert>
            )}
          </VStack>
        )}

        <Divider mb={4} />

        {/* Action Buttons */}
        <HStack spacing={2} wrap="wrap">
          {canSend && (
            <Button
              size="sm"
              colorScheme="green"
              leftIcon={<Icon as={FiSend as any} />}
              onClick={() => onSend(campaign.id)}
            >
              Send Now
            </Button>
          )}
          
          {canSchedule && (
            <Button
              size="sm"
              colorScheme="blue"
              leftIcon={<Icon as={FiCalendar as any} />}
              onClick={() => onSchedule(campaign.id)}
            >
              Schedule
            </Button>
          )}
          
          {canPause && (
            <Button
              size="sm"
              colorScheme="orange"
              leftIcon={<Icon as={FiPause as any} />}
              onClick={() => onPause(campaign.id)}
            >
              Pause
            </Button>
          )}
          
          {canResume && (
            <Button
              size="sm"
              colorScheme="green"
              leftIcon={<Icon as={FiPlay as any} />}
              onClick={() => onResume(campaign.id)}
            >
              Resume
            </Button>
          )}
          
          {canCancel && (
            <Button
              size="sm"
              colorScheme="red"
              leftIcon={<Icon as={FiSquare as any} />}
              onClick={() => onCancel(campaign.id)}
            >
              Cancel
            </Button>
          )}
          
          {canRetry && (
            <Button
              size="sm"
              colorScheme="purple"
              leftIcon={<Icon as={FiRefreshCw as any} />}
              onClick={() => onRetry(campaign.id)}
            >
              Retry Failed
            </Button>
          )}
          
          {canViewAnalytics && (
            <Button
              size="sm"
              variant="outline"
              leftIcon={<Icon as={FiBarChart as any} />}
              onClick={() => onViewAnalytics(campaign.id)}
            >
              Analytics
            </Button>
          )}
        </HStack>
      </CardBody>
    </Card>
  );
};

export const EnhancedCampaignPage: React.FC = () => {
  const [campaigns, setCampaigns] = useState<any[]>([]);
  const [campaignProgress, setCampaignProgress] = useState<Map<number, CampaignProgress>>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCampaign, setSelectedCampaign] = useState<number | null>(null);
  const [analytics, setAnalytics] = useState<CampaignAnalytics | null>(null);
  
  const toast = useToast();
  const { connected, subscribe } = useWebSocketSafe();
  
  const { isOpen: isScheduleOpen, onOpen: onScheduleOpen, onClose: onScheduleClose } = useDisclosure();
  const { isOpen: isAnalyticsOpen, onOpen: onAnalyticsOpen, onClose: onAnalyticsClose } = useDisclosure();
  
  const [scheduleForm, setScheduleForm] = useState({
    scheduledTime: '',
    batchSize: 50,
    rateLimitPerMinute: 100
  });

  // WebSocket subscription for real-time progress updates
  useEffect(() => {
    if (connected) {
      subscribe('/topic/campaign_progress', (message: any) => {
        if (message.type === 'campaign_progress_update' && message.data) {
          setCampaignProgress(prev => {
            const newMap = new Map(prev);
            newMap.set(message.campaignId, message.data);
            return newMap;
          });
        }
      });
    }
  }, [connected, subscribe]);

  // Fetch campaigns and progress
  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Mock campaigns data - replace with actual API call
      const mockCampaigns = [
        {
          id: 1,
          name: 'Welcome Series - Day 1',
          description: 'Welcome new subscribers to our platform',
          status: 'RUNNING',
          totalRecipients: 100,
          createdAt: '2024-12-08T10:00:00Z'
        },
        {
          id: 2,
          name: 'Monthly Newsletter - December 2024',
          description: 'Monthly newsletter with updates and news',
          status: 'COMPLETED',
          totalRecipients: 150,
          createdAt: '2024-12-01T10:00:00Z'
        },
        {
          id: 3,
          name: 'Product Launch Announcement',
          description: 'Announcement for new product features',
          status: 'DRAFT',
          totalRecipients: 200,
          createdAt: '2024-12-07T10:00:00Z'
        }
      ];
      
      setCampaigns(mockCampaigns);
      
      // Fetch progress for each campaign
      for (const campaign of mockCampaigns) {
        try {
          const progress = await CampaignService.getCampaignProgress(campaign.id);
          setCampaignProgress(prev => {
            const newMap = new Map(prev);
            newMap.set(campaign.id, progress);
            return newMap;
          });
        } catch (err) {
          console.warn(`Failed to fetch progress for campaign ${campaign.id}:`, err);
        }
      }
      
    } catch (err) {
      console.error('Error fetching campaigns:', err);
      setError('Failed to load campaigns');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // Campaign actions
  const handleSend = async (campaignId: number) => {
    try {
      const request: SendCampaignRequest = {
        campaignId,
        userId: 'current-user', // Replace with actual user ID
        batchSize: 50,
        rateLimitPerMinute: 100
      };
      
      const result = await CampaignService.sendCampaign(request);
      
      if (result.success) {
        toast({
          title: 'Campaign Started',
          description: result.message,
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
        fetchData(); // Refresh data
      } else {
        throw new Error(result.message);
      }
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to start campaign',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleSchedule = (campaignId: number) => {
    setSelectedCampaign(campaignId);
    onScheduleOpen();
  };

  const handleScheduleSubmit = async () => {
    if (!selectedCampaign) return;
    
    try {
      const request: ScheduleCampaignRequest = {
        campaignId: selectedCampaign,
        userId: 'current-user', // Replace with actual user ID
        scheduledTime: scheduleForm.scheduledTime,
        batchSize: scheduleForm.batchSize,
        rateLimitPerMinute: scheduleForm.rateLimitPerMinute
      };
      
      const result = await CampaignService.scheduleCampaign(request);
      
      if (result.success) {
        toast({
          title: 'Campaign Scheduled',
          description: result.message,
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
        onScheduleClose();
        fetchData(); // Refresh data
      } else {
        throw new Error(result.message);
      }
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to schedule campaign',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handlePause = async (campaignId: number) => {
    try {
      const result = await CampaignService.pauseCampaign(campaignId);
      
      if (result.success) {
        toast({
          title: 'Campaign Paused',
          description: result.message,
          status: 'info',
          duration: 5000,
          isClosable: true,
        });
        fetchData();
      } else {
        throw new Error(result.message);
      }
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to pause campaign',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleResume = async (campaignId: number) => {
    try {
      const result = await CampaignService.resumeCampaign(campaignId);
      
      if (result.success) {
        toast({
          title: 'Campaign Resumed',
          description: result.message,
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
        fetchData();
      } else {
        throw new Error(result.message);
      }
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to resume campaign',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleCancel = async (campaignId: number) => {
    try {
      const result = await CampaignService.cancelCampaign(campaignId);
      
      if (result.success) {
        toast({
          title: 'Campaign Cancelled',
          description: result.message,
          status: 'warning',
          duration: 5000,
          isClosable: true,
        });
        fetchData();
      } else {
        throw new Error(result.message);
      }
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to cancel campaign',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleRetry = async (campaignId: number) => {
    try {
      const result = await CampaignService.retryFailedEmails(campaignId);
      
      if (result.success) {
        toast({
          title: 'Retry Started',
          description: result.message,
          status: 'info',
          duration: 5000,
          isClosable: true,
        });
        fetchData();
      } else {
        throw new Error(result.message);
      }
    } catch (err) {
      toast({
        title: 'Error',
        description: err instanceof Error ? err.message : 'Failed to retry emails',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleViewAnalytics = async (campaignId: number) => {
    try {
      const analyticsData = await CampaignService.getCampaignAnalytics(campaignId);
      setAnalytics(analyticsData);
      onAnalyticsOpen();
    } catch (err) {
      toast({
        title: 'Error',
        description: 'Failed to load campaign analytics',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  if (loading) {
    return (
      <VStack spacing={4} align="center" justify="center" h="400px">
        <Spinner size="xl" color="blue.500" />
        <Text>Loading campaigns...</Text>
      </VStack>
    );
  }

  if (error) {
    return (
      <Alert status="error">
        <AlertIcon />
        <AlertTitle>Error loading campaigns!</AlertTitle>
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    );
  }

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <HStack justify="space-between" align="flex-start" mb={2}>
          <Box>
            <Heading size="lg" color="gray.800" mb={2}>
              Enhanced Campaign Management
            </Heading>
            <Text color="gray.600">
              Manage your email campaigns with real-time progress tracking, scheduling, and advanced controls.
            </Text>
          </Box>
          <HStack spacing={2}>
            <Box
              w={2}
              h={2}
              borderRadius="full"
              bg={connected ? 'green.500' : 'red.500'}
            />
            <Text fontSize="xs" color="gray.500">
              {connected ? 'Live Updates' : 'Offline'}
            </Text>
          </HStack>
        </HStack>
      </Box>

      {/* Campaigns Grid */}
      <VStack spacing={6} align="stretch">
        {campaigns.map((campaign) => (
          <CampaignCard
            key={campaign.id}
            campaign={campaign}
            progress={campaignProgress.get(campaign.id)}
            onSend={handleSend}
            onSchedule={handleSchedule}
            onPause={handlePause}
            onResume={handleResume}
            onCancel={handleCancel}
            onRetry={handleRetry}
            onViewAnalytics={handleViewAnalytics}
          />
        ))}
      </VStack>

      {/* Schedule Modal */}
      <Modal isOpen={isScheduleOpen} onClose={onScheduleClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Schedule Campaign</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <VStack spacing={4}>
              <FormControl>
                <FormLabel>Scheduled Time</FormLabel>
                <Input
                  type="datetime-local"
                  value={scheduleForm.scheduledTime}
                  onChange={(e) => setScheduleForm(prev => ({ ...prev, scheduledTime: e.target.value }))}
                />
              </FormControl>
              
              <FormControl>
                <FormLabel>Batch Size</FormLabel>
                <NumberInput
                  value={scheduleForm.batchSize}
                  onChange={(value) => setScheduleForm(prev => ({ ...prev, batchSize: parseInt(value) }))}
                  min={1}
                  max={1000}
                >
                  <NumberInputField />
                  <NumberInputStepper>
                    <NumberIncrementStepper />
                    <NumberDecrementStepper />
                  </NumberInputStepper>
                </NumberInput>
              </FormControl>
              
              <FormControl>
                <FormLabel>Rate Limit (emails per minute)</FormLabel>
                <NumberInput
                  value={scheduleForm.rateLimitPerMinute}
                  onChange={(value) => setScheduleForm(prev => ({ ...prev, rateLimitPerMinute: parseInt(value) }))}
                  min={1}
                  max={1000}
                >
                  <NumberInputField />
                  <NumberInputStepper>
                    <NumberIncrementStepper />
                    <NumberDecrementStepper />
                  </NumberInputStepper>
                </NumberInput>
              </FormControl>
              
              <HStack spacing={4} w="full">
                <Button onClick={onScheduleClose} flex={1}>
                  Cancel
                </Button>
                <Button colorScheme="blue" onClick={handleScheduleSubmit} flex={1}>
                  Schedule
                </Button>
              </HStack>
            </VStack>
          </ModalBody>
        </ModalContent>
      </Modal>

      {/* Analytics Modal */}
      <Modal isOpen={isAnalyticsOpen} onClose={onAnalyticsClose} size="xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Campaign Analytics</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            {analytics && (
              <VStack spacing={6} align="stretch">
                {/* Overview Stats */}
                <HStack spacing={4}>
                  <Stat>
                    <StatLabel>Open Rate</StatLabel>
                    <StatNumber color="green.500">{analytics.openRate.toFixed(1)}%</StatNumber>
                  </Stat>
                  <Stat>
                    <StatLabel>Click Rate</StatLabel>
                    <StatNumber color="blue.500">{analytics.clickRate.toFixed(1)}%</StatNumber>
                  </Stat>
                  <Stat>
                    <StatLabel>Bounce Rate</StatLabel>
                    <StatNumber color="red.500">{analytics.bounceRate.toFixed(1)}%</StatNumber>
                  </Stat>
                </HStack>
                
                {/* Detailed Stats */}
                <Table variant="simple" size="sm">
                  <Thead>
                    <Tr>
                      <Th>Metric</Th>
                      <Th>Count</Th>
                      <Th>Rate</Th>
                    </Tr>
                  </Thead>
                  <Tbody>
                    <Tr>
                      <Td>Total Recipients</Td>
                      <Td>{analytics.totalRecipients}</Td>
                      <Td>-</Td>
                    </Tr>
                    <Tr>
                      <Td>Emails Sent</Td>
                      <Td>{analytics.sentCount}</Td>
                      <Td>{((analytics.sentCount / analytics.totalRecipients) * 100).toFixed(1)}%</Td>
                    </Tr>
                    <Tr>
                      <Td>Emails Opened</Td>
                      <Td>{analytics.openedCount}</Td>
                      <Td>{analytics.openRate.toFixed(1)}%</Td>
                    </Tr>
                    <Tr>
                      <Td>Emails Clicked</Td>
                      <Td>{analytics.clickedCount}</Td>
                      <Td>{analytics.clickRate.toFixed(1)}%</Td>
                    </Tr>
                    <Tr>
                      <Td>Emails Bounced</Td>
                      <Td>{analytics.bouncedCount}</Td>
                      <Td>{analytics.bounceRate.toFixed(1)}%</Td>
                    </Tr>
                  </Tbody>
                </Table>
              </VStack>
            )}
          </ModalBody>
        </ModalContent>
      </Modal>
    </VStack>
  );
};
