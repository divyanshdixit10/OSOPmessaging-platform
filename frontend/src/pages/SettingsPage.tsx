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
  Switch,
  FormControl,
  FormLabel,
  Input,
  Select,
  Textarea,
  Divider,
  Avatar,
  useToast,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Badge,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
  Icon,
  useColorModeValue,
} from '@chakra-ui/react';
import { IconType } from 'react-icons';
import {
  FiUser,
  FiMail,
  FiShield,
  FiBell,
  FiGlobe,
  FiSave,
  FiEdit3,
  FiTrash2,
  FiKey,
  FiDownload,
  FiUpload,
} from 'react-icons/fi';

interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  company: string;
  role: string;
  avatar: string;
}

interface NotificationSettings {
  emailNotifications: boolean;
  pushNotifications: boolean;
  campaignUpdates: boolean;
  performanceReports: boolean;
  securityAlerts: boolean;
  marketingTips: boolean;
}

interface SecuritySettings {
  twoFactorAuth: boolean;
  sessionTimeout: number;
  passwordExpiry: number;
  loginNotifications: boolean;
  suspiciousActivityAlerts: boolean;
}

const defaultUserProfile: UserProfile = {
  firstName: 'John',
  lastName: 'Doe',
  email: 'john.doe@example.com',
  company: 'OSOP Technologies',
  role: 'Marketing Manager',
  avatar: '',
};

const defaultNotificationSettings: NotificationSettings = {
  emailNotifications: true,
  pushNotifications: true,
  campaignUpdates: true,
  performanceReports: true,
  securityAlerts: true,
  marketingTips: false,
};

const defaultSecuritySettings: SecuritySettings = {
  twoFactorAuth: false,
  sessionTimeout: 30,
  passwordExpiry: 90,
  loginNotifications: true,
  suspiciousActivityAlerts: true,
};

