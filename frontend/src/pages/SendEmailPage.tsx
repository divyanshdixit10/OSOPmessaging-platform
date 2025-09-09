import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Container,
  Divider,
  FormControl,
  FormLabel,
  Grid,
  GridItem,
  HStack,
  Input,
  Switch,
  Tab,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  Text,
  VStack,
  useToast,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Badge,
  Icon,
  Progress,
  Alert,
  AlertIcon,
  AlertTitle,
  AlertDescription,
} from '@chakra-ui/react';
import {
  FiMail,
  FiUsers,
  FiPaperclip,
  FiEye,
  FiSend,
  FiBarChart,
  FiShield,
  FiLink,
} from 'react-icons/fi';
import { EmailEditor } from '../components/EmailEditor';
import { RecipientUploader } from '../components/RecipientUploader';
import { TemplateSelector } from '../components/TemplateSelector';
import { BulkEmailRequest, Recipient } from '../types/EmailRequest';
import { EmailTemplate } from '../types/EmailTemplate';
import { sendBulkEmail } from '../api/messageService';
import { TemplateService } from '../api/templateService';
import FileUploader from '../components/FileUploader';

const SendEmailPage: React.FC = () => {
  const [templates, setTemplates] = useState<EmailTemplate[]>([]);
  const [selectedTemplate, setSelectedTemplate] = useState<EmailTemplate | undefined>();
  const [recipients, setRecipients] = useState<Recipient[]>([]);
  const [attachments, setAttachments] = useState<File[]>([]);
  const [mediaUrls, setMediaUrls] = useState<string>('');
  const [trackOpens, setTrackOpens] = useState(true);
  const [trackClicks, setTrackClicks] = useState(true);
  const [addUnsubscribeLink, setAddUnsubscribeLink] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [campaignProgress, setCampaignProgress] = useState(0);
  const toast = useToast();

  useEffect(() => {
    loadTemplates();
  }, []);

  const loadTemplates = async () => {
    try {
      const response = await TemplateService.getTemplates();
      // Convert Template[] to EmailTemplate[]
      const convertedTemplates: EmailTemplate[] = response.content.map(template => ({
        id: template.id,
        name: template.name,
        subject: template.subject,
        body: template.contentHtml, // Map contentHtml to body
        contentHtml: template.contentHtml,
        contentText: template.contentText,
        category: template.category,
        type: template.type,
        createdBy: template.createdBy,
        createdAt: template.createdAt,
        updatedAt: template.updatedAt,
        isDefault: template.isDefault,
        isActive: template.isActive,
        isPublic: template.isPublic,
        description: template.description,
        variables: template.variables,
        version: template.version,
        parentTemplateId: template.parentTemplateId,
        usageCount: template.usageCount,
        lastUsedAt: template.lastUsedAt,
        tags: template.tags,
        cssStyles: template.cssStyles,
        metadata: template.metadata,
        thumbnailUrl: template.thumbnailUrl,
      }));
      setTemplates(convertedTemplates);
    } catch (error) {
      toast({
        title: 'Error loading templates',
        description: error instanceof Error ? error.message : 'Failed to load templates',
        status: 'error',
        duration: 5000,
      });
    }
  };

  const handleTemplateSave = async (template: EmailTemplate) => {
    try {
      // Convert EmailTemplate to CreateTemplateRequest
      const createRequest = {
        name: template.name,
        subject: template.subject,
        contentHtml: template.body || template.contentHtml || '',
        contentText: template.contentText,
        category: template.category || 'CUSTOM',
        type: template.type || 'HTML',
        description: template.description,
        cssStyles: template.cssStyles,
        variables: template.variables,
        tags: template.tags,
        isPublic: template.isPublic || false,
        isActive: template.isActive !== false,
        isDefault: template.isDefault || false,
      };
      const savedTemplate = await TemplateService.createTemplate(createRequest);
      await loadTemplates();
      // Convert the returned Template back to EmailTemplate
      const convertedTemplate: EmailTemplate = {
        id: savedTemplate.id,
        name: savedTemplate.name,
        subject: savedTemplate.subject,
        body: savedTemplate.contentHtml,
        contentHtml: savedTemplate.contentHtml,
        contentText: savedTemplate.contentText,
        category: savedTemplate.category,
        type: savedTemplate.type,
        createdBy: savedTemplate.createdBy,
        createdAt: savedTemplate.createdAt,
        updatedAt: savedTemplate.updatedAt,
        isDefault: savedTemplate.isDefault,
        isActive: savedTemplate.isActive,
        isPublic: savedTemplate.isPublic,
        description: savedTemplate.description,
        variables: savedTemplate.variables,
        version: savedTemplate.version,
        parentTemplateId: savedTemplate.parentTemplateId,
        usageCount: savedTemplate.usageCount,
        lastUsedAt: savedTemplate.lastUsedAt,
        tags: savedTemplate.tags,
        cssStyles: savedTemplate.cssStyles,
        metadata: savedTemplate.metadata,
        thumbnailUrl: savedTemplate.thumbnailUrl,
      };
      setSelectedTemplate(convertedTemplate);
      return convertedTemplate;
    } catch (error) {
      throw new Error(error instanceof Error ? error.message : 'Failed to save template');
    }
  };

  const handleTemplateSelect = (template: EmailTemplate) => {
    setSelectedTemplate(template);
  };

  const updateTemplateField = (field: 'subject' | 'body', value: string) => {
    if (!selectedTemplate) return;
    
    const updatedTemplate: EmailTemplate = {
      ...selectedTemplate,
      [field]: value
    };
    setSelectedTemplate(updatedTemplate);
  };

  const handleSendEmail = async () => {
    if (!selectedTemplate || recipients.length === 0) {
      toast({
        title: 'Validation Error',
        description: 'Please select a template and add recipients',
        status: 'error',
        duration: 3000,
      });
      return;
    }

    setIsLoading(true);
    setCampaignProgress(0);

    // Simulate progress
    const progressInterval = setInterval(() => {
      setCampaignProgress(prev => {
        if (prev >= 90) {
          clearInterval(progressInterval);
          return 90;
        }
        return prev + 10;
      });
    }, 200);

    try {
      // Create FormData for multipart/form-data
      const formData = new FormData();
      formData.append('templateId', selectedTemplate.id?.toString() || '');
      formData.append('subject', selectedTemplate.subject);
      formData.append('body', selectedTemplate.body);
      
      // Add recipients as a JSON string array
      formData.append('recipients', JSON.stringify(recipients.map(r => r.email)));
      
      // Add attachments
      attachments.forEach(file => {
        formData.append('attachments', file);
      });

      // Add media URLs as a JSON string array
      if (mediaUrls) {
        formData.append('mediaUrls', JSON.stringify(mediaUrls.split(',').map(url => url.trim())));
      }

      // Add tracking options
      formData.append('trackOpens', String(trackOpens));
      formData.append('trackClicks', String(trackClicks));
      formData.append('addUnsubscribeLink', String(addUnsubscribeLink));

      const response = await sendBulkEmail(formData);
      
      setCampaignProgress(100);
      
      // Handle different response statuses
      if (response.status === 'SUCCESS') {
        toast({
          title: 'Success',
          description: `Email campaign sent successfully to ${recipients.length} recipients`,
          status: 'success',
          duration: 5000,
        });
      } else if (response.status === 'PARTIAL') {
        const failedCount = Object.values(response.details || {}).filter(status => status === 'FAILED').length;
        const successCount = recipients.length - failedCount;
        toast({
          title: 'Partial Success',
          description: `Emails sent to ${successCount} recipients, ${failedCount} failed`,
          status: 'warning',
          duration: 7000,
        });
      } else {
        toast({
          title: 'Failed',
          description: 'Failed to send emails to any recipients',
          status: 'error',
          duration: 5000,
        });
      }

      // Reset form after delay
      setTimeout(() => {
        setRecipients([]);
        setSelectedTemplate(undefined);
        setAttachments([]);
        setMediaUrls('');
        setCampaignProgress(0);
      }, 2000);

    } catch (error) {
      toast({
        title: 'Error',
        description: error instanceof Error ? error.message : 'Failed to send email',
        status: 'error',
        duration: 5000,
      });
    } finally {
      setIsLoading(false);
      clearInterval(progressInterval);
    }
  };

  const getCampaignStats = () => {
    const totalRecipients = recipients.length;
    const hasAttachments = attachments.length > 0;
    const hasMedia = mediaUrls.trim().length > 0;
    const trackingEnabled = trackOpens || trackClicks;

    return {
      totalRecipients,
      hasAttachments,
      hasMedia,
      trackingEnabled,
      estimatedDelivery: totalRecipients > 1000 ? 'Bulk delivery (2-4 hours)' : 'Immediate delivery',
    };
  };

  const stats = getCampaignStats();

  return (
    <VStack spacing={8} align="stretch">
      {/* Header */}
      <Box>
        <Heading size="lg" color="gray.800" mb={2}>
          Send Email Campaign
        </Heading>
        <Text color="gray.600">
          Create and send professional email campaigns with advanced tracking and analytics.
        </Text>
      </Box>

      {/* Campaign Progress */}
      {isLoading && (
        <Card>
          <CardBody>
            <VStack spacing={4} align="stretch">
              <HStack justify="space-between">
                <Text fontWeight="medium" color="gray.700">
                  Sending Campaign...
                </Text>
                <Text fontSize="sm" color="gray.500">
                  {campaignProgress}% Complete
                </Text>
              </HStack>
              <Progress value={campaignProgress} colorScheme="brand" size="lg" borderRadius="full" />
              <Text fontSize="sm" color="gray.600">
                {campaignProgress < 50 ? 'Preparing campaign...' : 
                 campaignProgress < 90 ? 'Sending emails...' : 'Finalizing...'}
              </Text>
            </VStack>
          </CardBody>
        </Card>
      )}

      {/* Campaign Overview */}
      <Card>
        <CardHeader>
          <Heading size="md" color="gray.800">
            Campaign Overview
          </Heading>
        </CardHeader>
        <CardBody>
          <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }} gap={6}>
            <Box textAlign="center" p={4} bg="blue.50" borderRadius="lg">
              <Icon as={FiUsers as any} boxSize={6} color="blue.500" mb={2} />
              <Text fontSize="2xl" fontWeight="bold" color="blue.700">
                {stats.totalRecipients}
              </Text>
              <Text fontSize="sm" color="blue.600">
                Recipients
              </Text>
            </Box>
            
            <Box textAlign="center" p={4} bg="green.50" borderRadius="lg">
              <Icon as={FiMail as any} boxSize={6} color="green.500" mb={2} />
              <Text fontSize="2xl" fontWeight="bold" color="green.700">
                {selectedTemplate ? 'Ready' : 'Draft'}
              </Text>
              <Text fontSize="sm" color="green.600">
                Status
              </Text>
            </Box>
            
            <Box textAlign="center" p={4} bg="purple.50" borderRadius="lg">
                              <Icon as={FiBarChart as any} boxSize={6} color="purple.500" mb={2} />
              <Text fontSize="2xl" fontWeight="bold" color="purple.700">
                {stats.trackingEnabled ? 'Enabled' : 'Disabled'}
              </Text>
              <Text fontSize="sm" color="purple.600">
                Tracking
              </Text>
            </Box>
            
            <Box textAlign="center" p={4} bg="orange.50" borderRadius="lg">
              <Icon as={FiPaperclip as any} boxSize={6} color="orange.500" mb={2} />
              <Text fontSize="2xl" fontWeight="bold" color="orange.700">
                {attachments.length + (stats.hasMedia ? 1 : 0)}
              </Text>
              <Text fontSize="sm" color="orange.600">
                Attachments
              </Text>
            </Box>
          </Grid>
        </CardBody>
      </Card>

      {/* Main Campaign Builder */}
      <Grid templateColumns={{ base: '1fr', lg: '2fr 1fr' }} gap={8}>
        {/* Left Column - Email Composition */}
        <GridItem>
          <Card>
            <CardHeader>
              <Heading size="md" color="gray.800">
                Email Content
              </Heading>
            </CardHeader>
            <CardBody>
              <Tabs variant="enclosed" colorScheme="brand">
                <TabList>
                  <Tab>
                    <HStack spacing={2}>
                      <Icon as={FiMail as any} />
                      <Text>Compose</Text>
                    </HStack>
                  </Tab>
                  <Tab>
                    <HStack spacing={2}>
                      <Icon as={FiEye as any} />
                      <Text>Preview</Text>
                    </HStack>
                  </Tab>
                </TabList>

                <TabPanels>
                  <TabPanel p={0} pt={4}>
                    <VStack spacing={6} align="stretch">
                      <TemplateSelector
                        templates={templates}
                        selectedTemplate={selectedTemplate}
                        onTemplateSelect={handleTemplateSelect}
                        onTemplateSave={handleTemplateSave}
                      />

                      <EmailEditor
                        subject={selectedTemplate?.subject || ''}
                        body={selectedTemplate?.body || ''}
                        onSubjectChange={(subject) => updateTemplateField('subject', subject)}
                        onBodyChange={(body) => updateTemplateField('body', body)}
                      />
                    </VStack>
                  </TabPanel>

                  <TabPanel p={0} pt={4}>
                    <EmailEditor
                      subject={selectedTemplate?.subject || ''}
                      body={selectedTemplate?.body || ''}
                      onSubjectChange={(subject) => updateTemplateField('subject', subject)}
                      onBodyChange={(body) => updateTemplateField('body', body)}
                      isPreview
                    />
                  </TabPanel>
                </TabPanels>
              </Tabs>
            </CardBody>
          </Card>
        </GridItem>

        {/* Right Column - Campaign Settings */}
        <GridItem>
          <VStack spacing={6} align="stretch">
            {/* Recipients */}
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Recipients
                </Heading>
              </CardHeader>
              <CardBody>
                <RecipientUploader
                  recipients={recipients}
                  onRecipientsChange={setRecipients}
                />
              </CardBody>
            </Card>

            {/* Tracking Options */}
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Tracking & Analytics
                </Heading>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  <FormControl display="flex" alignItems="center">
                    <Box flex="1">
                      <FormLabel htmlFor="track-opens" mb="0">
                        Track Opens
                      </FormLabel>
                      <Text fontSize="sm" color="gray.600">
                        Monitor email open rates
                      </Text>
                    </Box>
                    <Switch
                      id="track-opens"
                      isChecked={trackOpens}
                      onChange={(e) => setTrackOpens(e.target.checked)}
                    />
                  </FormControl>

                  <FormControl display="flex" alignItems="center">
                    <Box flex="1">
                      <FormLabel htmlFor="track-clicks" mb="0">
                        Track Clicks
                      </FormLabel>
                      <Text fontSize="sm" color="gray.600">
                        Monitor link click-through rates
                      </Text>
                    </Box>
                    <Switch
                      id="track-clicks"
                      isChecked={trackClicks}
                      onChange={(e) => setTrackClicks(e.target.checked)}
                    />
                  </FormControl>

                  <FormControl display="flex" alignItems="center">
                    <Box flex="1">
                      <FormLabel htmlFor="unsubscribe-link" mb="0">
                        Add Unsubscribe Link
                      </FormLabel>
                      <Text fontSize="sm" color="gray.600">
                        Compliant with email regulations
                      </Text>
                    </Box>
                    <Switch
                      id="unsubscribe-link"
                      isChecked={addUnsubscribeLink}
                      onChange={(e) => setAddUnsubscribeLink(e.target.checked)}
                    />
                  </FormControl>
                </VStack>
              </CardBody>
            </Card>

            {/* Attachments & Media */}
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Attachments & Media
                </Heading>
              </CardHeader>
              <CardBody>
                <VStack spacing={4} align="stretch">
                  <FormControl>
                    <FormLabel>File Attachments</FormLabel>
                    <FileUploader
                      files={attachments}
                      onFilesChange={setAttachments}
                    />
                  </FormControl>

                  <FormControl>
                    <FormLabel>Media URLs (Optional)</FormLabel>
                    <Input
                      value={mediaUrls}
                      onChange={(e) => setMediaUrls(e.target.value)}
                      placeholder="Enter comma-separated URLs"
                    />
                    <Text fontSize="xs" color="gray.500" mt={1}>
                      Separate multiple URLs with commas
                    </Text>
                  </FormControl>
                </VStack>
              </CardBody>
            </Card>

            {/* Delivery Information */}
            <Card>
              <CardHeader>
                <Heading size="md" color="gray.800">
                  Delivery Information
                </Heading>
              </CardHeader>
              <CardBody>
                <VStack spacing={3} align="stretch">
                  <Box>
                    <Text fontSize="sm" color="gray.600">Estimated Delivery</Text>
                    <Text fontWeight="medium" color="gray.800">{stats.estimatedDelivery}</Text>
                  </Box>
                  
                  <Box>
                    <Text fontSize="sm" color="gray.600">Campaign Size</Text>
                    <Text fontWeight="medium" color="gray.800">
                      {stats.totalRecipients > 1000 ? 'Bulk Campaign' : 'Standard Campaign'}
                    </Text>
                  </Box>

                  {stats.hasAttachments && (
                    <Box>
                      <Text fontSize="sm" color="gray.600">Attachments</Text>
                      <Text fontWeight="medium" color="gray.800">
                        {attachments.length} file(s) attached
                      </Text>
                    </Box>
                  )}
                </VStack>
              </CardBody>
            </Card>
          </VStack>
        </GridItem>
      </Grid>

      {/* Send Campaign */}
      <Card>
        <CardBody>
          <VStack spacing={4} align="stretch">
            {recipients.length > 0 && selectedTemplate && (
              <Alert status="info">
                <AlertIcon />
                <Box>
                  <AlertTitle>Campaign Ready!</AlertTitle>
                  <AlertDescription>
                    Your campaign is ready to send to {recipients.length} recipients. 
                    Review the content and settings before sending.
                  </AlertDescription>
                </Box>
              </Alert>
            )}

            <HStack justify="space-between" align="center">
              <VStack align="flex-start" spacing={1}>
                <Text fontSize="lg" fontWeight="medium" color="gray.700">
                  Ready to Send?
                </Text>
                <Text fontSize="sm" color="gray.500">
                  {recipients.length > 0 && selectedTemplate 
                    ? `Campaign will be sent to ${recipients.length} recipients`
                    : 'Complete all required fields to send campaign'
                  }
                </Text>
              </VStack>

              <Button
                colorScheme="brand"
                size="lg"
                leftIcon={<Icon as={FiSend as any} />}
                isLoading={isLoading}
                onClick={handleSendEmail}
                isDisabled={!selectedTemplate || recipients.length === 0}
                loadingText="Sending Campaign..."
              >
                Send Campaign
              </Button>
            </HStack>
          </VStack>
        </CardBody>
      </Card>
    </VStack>
  );
};

export { SendEmailPage }; 