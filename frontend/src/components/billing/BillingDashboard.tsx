import React, { useState, useEffect } from 'react';
import {
  Box,
  VStack,
  HStack,
  Text,
  Button,
  Card,
  CardBody,
  CardHeader,
  Badge,
  Progress,
  Grid,
  GridItem,
  useColorModeValue,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  ModalCloseButton,
  useDisclosure,
  Divider,
  Icon,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
  StatArrow,
} from '@chakra-ui/react';
import {
  FiDollarSign,
  FiCreditCard,
  FiCalendar,
  FiDownload,
  FiAlertCircle,
  FiCheckCircle,
  FiXCircle,
  FiClock,
} from 'react-icons/fi';
import { useAppStore } from '../../store/useAppStore';

interface BillingPlan {
  id: string;
  name: string;
  price: number;
  currency: string;
  interval: string;
  features: string[];
  priceId?: string;
}

interface BillingHistory {
  id: string;
  amount: number;
  currency: string;
  status: string;
  created: number;
  periodStart: number;
  periodEnd: number;
}

interface SubscriptionStatus {
  status: string;
}

interface UpcomingInvoice {
  amount: number;
  currency: string;
  periodStart: number;
  periodEnd: number;
}

export const BillingDashboard: React.FC = () => {
  const { currentTenant } = useAppStore();
  const toast = useToast();
  const cardBg = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.600');
  
  const [plans, setPlans] = useState<BillingPlan[]>([]);
  const [billingHistory, setBillingHistory] = useState<BillingHistory[]>([]);
  const [subscriptionStatus, setSubscriptionStatus] = useState<SubscriptionStatus | null>(null);
  const [upcomingInvoice, setUpcomingInvoice] = useState<UpcomingInvoice | null>(null);
  const [loading, setLoading] = useState(false);
  
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [selectedPlan, setSelectedPlan] = useState<BillingPlan | null>(null);

  useEffect(() => {
    loadBillingData();
  }, []);

  const loadBillingData = async () => {
    setLoading(true);
    try {
      // Load plans, billing history, subscription status, and upcoming invoice
      // This would be replaced with actual API calls
      setPlans([
        {
          id: 'free',
          name: 'Free',
          price: 0,
          currency: 'usd',
          interval: 'month',
          features: [
            '5 users',
            '10 campaigns/month',
            '1,000 emails/month',
            '50 SMS/month',
            '25 WhatsApp/month',
            '100 MB storage'
          ]
        },
        {
          id: 'starter',
          name: 'Starter',
          price: 29,
          currency: 'usd',
          interval: 'month',
          priceId: 'price_starter_monthly',
          features: [
            '10 users',
            '50 campaigns/month',
            '10,000 emails/month',
            '500 SMS/month',
            '100 WhatsApp/month',
            '1 GB storage',
            'Email support'
          ]
        },
        {
          id: 'professional',
          name: 'Professional',
          price: 99,
          currency: 'usd',
          interval: 'month',
          priceId: 'price_professional_monthly',
          features: [
            '25 users',
            '200 campaigns/month',
            '50,000 emails/month',
            '2,000 SMS/month',
            '500 WhatsApp/month',
            '5 GB storage',
            'Priority support',
            'Advanced analytics'
          ]
        },
        {
          id: 'enterprise',
          name: 'Enterprise',
          price: 299,
          currency: 'usd',
          interval: 'month',
          priceId: 'price_enterprise_monthly',
          features: [
            '100 users',
            '1,000 campaigns/month',
            '200,000 emails/month',
            '10,000 SMS/month',
            '2,000 WhatsApp/month',
            '50 GB storage',
            '24/7 support',
            'Custom integrations',
            'Dedicated account manager'
          ]
        }
      ]);

      setBillingHistory([
        {
          id: 'inv_123',
          amount: 9900,
          currency: 'usd',
          status: 'paid',
          created: Date.now() - 30 * 24 * 60 * 60 * 1000,
          periodStart: Date.now() - 30 * 24 * 60 * 60 * 1000,
          periodEnd: Date.now()
        }
      ]);

      setSubscriptionStatus({ status: 'active' });
      setUpcomingInvoice({
        amount: 9900,
        currency: 'usd',
        periodStart: Date.now(),
        periodEnd: Date.now() + 30 * 24 * 60 * 60 * 1000
      });

    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to load billing data',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUpgradePlan = async (plan: BillingPlan) => {
    setSelectedPlan(plan);
    onOpen();
  };

  const confirmUpgrade = async () => {
    if (!selectedPlan) return;

    setLoading(true);
    try {
      // Create checkout session
      const response = await fetch('/api/billing/checkout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ planId: selectedPlan.id }),
      });

      if (response.ok) {
        const data = await response.json();
        // Redirect to Stripe checkout
        window.location.href = data.data.url;
      } else {
        throw new Error('Failed to create checkout session');
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to upgrade plan. Please try again.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
      onClose();
    }
  };

  const handleManageBilling = async () => {
    try {
      const response = await fetch('/api/billing/portal', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        window.location.href = data.data.url;
      } else {
        throw new Error('Failed to create customer portal session');
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to open billing portal. Please try again.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active': return 'green';
      case 'past_due': return 'yellow';
      case 'canceled': return 'red';
      case 'unpaid': return 'red';
      default: return 'gray';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'active': return FiCheckCircle;
      case 'past_due': return FiAlertCircle;
      case 'canceled': return FiXCircle;
      case 'unpaid': return FiXCircle;
      default: return FiClock;
    }
  };

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        {/* Header */}
        <Box>
          <Text fontSize="2xl" fontWeight="bold" mb={2}>
            Billing & Subscription
          </Text>
          <Text color="gray.600">
            Manage your subscription, billing, and payment methods
          </Text>
        </Box>

        {/* Current Plan Status */}
        {currentTenant && (
          <Card bg={cardBg} borderColor={borderColor}>
            <CardBody>
              <HStack justify="space-between" align="start">
                <VStack align="start" spacing={2}>
                  <Text fontSize="lg" fontWeight="semibold">
                    Current Plan: {currentTenant.plan}
                  </Text>
                  <HStack>
                    <Badge colorScheme={getStatusColor(subscriptionStatus?.status || 'active')} size="lg">
                      {subscriptionStatus?.status || 'active'}
                    </Badge>
                    <Icon as={getStatusIcon(subscriptionStatus?.status || 'active')} />
                  </HStack>
                  {currentTenant.trialEndsAt && (
                    <Text fontSize="sm" color="gray.500">
                      Trial ends: {new Date(currentTenant.trialEndsAt).toLocaleDateString()}
                    </Text>
                  )}
                </VStack>
                <Button
                  leftIcon={<FiCreditCard />}
                  colorScheme="blue"
                  onClick={handleManageBilling}
                >
                  Manage Billing
                </Button>
              </HStack>
            </CardBody>
          </Card>
        )}

        {/* Upcoming Invoice */}
        {upcomingInvoice && (
          <Card bg={cardBg} borderColor={borderColor}>
            <CardHeader>
              <Text fontSize="lg" fontWeight="semibold">
                Upcoming Invoice
              </Text>
            </CardHeader>
            <CardBody>
              <HStack justify="space-between">
                <VStack align="start" spacing={1}>
                  <Text fontSize="sm" color="gray.500">Amount Due</Text>
                  <Text fontSize="2xl" fontWeight="bold">
                    ${(upcomingInvoice.amount / 100).toFixed(2)} {upcomingInvoice.currency.toUpperCase()}
                  </Text>
                </VStack>
                <VStack align="end" spacing={1}>
                  <Text fontSize="sm" color="gray.500">Due Date</Text>
                  <Text fontSize="lg" fontWeight="medium">
                    {new Date(upcomingInvoice.periodEnd).toLocaleDateString()}
                  </Text>
                </VStack>
              </HStack>
            </CardBody>
          </Card>
        )}

        {/* Available Plans */}
        <Box>
          <Text fontSize="lg" fontWeight="semibold" mb={4}>
            Available Plans
          </Text>
          <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }} gap={6}>
            {plans.map((plan) => (
              <Card
                key={plan.id}
                bg={cardBg}
                borderColor={borderColor}
                borderWidth={currentTenant?.plan === plan.id ? 2 : 1}
                borderColor={currentTenant?.plan === plan.id ? 'blue.500' : borderColor}
              >
                <CardHeader>
                  <VStack spacing={2}>
                    <Text fontSize="lg" fontWeight="bold">
                      {plan.name}
                    </Text>
                    <HStack>
                      <Text fontSize="3xl" fontWeight="bold">
                        ${plan.price}
                      </Text>
                      <Text color="gray.500">/{plan.interval}</Text>
                    </HStack>
                  </VStack>
                </CardHeader>
                <CardBody>
                  <VStack spacing={4} align="stretch">
                    <VStack spacing={2} align="start">
                      {plan.features.map((feature, index) => (
                        <HStack key={index} spacing={2}>
                          <Icon as={FiCheckCircle} color="green.500" boxSize={4} />
                          <Text fontSize="sm">{feature}</Text>
                        </HStack>
                      ))}
                    </VStack>
                    <Button
                      colorScheme={currentTenant?.plan === plan.id ? 'gray' : 'blue'}
                      variant={currentTenant?.plan === plan.id ? 'outline' : 'solid'}
                      isDisabled={currentTenant?.plan === plan.id}
                      onClick={() => handleUpgradePlan(plan)}
                    >
                      {currentTenant?.plan === plan.id ? 'Current Plan' : 'Upgrade'}
                    </Button>
                  </VStack>
                </CardBody>
              </Card>
            ))}
          </Grid>
        </Box>

        {/* Billing History */}
        <Card bg={cardBg} borderColor={borderColor}>
          <CardHeader>
            <HStack justify="space-between">
              <Text fontSize="lg" fontWeight="semibold">
                Billing History
              </Text>
              <Button
                leftIcon={<FiDownload />}
                variant="outline"
                size="sm"
              >
                Download
              </Button>
            </HStack>
          </CardHeader>
          <CardBody>
            <VStack spacing={4} align="stretch">
              {billingHistory.map((invoice) => (
                <Box key={invoice.id} p={4} borderWidth={1} borderRadius="md" borderColor={borderColor}>
                  <HStack justify="space-between">
                    <VStack align="start" spacing={1}>
                      <Text fontWeight="medium">Invoice #{invoice.id}</Text>
                      <Text fontSize="sm" color="gray.500">
                        {new Date(invoice.created).toLocaleDateString()}
                      </Text>
                    </VStack>
                    <VStack align="end" spacing={1}>
                      <Text fontWeight="bold">
                        ${(invoice.amount / 100).toFixed(2)} {invoice.currency.toUpperCase()}
                      </Text>
                      <Badge colorScheme={getStatusColor(invoice.status)}>
                        {invoice.status}
                      </Badge>
                    </VStack>
                  </HStack>
                </Box>
              ))}
            </VStack>
          </CardBody>
        </Card>

        {/* Upgrade Confirmation Modal */}
        <Modal isOpen={isOpen} onClose={onClose}>
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>Upgrade to {selectedPlan?.name}</ModalHeader>
            <ModalCloseButton />
            <ModalBody>
              <VStack spacing={4} align="stretch">
                <Text>
                  You're about to upgrade to the <strong>{selectedPlan?.name}</strong> plan for{' '}
                  <strong>${selectedPlan?.price}/{selectedPlan?.interval}</strong>.
                </Text>
                <Text fontSize="sm" color="gray.600">
                  This will give you access to:
                </Text>
                <VStack spacing={2} align="start">
                  {selectedPlan?.features.map((feature, index) => (
                    <HStack key={index} spacing={2}>
                      <Icon as={FiCheckCircle} color="green.500" boxSize={4} />
                      <Text fontSize="sm">{feature}</Text>
                    </HStack>
                  ))}
                </VStack>
                <Text fontSize="sm" color="gray.600">
                  You'll be redirected to Stripe to complete the payment.
                </Text>
              </VStack>
            </ModalBody>
            <ModalFooter>
              <Button variant="outline" mr={3} onClick={onClose}>
                Cancel
              </Button>
              <Button colorScheme="blue" onClick={confirmUpgrade} isLoading={loading}>
                Upgrade Now
              </Button>
            </ModalFooter>
          </ModalContent>
        </Modal>
      </VStack>
    </Box>
  );
};
