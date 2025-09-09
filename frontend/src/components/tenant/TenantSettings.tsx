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
  FormControl,
  FormLabel,
  Input,
  Textarea,
  Select,
  Switch,
  Divider,
  Badge,
  Progress,
  Grid,
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
  Image,
  IconButton,
} from '@chakra-ui/react';
import { FiUpload, FiEdit, FiSave, FiX, FiDollarSign, FiUsers, FiMail, FiMessageSquare } from 'react-icons/fi';
import { ChromePicker } from 'react-color';
import { useAppStore } from '../../store/useAppStore';
import { tenantService, Tenant, UpdateTenantRequest } from '../../api/tenantService';
import { IconWrapper } from '../common/IconWrapper';

export const TenantSettings: React.FC = () => {
  const { currentTenant, updateTenantSettings } = useAppStore();
  const toast = useToast();
  const cardBg = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.600');
  
  const [tenant, setTenant] = useState<Tenant | null>(currentTenant);
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);
  const [colorPickerOpen, setColorPickerOpen] = useState(false);
  const [selectedColor, setSelectedColor] = useState<'primary' | 'secondary'>('primary');
  
  const { isOpen, onOpen, onClose } = useDisclosure();

  useEffect(() => {
    if (currentTenant) {
      setTenant(currentTenant);
    }
  }, [currentTenant]);

  const handleSave = async () => {
    if (!tenant) return;
    
    setLoading(true);
    try {
      const updateData: UpdateTenantRequest = {
        displayName: tenant.displayName,
        description: tenant.description,
        contactEmail: tenant.contactEmail,
        contactPhone: tenant.contactPhone,
        companyName: tenant.companyName,
        companyAddress: tenant.companyAddress,
        timezone: tenant.timezone,
        locale: tenant.locale,
        primaryColor: tenant.primaryColor,
        secondaryColor: tenant.secondaryColor,
        logoUrl: tenant.logoUrl,
      };

      const response = await tenantService.updateTenant(tenant.id, updateData);
      if (response.success) {
        updateTenantSettings(response.data);
        setTenant(response.data);
        setEditing(false);
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
        title: 'Error',
        description: 'Failed to update settings. Please try again.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleLogoUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    setLoading(true);
    try {
      const response = await tenantService.uploadLogo(file);
      if (response.success) {
        setTenant(prev => prev ? { ...prev, logoUrl: response.data.logoUrl } : null);
        toast({
          title: 'Logo uploaded',
          description: 'Your logo has been uploaded successfully.',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to upload logo. Please try again.',
        status: 'error',
        duration: 3000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleColorChange = (color: any) => {
    if (!tenant) return;
    
    if (selectedColor === 'primary') {
      setTenant({ ...tenant, primaryColor: color.hex });
    } else {
      setTenant({ ...tenant, secondaryColor: color.hex });
    }
  };

  const getPlanColor = (plan: string) => {
    switch (plan) {
      case 'FREE': return 'gray';
      case 'STARTER': return 'blue';
      case 'PROFESSIONAL': return 'green';
      case 'ENTERPRISE': return 'purple';
      default: return 'gray';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'green';
      case 'TRIAL': return 'blue';
      case 'SUSPENDED': return 'red';
      case 'CANCELLED': return 'gray';
      default: return 'gray';
    }
  };

  if (!tenant) {
    return (
      <Box p={6}>
        <Text>Loading tenant settings...</Text>
      </Box>
    );
  }

  return (
    <Box p={6}>
      <VStack spacing={6} align="stretch">
        {/* Header */}
        <HStack justify="space-between">
          <Box>
            <Text fontSize="2xl" fontWeight="bold">
              Tenant Settings
            </Text>
            <Text color="gray.600">
              Manage your organization's settings and preferences
            </Text>
          </Box>
          <HStack>
            {editing ? (
              <>
                <Button
                  leftIcon={<IconWrapper icon={FiSave} />}
                  colorScheme="blue"
                  onClick={handleSave}
                  isLoading={loading}
                >
                  Save Changes
                </Button>
                <Button
                  leftIcon={<IconWrapper icon={FiX} />}
                  variant="outline"
                  onClick={() => {
                    setTenant(currentTenant);
                    setEditing(false);
                  }}
                >
                  Cancel
                </Button>
              </>
            ) : (
              <Button
                leftIcon={<IconWrapper icon={FiEdit} />}
                colorScheme="blue"
                onClick={() => setEditing(true)}
              >
                Edit Settings
              </Button>
            )}
          </HStack>
        </HStack>

        <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={6}>
          {/* Main Settings */}
          <VStack spacing={6} align="stretch">
            {/* Basic Information */}
            <Card bg={cardBg} borderColor={borderColor}>
              <CardHeader>
                <Text fontSize="lg" fontWeight="semibold">
                  Basic Information
                </Text>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  <FormControl>
                    <FormLabel>Display Name</FormLabel>
                    <Input
                      value={tenant.displayName}
                      onChange={(e) => setTenant({ ...tenant, displayName: e.target.value })}
                      isDisabled={!editing}
                    />
                  </FormControl>

                  <FormControl>
                    <FormLabel>Description</FormLabel>
                    <Textarea
                      value={tenant.description || ''}
                      onChange={(e) => setTenant({ ...tenant, description: e.target.value })}
                      isDisabled={!editing}
                      rows={3}
                    />
                  </FormControl>

                  <HStack spacing={4}>
                    <FormControl>
                      <FormLabel>Contact Email</FormLabel>
                      <Input
                        type="email"
                        value={tenant.contactEmail}
                        onChange={(e) => setTenant({ ...tenant, contactEmail: e.target.value })}
                        isDisabled={!editing}
                      />
                    </FormControl>

                    <FormControl>
                      <FormLabel>Contact Phone</FormLabel>
                      <Input
                        value={tenant.contactPhone || ''}
                        onChange={(e) => setTenant({ ...tenant, contactPhone: e.target.value })}
                        isDisabled={!editing}
                      />
                    </FormControl>
                  </HStack>

                  <FormControl>
                    <FormLabel>Company Name</FormLabel>
                    <Input
                      value={tenant.companyName || ''}
                      onChange={(e) => setTenant({ ...tenant, companyName: e.target.value })}
                      isDisabled={!editing}
                    />
                  </FormControl>

                  <FormControl>
                    <FormLabel>Company Address</FormLabel>
                    <Textarea
                      value={tenant.companyAddress || ''}
                      onChange={(e) => setTenant({ ...tenant, companyAddress: e.target.value })}
                      isDisabled={!editing}
                      rows={2}
                    />
                  </FormControl>
                </VStack>
              </CardBody>
            </Card>

            {/* Branding */}
            <Card bg={cardBg} borderColor={borderColor}>
              <CardHeader>
                <Text fontSize="lg" fontWeight="semibold">
                  Branding
                </Text>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  <FormControl>
                    <FormLabel>Logo</FormLabel>
                    <HStack spacing={4}>
                      {tenant.logoUrl && (
                        <Image
                          src={tenant.logoUrl}
                          alt="Logo"
                          boxSize="60px"
                          objectFit="contain"
                          borderRadius="md"
                        />
                      )}
                      <Box>
                        <input
                          type="file"
                          accept="image/*"
                          onChange={handleLogoUpload}
                          style={{ display: 'none' }}
                          id="logo-upload"
                        />
                        <Button
                          as="label"
                          htmlFor="logo-upload"
                          leftIcon={<IconWrapper icon={FiUpload} />}
                          variant="outline"
                          size="sm"
                          isDisabled={!editing}
                        >
                          Upload Logo
                        </Button>
                      </Box>
                    </HStack>
                  </FormControl>

                  <HStack spacing={4}>
                    <FormControl>
                      <FormLabel>Primary Color</FormLabel>
                      <HStack>
                        <Box
                          w="40px"
                          h="40px"
                          bg={tenant.primaryColor || '#3182ce'}
                          borderRadius="md"
                          border="2px solid"
                          borderColor={borderColor}
                          cursor={editing ? 'pointer' : 'default'}
                          onClick={() => editing && (setSelectedColor('primary'), setColorPickerOpen(true))}
                        />
                        <Input
                          value={tenant.primaryColor || ''}
                          onChange={(e) => setTenant({ ...tenant, primaryColor: e.target.value })}
                          isDisabled={!editing}
                          placeholder="#3182ce"
                        />
                      </HStack>
                    </FormControl>

                    <FormControl>
                      <FormLabel>Secondary Color</FormLabel>
                      <HStack>
                        <Box
                          w="40px"
                          h="40px"
                          bg={tenant.secondaryColor || '#38a169'}
                          borderRadius="md"
                          border="2px solid"
                          borderColor={borderColor}
                          cursor={editing ? 'pointer' : 'default'}
                          onClick={() => editing && (setSelectedColor('secondary'), setColorPickerOpen(true))}
                        />
                        <Input
                          value={tenant.secondaryColor || ''}
                          onChange={(e) => setTenant({ ...tenant, secondaryColor: e.target.value })}
                          isDisabled={!editing}
                          placeholder="#38a169"
                        />
                      </HStack>
                    </FormControl>
                  </HStack>
                </VStack>
              </CardBody>
            </Card>

            {/* Localization */}
            <Card bg={cardBg} borderColor={borderColor}>
              <CardHeader>
                <Text fontSize="lg" fontWeight="semibold">
                  Localization
                </Text>
              </CardHeader>
              <CardBody>
                <HStack spacing={4}>
                  <FormControl>
                    <FormLabel>Timezone</FormLabel>
                    <Select
                      value={tenant.timezone}
                      onChange={(e) => setTenant({ ...tenant, timezone: e.target.value })}
                      isDisabled={!editing}
                    >
                      <option value="UTC">UTC</option>
                      <option value="America/New_York">Eastern Time</option>
                      <option value="America/Chicago">Central Time</option>
                      <option value="America/Denver">Mountain Time</option>
                      <option value="America/Los_Angeles">Pacific Time</option>
                      <option value="Europe/London">London</option>
                      <option value="Europe/Paris">Paris</option>
                      <option value="Asia/Tokyo">Tokyo</option>
                      <option value="Asia/Shanghai">Shanghai</option>
                    </Select>
                  </FormControl>

                  <FormControl>
                    <FormLabel>Locale</FormLabel>
                    <Select
                      value={tenant.locale}
                      onChange={(e) => setTenant({ ...tenant, locale: e.target.value })}
                      isDisabled={!editing}
                    >
                      <option value="en_US">English (US)</option>
                      <option value="en_GB">English (UK)</option>
                      <option value="es_ES">Spanish</option>
                      <option value="fr_FR">French</option>
                      <option value="de_DE">German</option>
                      <option value="it_IT">Italian</option>
                      <option value="pt_BR">Portuguese (Brazil)</option>
                      <option value="ja_JP">Japanese</option>
                      <option value="ko_KR">Korean</option>
                      <option value="zh_CN">Chinese (Simplified)</option>
                    </Select>
                  </FormControl>
                </HStack>
              </CardBody>
            </Card>
          </VStack>

          {/* Sidebar */}
          <VStack spacing={6} align="stretch">
            {/* Plan & Status */}
            <Card bg={cardBg} borderColor={borderColor}>
              <CardHeader>
                <Text fontSize="lg" fontWeight="semibold">
                  Plan & Status
                </Text>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  <HStack justify="space-between">
                    <Text fontSize="sm" color="gray.500">Current Plan</Text>
                    <Badge colorScheme={getPlanColor(tenant.plan)} size="lg">
                      {tenant.plan}
                    </Badge>
                  </HStack>

                  <HStack justify="space-between">
                    <Text fontSize="sm" color="gray.500">Status</Text>
                    <Badge colorScheme={getStatusColor(tenant.status)} size="lg">
                      {tenant.status}
                    </Badge>
                  </HStack>

                  {tenant.trialEndsAt && (
                    <Box>
                      <HStack justify="space-between" mb={2}>
                        <Text fontSize="sm" color="gray.500">Trial Ends</Text>
                        <Text fontSize="sm" fontWeight="medium">
                          {new Date(tenant.trialEndsAt).toLocaleDateString()}
                        </Text>
                      </HStack>
                      <Progress
                        value={100}
                        size="sm"
                        colorScheme={new Date(tenant.trialEndsAt) > new Date() ? 'blue' : 'red'}
                      />
                    </Box>
                  )}

                  <Button
                    leftIcon={<IconWrapper icon={FiDollarSign} />}
                    colorScheme="blue"
                    variant="outline"
                    onClick={onOpen}
                  >
                    Manage Billing
                  </Button>
                </VStack>
              </CardBody>
            </Card>

            {/* Usage Quotas */}
            <Card bg={cardBg} borderColor={borderColor}>
              <CardHeader>
                <Text fontSize="lg" fontWeight="semibold">
                  Usage Quotas
                </Text>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  <Box>
                    <HStack justify="space-between" mb={2}>
                      <HStack>
                        <IconWrapper icon={FiUsers} />
                        <Text fontSize="sm">Users</Text>
                      </HStack>
                      <Text fontSize="sm" fontWeight="medium">
                        {tenant.currentUserCount || 0} / {tenant.maxUsers}
                      </Text>
                    </HStack>
                    <Progress
                      value={((tenant.currentUserCount || 0) / tenant.maxUsers) * 100}
                      size="sm"
                      colorScheme="blue"
                    />
                  </Box>

                  <Box>
                    <HStack justify="space-between" mb={2}>
                      <HStack>
                        <IconWrapper icon={FiMail} />
                        <Text fontSize="sm">Emails/Month</Text>
                      </HStack>
                      <Text fontSize="sm" fontWeight="medium">
                        {tenant.currentMonthEmails || 0} / {tenant.maxEmailsPerMonth}
                      </Text>
                    </HStack>
                    <Progress
                      value={((tenant.currentMonthEmails || 0) / tenant.maxEmailsPerMonth) * 100}
                      size="sm"
                      colorScheme="green"
                    />
                  </Box>

                  <Box>
                    <HStack justify="space-between" mb={2}>
                      <HStack>
                        <IconWrapper icon={FiMessageSquare} />
                        <Text fontSize="sm">SMS/Month</Text>
                      </HStack>
                      <Text fontSize="sm" fontWeight="medium">
                        {tenant.currentMonthSms || 0} / {tenant.maxSmsPerMonth}
                      </Text>
                    </HStack>
                    <Progress
                      value={((tenant.currentMonthSms || 0) / tenant.maxSmsPerMonth) * 100}
                      size="sm"
                      colorScheme="purple"
                    />
                  </Box>

                  <Box>
                    <HStack justify="space-between" mb={2}>
                      <Text fontSize="sm">Storage</Text>
                      <Text fontSize="sm" fontWeight="medium">
                        {Math.round(tenant.currentStorageMb / 1024)} MB / {Math.round(tenant.storageLimitMb / 1024)} MB
                      </Text>
                    </HStack>
                    <Progress
                      value={(tenant.currentStorageMb / tenant.storageLimitMb) * 100}
                      size="sm"
                      colorScheme="orange"
                    />
                  </Box>
                </VStack>
              </CardBody>
            </Card>
          </VStack>
        </Grid>

        {/* Color Picker Modal */}
        <Modal isOpen={colorPickerOpen} onClose={() => setColorPickerOpen(false)}>
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>
              Choose {selectedColor === 'primary' ? 'Primary' : 'Secondary'} Color
            </ModalHeader>
            <ModalCloseButton />
            <ModalBody>
              <ChromePicker
                color={selectedColor === 'primary' ? tenant.primaryColor : tenant.secondaryColor}
                onChange={handleColorChange}
              />
            </ModalBody>
            <ModalFooter>
              <Button onClick={() => setColorPickerOpen(false)}>
                Done
              </Button>
            </ModalFooter>
          </ModalContent>
        </Modal>

        {/* Billing Modal */}
        <Modal isOpen={isOpen} onClose={onClose} size="xl">
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>Billing & Subscription</ModalHeader>
            <ModalCloseButton />
            <ModalBody>
              <Text>Billing management interface would go here...</Text>
            </ModalBody>
            <ModalFooter>
              <Button onClick={onClose}>Close</Button>
            </ModalFooter>
          </ModalContent>
        </Modal>
      </VStack>
    </Box>
  );
};
