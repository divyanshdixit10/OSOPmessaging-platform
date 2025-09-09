import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardBody,
  CardHeader,
  FormControl,
  FormLabel,
  Grid,
  Heading,
  Input,
  Select,
  Stack,
  Tab,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  Text,
  useColorModeValue,
  useToast,
} from '@chakra-ui/react';
import { tenantService, Tenant, UpdateTenantRequest } from '../../api/tenantService';
import { useAppStore } from '../../store/useAppStore';

const TenantSettings: React.FC = () => {
  const [tenant, setTenant] = useState<Tenant | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState<UpdateTenantRequest>({});
  const toast = useToast();
  const cardBg = useColorModeValue('white', 'gray.700');
  const { setCurrentTenant } = useAppStore();

  useEffect(() => {
    const loadTenant = async () => {
      try {
        setLoading(true);
        const response = await tenantService.getCurrentTenant();
        if (response.success && response.data) {
          setTenant(response.data);
          setFormData({
            displayName: response.data.displayName,
            description: response.data.description,
            contactEmail: response.data.contactEmail,
            contactPhone: response.data.contactPhone,
            companyName: response.data.companyName,
            companyAddress: response.data.companyAddress,
            logoUrl: response.data.logoUrl,
            primaryColor: response.data.primaryColor,
            secondaryColor: response.data.secondaryColor,
            timezone: response.data.timezone,
            locale: response.data.locale,
          });
        }
      } catch (error) {
        toast({
          title: 'Error loading tenant',
          description: error instanceof Error ? error.message : 'Unknown error',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      } finally {
        setLoading(false);
      }
    };

    loadTenant();
  }, [toast]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!tenant) return;

    try {
      setSaving(true);
      const response = await tenantService.updateTenant(tenant.id, formData);
      if (response.success && response.data) {
        setTenant(response.data);
        setCurrentTenant(response.data);
        toast({
          title: 'Settings updated',
          description: 'Your tenant settings have been updated successfully.',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error saving settings',
        description: error instanceof Error ? error.message : 'Unknown error',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Box textAlign="center" py={10}>
        <Text>Loading tenant settings...</Text>
      </Box>
    );
  }

  if (!tenant) {
    return (
      <Box textAlign="center" py={10}>
        <Text>No tenant found. Please contact support.</Text>
      </Box>
    );
  }

  return (
    <Box p={4}>
      <Heading mb={6}>Tenant Settings</Heading>

      <Tabs variant="enclosed" colorScheme="brand">
        <TabList>
          <Tab>General</Tab>
          <Tab>Branding</Tab>
          <Tab>Subscription</Tab>
          <Tab>Usage</Tab>
        </TabList>

        <TabPanels>
          {/* General Settings */}
          <TabPanel>
            <Card bg={cardBg} shadow="md">
              <CardHeader>
                <Heading size="md">General Information</Heading>
              </CardHeader>
              <CardBody>
                <form onSubmit={handleSubmit}>
                  <Stack spacing={4}>
                    <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)' }} gap={4}>
                      <FormControl>
                        <FormLabel>Tenant Name</FormLabel>
                        <Input value={tenant.name} isReadOnly />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Subdomain</FormLabel>
                        <Input value={tenant.subdomain} isReadOnly />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Display Name</FormLabel>
                        <Input
                          name="displayName"
                          value={formData.displayName || ''}
                          onChange={handleInputChange}
                        />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Contact Email</FormLabel>
                        <Input
                          name="contactEmail"
                          value={formData.contactEmail || ''}
                          onChange={handleInputChange}
                        />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Contact Phone</FormLabel>
                        <Input
                          name="contactPhone"
                          value={formData.contactPhone || ''}
                          onChange={handleInputChange}
                        />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Company Name</FormLabel>
                        <Input
                          name="companyName"
                          value={formData.companyName || ''}
                          onChange={handleInputChange}
                        />
                      </FormControl>
                    </Grid>

                    <FormControl>
                      <FormLabel>Company Address</FormLabel>
                      <Input
                        name="companyAddress"
                        value={formData.companyAddress || ''}
                        onChange={handleInputChange}
                      />
                    </FormControl>

                    <FormControl>
                      <FormLabel>Description</FormLabel>
                      <Input
                        name="description"
                        value={formData.description || ''}
                        onChange={handleInputChange}
                      />
                    </FormControl>

                    <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)' }} gap={4}>
                      <FormControl>
                        <FormLabel>Timezone</FormLabel>
                        <Select
                          name="timezone"
                          value={formData.timezone || 'UTC'}
                          onChange={handleInputChange}
                        >
                          <option value="UTC">UTC</option>
                          <option value="America/New_York">Eastern Time (ET)</option>
                          <option value="America/Chicago">Central Time (CT)</option>
                          <option value="America/Denver">Mountain Time (MT)</option>
                          <option value="America/Los_Angeles">Pacific Time (PT)</option>
                          <option value="Europe/London">London</option>
                          <option value="Europe/Paris">Paris</option>
                          <option value="Asia/Tokyo">Tokyo</option>
                          <option value="Asia/Shanghai">Shanghai</option>
                          <option value="Australia/Sydney">Sydney</option>
                        </Select>
                      </FormControl>

                      <FormControl>
                        <FormLabel>Locale</FormLabel>
                        <Select
                          name="locale"
                          value={formData.locale || 'en_US'}
                          onChange={handleInputChange}
                        >
                          <option value="en_US">English (US)</option>
                          <option value="en_GB">English (UK)</option>
                          <option value="fr_FR">French</option>
                          <option value="de_DE">German</option>
                          <option value="es_ES">Spanish</option>
                          <option value="ja_JP">Japanese</option>
                          <option value="zh_CN">Chinese (Simplified)</option>
                        </Select>
                      </FormControl>
                    </Grid>

                    <Button
                      type="submit"
                      colorScheme="brand"
                      isLoading={saving}
                      alignSelf="flex-end"
                    >
                      Save Changes
                    </Button>
                  </Stack>
                </form>
              </CardBody>
            </Card>
          </TabPanel>

          {/* Branding Settings */}
          <TabPanel>
            <Card bg={cardBg} shadow="md">
              <CardHeader>
                <Heading size="md">Branding</Heading>
              </CardHeader>
              <CardBody>
                <form onSubmit={handleSubmit}>
                  <Stack spacing={4}>
                    <FormControl>
                      <FormLabel>Logo URL</FormLabel>
                      <Input
                        name="logoUrl"
                        value={formData.logoUrl || ''}
                        onChange={handleInputChange}
                      />
                    </FormControl>

                    <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)' }} gap={4}>
                      <FormControl>
                        <FormLabel>Primary Color</FormLabel>
                        <Input
                          name="primaryColor"
                          value={formData.primaryColor || ''}
                          onChange={handleInputChange}
                          type="color"
                        />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Secondary Color</FormLabel>
                        <Input
                          name="secondaryColor"
                          value={formData.secondaryColor || ''}
                          onChange={handleInputChange}
                          type="color"
                        />
                      </FormControl>
                    </Grid>

                    <Button
                      type="submit"
                      colorScheme="brand"
                      isLoading={saving}
                      alignSelf="flex-end"
                    >
                      Save Changes
                    </Button>
                  </Stack>
                </form>
              </CardBody>
            </Card>
          </TabPanel>

          {/* Subscription Settings */}
          <TabPanel>
            <Card bg={cardBg} shadow="md">
              <CardHeader>
                <Heading size="md">Subscription</Heading>
              </CardHeader>
              <CardBody>
                <Stack spacing={4}>
                  <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)' }} gap={4}>
                    <Box>
                      <Text fontWeight="bold">Current Plan</Text>
                      <Text fontSize="xl">{tenant.plan}</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">Status</Text>
                      <Text fontSize="xl">{tenant.status}</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">Plan Start Date</Text>
                      <Text>{tenant.planStartDate ? new Date(tenant.planStartDate).toLocaleDateString() : 'N/A'}</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">Plan End Date</Text>
                      <Text>{tenant.planEndDate ? new Date(tenant.planEndDate).toLocaleDateString() : 'N/A'}</Text>
                    </Box>
                  </Grid>

                  <Box mt={4}>
                    <Heading size="sm" mb={2}>Available Plans</Heading>
                    <Grid templateColumns={{ base: '1fr', md: 'repeat(4, 1fr)' }} gap={4}>
                      {['FREE', 'STARTER', 'PROFESSIONAL', 'ENTERPRISE'].map((plan) => (
                        <Card key={plan} variant="outline" bg={tenant.plan === plan ? 'brand.50' : undefined}>
                          <CardHeader pb={2}>
                            <Heading size="md">{plan}</Heading>
                          </CardHeader>
                          <CardBody pt={0}>
                            <Text mb={4}>
                              {plan === 'FREE' && 'Basic features for small teams'}
                              {plan === 'STARTER' && 'Essential features for growing teams'}
                              {plan === 'PROFESSIONAL' && 'Advanced features for businesses'}
                              {plan === 'ENTERPRISE' && 'Complete solution for large organizations'}
                            </Text>
                            <Button
                              size="sm"
                              colorScheme={tenant.plan === plan ? 'gray' : 'brand'}
                              isDisabled={tenant.plan === plan}
                            >
                              {tenant.plan === plan ? 'Current Plan' : 'Change Plan'}
                            </Button>
                          </CardBody>
                        </Card>
                      ))}
                    </Grid>
                  </Box>
                </Stack>
              </CardBody>
            </Card>
          </TabPanel>

          {/* Usage Stats */}
          <TabPanel>
            <Card bg={cardBg} shadow="md">
              <CardHeader>
                <Heading size="md">Usage Statistics</Heading>
              </CardHeader>
              <CardBody>
                <Stack spacing={4}>
                  <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)' }} gap={4}>
                    <Box>
                      <Text fontWeight="bold">Email Quota</Text>
                      <Text>{tenant.maxEmailsPerMonth} emails per month</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">SMS Quota</Text>
                      <Text>{tenant.maxSmsPerMonth} SMS per month</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">WhatsApp Quota</Text>
                      <Text>{tenant.maxWhatsappPerMonth} messages per month</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">Campaign Quota</Text>
                      <Text>{tenant.maxCampaignsPerMonth} campaigns per month</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">Storage Quota</Text>
                      <Text>{tenant.storageLimitMb} MB</Text>
                    </Box>

                    <Box>
                      <Text fontWeight="bold">User Quota</Text>
                      <Text>{tenant.maxUsers} users</Text>
                    </Box>
                  </Grid>

                  <Box mt={4}>
                    <Heading size="sm" mb={2}>Current Usage</Heading>
                    <Text>Loading usage statistics...</Text>
                  </Box>
                </Stack>
              </CardBody>
            </Card>
          </TabPanel>
        </TabPanels>
      </Tabs>
    </Box>
  );
};

export default TenantSettings;