export const SettingsPage: React.FC = () => {
  const [userProfile, setUserProfile] = useState<UserProfile>(defaultUserProfile);
  const [notificationSettings, setNotificationSettings] = useState<NotificationSettings>(defaultNotificationSettings);
  const [securitySettings, setSecuritySettings] = useState<SecuritySettings>(defaultSecuritySettings);
  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const toast = useToast();

  const cardBg = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');

  const handleSaveProfile = async () => {
    setIsLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      setIsEditingProfile(false);
      toast({
        title: 'Profile updated',
        description: 'Your profile has been successfully updated',
        status: 'success',
        duration: 3000,
      });
    } catch (error) {
      toast({
        title: 'Error updating profile',
        description: 'Failed to update profile. Please try again.',
        status: 'error',
        duration: 5000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleSaveNotifications = async () => {
    setIsLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast({
        title: 'Settings saved',
        description: 'Your notification preferences have been updated',
        status: 'success',
        duration: 3000,
      });
    } catch (error) {
      toast({
        title: 'Error saving settings',
        description: 'Failed to save notification settings. Please try again.',
        status: 'error',
        duration: 5000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleSaveSecurity = async () => {
    setIsLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast({
        title: 'Security settings saved',
        description: 'Your security preferences have been updated',
        status: 'success',
        duration: 3000,
      });
    } catch (error) {
      toast({
        title: 'Error saving settings',
        description: 'Failed to save security settings. Please try again.',
        status: 'error',
        duration: 5000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const updateNotificationSetting = (key: keyof NotificationSettings, value: boolean) => {
    setNotificationSettings(prev => ({
      ...prev,
      [key]: value,
    }));
  };

  const updateSecuritySetting = (key: keyof SecuritySettings, value: boolean | number) => {
    setSecuritySettings(prev => ({
      ...prev,
      [key]: value,
    }));
  };

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <Heading size="lg" color="gray.800" mb={2}>
          Settings
        </Heading>
        <Text color="gray.600">
          Manage your account settings, preferences, and platform configuration.
        </Text>
      </Box>

      <Tabs variant="enclosed" colorScheme="brand">
        <TabList>
          <Tab>
            <HStack spacing={2}>
              <Icon as={FiUser as any} />
              <Text>Profile</Text>
            </HStack>
          </Tab>
          <Tab>
            <HStack spacing={2}>
              <Icon as={FiBell as any} />
              <Text>Notifications</Text>
            </HStack>
          </Tab>
          <Tab>
            <HStack spacing={2}>
              <Icon as={FiShield as any} />
              <Text>Security</Text>
            </HStack>
          </Tab>
          <Tab>
            <HStack spacing={2}>
              <Icon as={FiGlobe as any} />
              <Text>Platform</Text>
            </HStack>
          </Tab>
        </TabList>

        <TabPanels>
          {/* Profile Tab */}
          <TabPanel p={0} pt={6}>
            <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={8}>
              <GridItem>
                <Card>
                  <CardHeader>
                    <HStack justify="space-between">
                      <Heading size="md" color="gray.800">
                        Personal Information
                      </Heading>
                      <Button
                        leftIcon={isEditingProfile ? <Icon as={FiSave as any} /> : <Icon as={FiEdit3 as any} />}
                        variant={isEditingProfile ? 'solid' : 'outline'}
                        colorScheme="brand"
                        onClick={isEditingProfile ? handleSaveProfile : () => setIsEditingProfile(true)}
                        isLoading={isLoading}
                      >
                        {isEditingProfile ? 'Save Changes' : 'Edit Profile'}
                      </Button>
                    </HStack>
                  </CardHeader>
                  <CardBody>
                    <VStack spacing={6} align="stretch">
                      <Grid templateColumns="repeat(2, 1fr)" gap={4}>
                        <FormControl>
                          <FormLabel>First Name</FormLabel>
                          <Input
                            value={userProfile.firstName}
                            onChange={(e) => setUserProfile(prev => ({ ...prev, firstName: e.target.value }))}
                            isDisabled={!isEditingProfile}
                          />
                        </FormControl>
                        <FormControl>
                          <FormLabel>Last Name</FormLabel>
                          <Input
                            value={userProfile.lastName}
                            onChange={(e) => setUserProfile(prev => ({ ...prev, lastName: e.target.value }))}
                            isDisabled={!isEditingProfile}
                          />
                        </FormControl>
                      </Grid>

                      <FormControl>
                        <FormLabel>Email Address</FormLabel>
                        <Input
                          value={userProfile.email}
                          onChange={(e) => setUserProfile(prev => ({ ...prev, email: e.target.value }))}
                          isDisabled={!isEditingProfile}
                          type="email"
                        />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Company</FormLabel>
                        <Input
                          value={userProfile.company}
                          onChange={(e) => setUserProfile(prev => ({ ...prev, company: e.target.value }))}
                          isDisabled={!isEditingProfile}
                        />
                      </FormControl>

                      <FormControl>
                        <FormLabel>Role</FormLabel>
                        <Select
                          value={userProfile.role}
                          onChange={(e) => setUserProfile(prev => ({ ...prev, role: e.target.value }))}
                          isDisabled={!isEditingProfile}
                        >
                          <option value="Marketing Manager">Marketing Manager</option>
                          <option value="Content Creator">Content Creator</option>
                          <option value="Business Owner">Business Owner</option>
                          <option value="Developer">Developer</option>
                          <option value="Other">Other</option>
                        </Select>
                      </FormControl>

                      {isEditingProfile && (
                        <HStack justify="flex-end" spacing={3}>
                          <Button
                            variant="ghost"
                            onClick={() => {
                              setIsEditingProfile(false);
                              setUserProfile(defaultUserProfile);
                            }}
                          >
                            Cancel
                          </Button>
                        </HStack>
                      )}
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>

              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      Profile Picture
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <VStack spacing={4} align="center">
                      <Avatar
                        size="2xl"
                        name={`${userProfile.firstName} ${userProfile.lastName}`}
                        src={userProfile.avatar}
                        bg="brand.500"
                        color="white"
                      />
                      <Text fontSize="sm" color="gray.600" textAlign="center">
                        {userProfile.firstName} {userProfile.lastName}
                      </Text>
                      <Text fontSize="xs" color="gray.500" textAlign="center">
                        {userProfile.role} at {userProfile.company}
                      </Text>
                      <Button size="sm" variant="outline" leftIcon={<Icon as={FiUpload as any} />}>
                        Change Photo
                      </Button>
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>
            </Grid>
          </TabPanel>

          {/* Notifications Tab */}
          <TabPanel p={0} pt={6}>
            <Card>
              <CardHeader>
                <HStack justify="space-between">
                  <Heading size="md" color="gray.800">
                    Notification Preferences
                  </Heading>
                  <Button
                    leftIcon={<Icon as={FiSave as any} />}
                    colorScheme="brand"
                    onClick={handleSaveNotifications}
                    isLoading={isLoading}
                  >
                    Save Settings
                  </Button>
                </HStack>
              </CardHeader>
              <CardBody>
                <VStack spacing={6} align="stretch">
                  <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)' }} gap={6}>
                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="email-notifications" mb="0">
                          Email Notifications
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Receive notifications via email
                        </Text>
                      </Box>
                      <Switch
                        id="email-notifications"
                        isChecked={notificationSettings.emailNotifications}
                        onChange={(e) => updateNotificationSetting('emailNotifications', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="push-notifications" mb="0">
                          Push Notifications
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Receive browser push notifications
                        </Text>
                      </Box>
                      <Switch
                        id="push-notifications"
                        isChecked={notificationSettings.pushNotifications}
                        onChange={(e) => updateNotificationSetting('pushNotifications', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="campaign-updates" mb="0">
                          Campaign Updates
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Get notified about campaign status changes
                        </Text>
                      </Box>
                      <Switch
                        id="campaign-updates"
                        isChecked={notificationSettings.campaignUpdates}
                        onChange={(e) => updateNotificationSetting('campaignUpdates', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="performance-reports" mb="0">
                          Performance Reports
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Weekly performance summaries
                        </Text>
                      </Box>
                      <Switch
                        id="performance-reports"
                        isChecked={notificationSettings.performanceReports}
                        onChange={(e) => updateNotificationSetting('performanceReports', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="security-alerts" mb="0">
                          Security Alerts
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Important security notifications
                        </Text>
                      </Box>
                      <Switch
                        id="security-alerts"
                        isChecked={notificationSettings.securityAlerts}
                        onChange={(e) => updateNotificationSetting('securityAlerts', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="marketing-tips" mb="0">
                          Marketing Tips
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Helpful marketing insights and tips
                        </Text>
                      </Box>
                      <Switch
                        id="marketing-tips"
                        isChecked={notificationSettings.marketingTips}
                        onChange={(e) => updateNotificationSetting('marketingTips', e.target.checked)}
                      />
                    </FormControl>
                  </Grid>
                </VStack>
              </CardBody>
            </Card>
          </TabPanel>

          {/* Security Tab */}
          <TabPanel p={0} pt={6}>
            <Card>
              <CardHeader>
                <HStack justify="space-between">
                  <Heading size="md" color="gray.800">
                    Security Settings
                  </Heading>
                  <Button
                    leftIcon={<Icon as={FiSave as any} />}
                    colorScheme="brand"
                    onClick={handleSaveSecurity}
                    isLoading={isLoading}
                  >
                    Save Settings
                  </Button>
                </HStack>
              </CardHeader>
              <CardBody>
                <VStack spacing={6} align="stretch">
                  <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)' }} gap={6}>
                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="two-factor-auth" mb="0">
                          Two-Factor Authentication
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Add an extra layer of security
                        </Text>
                      </Box>
                      <Switch
                        id="two-factor-auth"
                        isChecked={securitySettings.twoFactorAuth}
                        onChange={(e) => updateSecuritySetting('twoFactorAuth', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="login-notifications" mb="0">
                          Login Notifications
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Get notified of new login attempts
                        </Text>
                      </Box>
                      <Switch
                        id="login-notifications"
                        isChecked={securitySettings.loginNotifications}
                        onChange={(e) => updateSecuritySetting('loginNotifications', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl display="flex" alignItems="center">
                      <Box flex="1">
                        <FormLabel htmlFor="suspicious-activity" mb="0">
                          Suspicious Activity Alerts
                        </FormLabel>
                        <Text fontSize="sm" color="gray.600">
                          Alert on unusual account activity
                        </Text>
                      </Box>
                      <Switch
                        id="suspicious-activity"
                        isChecked={securitySettings.suspiciousActivityAlerts}
                        onChange={(e) => updateSecuritySetting('suspiciousActivityAlerts', e.target.checked)}
                      />
                    </FormControl>

                    <FormControl>
                      <FormLabel>Session Timeout (minutes)</FormLabel>
                      <Select
                        value={securitySettings.sessionTimeout}
                        onChange={(e) => updateSecuritySetting('sessionTimeout', parseInt(e.target.value))}
                      >
                        <option value={15}>15 minutes</option>
                        <option value={30}>30 minutes</option>
                        <option value={60}>1 hour</option>
                        <option value={120}>2 hours</option>
                        <option value={480}>8 hours</option>
                      </Select>
                    </FormControl>

                    <FormControl>
                      <FormLabel>Password Expiry (days)</FormLabel>
                      <Select
                        value={securitySettings.passwordExpiry}
                        onChange={(e) => updateSecuritySetting('passwordExpiry', parseInt(e.target.value))}
                      >
                        <option value={30}>30 days</option>
                        <option value={60}>60 days</option>
                        <option value={90}>90 days</option>
                        <option value={180}>180 days</option>
                        <option value={365}>1 year</option>
                      </Select>
                    </FormControl>
                  </Grid>

                  <Divider />

                  <Box>
                    <Heading size="sm" color="gray.800" mb={4}>
                      Account Actions
                    </Heading>
                    <HStack spacing={4}>
                      <Button leftIcon={<Icon as={FiKey as any} />} variant="outline" colorScheme="orange">
                        Change Password
                      </Button>
                                              <Button leftIcon={<Icon as={FiDownload as any} />} variant="outline">
                        Export Data
                      </Button>
                                              <Button leftIcon={<Icon as={FiTrash2 as any} />} variant="outline" colorScheme="red">
                        Delete Account
                      </Button>
                    </HStack>
                  </Box>
                </VStack>
              </CardBody>
            </Card>
          </TabPanel>

          {/* Platform Tab */}
          <TabPanel p={0} pt={6}>
            <Grid templateColumns={{ base: '1fr', lg: 'repeat(2, 1fr)' }} gap={8}>
              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      Platform Information
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <VStack spacing={4} align="stretch">
                      <Box>
                        <Text fontSize="sm" color="gray.600" mb={1}>
                          Platform Version
                        </Text>
                        <Text fontWeight="medium">v2.1.0</Text>
                      </Box>
                      <Box>
                        <Text fontSize="sm" color="gray.600" mb={1}>
                          Last Updated
                        </Text>
                        <Text fontWeight="medium">December 20, 2024</Text>
                      </Box>
                      <Box>
                        <Text fontSize="sm" color="gray.600" mb={1}>
                          API Version
                        </Text>
                        <Text fontWeight="medium">v1.0.0</Text>
                      </Box>
                      <Box>
                        <Text fontSize="sm" color="gray.600" mb={1}>
                          Database
                        </Text>
                        <Text fontWeight="medium">PostgreSQL 14.0</Text>
                      </Box>
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>

              <GridItem>
                <Card>
                  <CardHeader>
                    <Heading size="md" color="gray.800">
                      System Status
                    </Heading>
                  </CardHeader>
                  <CardBody>
                    <VStack spacing={4} align="stretch">
                      <HStack justify="space-between">
                        <Text fontSize="sm">Email Service</Text>
                        <Badge colorScheme="green">Operational</Badge>
                      </HStack>
                      <HStack justify="space-between">
                        <Text fontSize="sm">SMS Service</Text>
                        <Badge colorScheme="green">Operational</Badge>
                      </HStack>
                      <HStack justify="space-between">
                        <Text fontSize="sm">WhatsApp Service</Text>
                        <Badge colorScheme="green">Operational</Badge>
                      </HStack>
                      <HStack justify="space-between">
                        <Text fontSize="sm">Database</Text>
                        <Badge colorScheme="green">Operational</Badge>
                      </HStack>
                      <HStack justify="space-between">
                        <Text fontSize="sm">API Gateway</Text>
                        <Badge colorScheme="green">Operational</Badge>
                      </HStack>
                    </VStack>
                  </CardBody>
                </Card>
              </GridItem>
            </Grid>

            <Card mt={8}>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Support & Documentation
                </Heading>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  <Alert status="info">
                    <AlertIcon />
                    <Box>
                      <AlertTitle>Need Help?</AlertTitle>
                      <AlertDescription>
                        Check our documentation, contact support, or join our community forum for assistance.
                      </AlertDescription>
                    </Box>
                  </Alert>
                  
                  <HStack spacing={4}>
                    <Button variant="outline" leftIcon={<Icon as={FiGlobe as any} />}>
                      Documentation
                    </Button>
                    <Button variant="outline">
                      Contact Support
                    </Button>
                    <Button variant="outline">
                      Community Forum
                    </Button>
                  </HStack>
                </VStack>
              </CardBody>
            </Card>
          </TabPanel>
        </TabPanels>
      </Tabs>
    </VStack>
  );
};
