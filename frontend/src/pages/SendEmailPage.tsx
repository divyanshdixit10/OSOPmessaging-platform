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
  useToast
} from '@chakra-ui/react';
import { EmailEditor } from '../components/EmailEditor';
import { RecipientUploader } from '../components/RecipientUploader';
import { TemplateSelector } from '../components/TemplateSelector';
import { BulkEmailRequest, Recipient } from '../types/EmailRequest';
import { EmailTemplate } from '../types/EmailTemplate';
import { sendBulkEmail } from '../api/messageService';
import { fetchTemplates, saveTemplate } from '../api/templateService';
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
  const toast = useToast();

  useEffect(() => {
    loadTemplates();
  }, []);

  const loadTemplates = async () => {
    try {
      const loadedTemplates = await fetchTemplates();
      setTemplates(loadedTemplates);
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
      const savedTemplate = await saveTemplate(template);
      await loadTemplates(); // Reload all templates to ensure we have the latest list
      setSelectedTemplate(savedTemplate);
      return savedTemplate;
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
    try {
      // Create FormData for multipart/form-data
      const formData = new FormData();
      formData.append('templateId', selectedTemplate.id || '');
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

      await sendBulkEmail(formData);
      
      toast({
        title: 'Success',
        description: 'Email sent successfully',
        status: 'success',
        duration: 3000,
      });

      // Reset form
      setRecipients([]);
      setSelectedTemplate(undefined);
      setAttachments([]);
      setMediaUrls('');
    } catch (error) {
      toast({
        title: 'Error',
        description: error instanceof Error ? error.message : 'Failed to send email',
        status: 'error',
        duration: 5000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Container maxW="container.xl" py={8}>
      <VStack spacing={8} align="stretch">
        <Grid templateColumns="repeat(3, 1fr)" gap={8}>
          <GridItem colSpan={2}>
            <Tabs>
              <TabList>
                <Tab>Compose</Tab>
                <Tab>Preview</Tab>
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
          </GridItem>

          <GridItem>
            <VStack spacing={6} align="stretch">
              <RecipientUploader
                recipients={recipients}
                onRecipientsChange={setRecipients}
              />

              <Box>
                <Text fontWeight="bold" mb={4}>Advanced Options</Text>
                <VStack spacing={4} align="stretch">
                  <FormControl display="flex" alignItems="center">
                    <FormLabel htmlFor="track-opens" mb="0">
                      Track Opens
                    </FormLabel>
                    <Switch
                      id="track-opens"
                      isChecked={trackOpens}
                      onChange={(e) => setTrackOpens(e.target.checked)}
                    />
                  </FormControl>

                  <FormControl display="flex" alignItems="center">
                    <FormLabel htmlFor="track-clicks" mb="0">
                      Track Clicks
                    </FormLabel>
                    <Switch
                      id="track-clicks"
                      isChecked={trackClicks}
                      onChange={(e) => setTrackClicks(e.target.checked)}
                    />
                  </FormControl>

                  <FormControl display="flex" alignItems="center">
                    <FormLabel htmlFor="unsubscribe-link" mb="0">
                      Add Unsubscribe Link
                    </FormLabel>
                    <Switch
                      id="unsubscribe-link"
                      isChecked={addUnsubscribeLink}
                      onChange={(e) => setAddUnsubscribeLink(e.target.checked)}
                    />
                  </FormControl>
                </VStack>
              </Box>

              <Box>
                <Text fontSize="2xl" fontWeight="bold" color="gray.700">
                  Attachments & Media
                </Text>
                
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
                    placeholder="Enter comma-separated URLs (e.g., https://example.com/image.jpg, https://example.com/doc.pdf)"
                  />
                </FormControl>
              </Box>
            </VStack>
          </GridItem>
        </Grid>

        <HStack justify="flex-end" spacing={4}>
          <Button
            colorScheme="blue"
            size="lg"
            isLoading={isLoading}
            onClick={handleSendEmail}
            isDisabled={!selectedTemplate || recipients.length === 0}
          >
            Send Email
          </Button>
        </HStack>
      </VStack>
    </Container>
  );
};

export default SendEmailPage; 