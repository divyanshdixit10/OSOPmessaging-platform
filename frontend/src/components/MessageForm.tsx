import React, { useState } from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import {
  Box,
  VStack,
  FormControl,
  FormLabel,
  Input,
  Textarea,
  Button,
  useToast,
  FormErrorMessage,
  Text,
} from '@chakra-ui/react';
import { MessageChannel, MessageRequest } from '../types/message';
import ChannelSelector from './ChannelSelector';
import FileUploader from './FileUploader';
import { sendMessage } from '../api/messageService';

interface FormValues {
  channel: MessageChannel;
  recipients: string;
  subject: string;
  message: string;
  mediaUrls: string;
}

const validationSchema = Yup.object().shape({
  channel: Yup.string().required('Please select a channel'),
  recipients: Yup.string()
    .required('Recipients are required')
    .test('recipients', 'Invalid format', function(value: string | undefined) {
      if (!value) return false;
      const recipients = value.split(',').map((r: string) => r.trim());
      const channel = this.parent.channel as MessageChannel;
      
      const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
      const phoneRegex = /^\+?[1-9]\d{1,14}$/;
      
      if (channel === 'EMAIL') {
        return recipients.every((r: string) => emailRegex.test(r));
      } else {
        return recipients.every((r: string) => phoneRegex.test(r));
      }
    }),
  subject: Yup.string().when('channel', {
    is: 'EMAIL',
    then: (schema: Yup.StringSchema) => schema.required('Subject is required for email'),
    otherwise: (schema: Yup.StringSchema) => schema.optional(),
  }),
  message: Yup.string().required('Message is required'),
  mediaUrls: Yup.string(),
});

const MessageForm: React.FC = () => {
  const [files, setFiles] = useState<File[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const toast = useToast();

  const formik = useFormik<FormValues>({
    initialValues: {
      channel: 'EMAIL',
      recipients: '',
      subject: '',
      message: '',
      mediaUrls: '',
    },
    validationSchema,
    onSubmit: async (values: FormValues) => {
      try {
        setIsLoading(true);
        const messageData: MessageRequest = {
          channel: values.channel,
          recipients: values.recipients.split(',').map((r: string) => r.trim()),
          message: values.message,
          attachments: files,
          mediaUrls: values.mediaUrls ? values.mediaUrls.split(',').map((url: string) => url.trim()) : undefined,
        };

        if (values.channel === 'EMAIL') {
          messageData.subject = values.subject;
        }

        const response = await sendMessage(messageData);
        
        toast({
          title: 'Message sent successfully!',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
        
        formik.resetForm();
        setFiles([]);
      } catch (error) {
        toast({
          title: 'Error sending message',
          description: error instanceof Error ? error.message : 'An unexpected error occurred',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      } finally {
        setIsLoading(false);
      }
    },
  });

  return (
    <Box
      as="form"
      onSubmit={formik.handleSubmit}
      bg="white"
      p={6}
      borderRadius="xl"
      shadow="xl"
      maxW="800px"
      mx="auto"
      w="100%"
    >
      <VStack spacing={6}>
        <Text fontSize="2xl" fontWeight="bold" color="gray.700">
          Send Message
        </Text>

        <FormControl>
          <FormLabel>Channel</FormLabel>
          <ChannelSelector
            value={formik.values.channel}
            onChange={(value) => formik.setFieldValue('channel', value)}
          />
        </FormControl>

        <FormControl isInvalid={!!formik.errors.recipients && formik.touched.recipients}>
          <FormLabel>Recipients ({formik.values.channel === 'EMAIL' ? 'Email' : 'Phone'} addresses)</FormLabel>
          <Input
            name="recipients"
            placeholder={formik.values.channel === 'EMAIL' ? 'email1@example.com, email2@example.com' : '+1234567890, +0987654321'}
            value={formik.values.recipients}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          <FormErrorMessage>{formik.errors.recipients}</FormErrorMessage>
        </FormControl>

        {formik.values.channel === 'EMAIL' && (
          <FormControl isInvalid={!!formik.errors.subject && formik.touched.subject}>
            <FormLabel>Subject</FormLabel>
            <Input
              name="subject"
              placeholder="Enter email subject"
              value={formik.values.subject}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            <FormErrorMessage>{formik.errors.subject}</FormErrorMessage>
          </FormControl>
        )}

        <FormControl isInvalid={!!formik.errors.message && formik.touched.message}>
          <FormLabel>Message</FormLabel>
          <Textarea
            name="message"
            placeholder="Type your message here..."
            value={formik.values.message}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            rows={5}
          />
          <FormErrorMessage>{formik.errors.message}</FormErrorMessage>
        </FormControl>

        <FormControl>
          <FormLabel>Attachments</FormLabel>
          <FileUploader files={files} onFilesChange={setFiles} />
        </FormControl>

        <FormControl>
          <FormLabel>Media URLs (Optional)</FormLabel>
          <Input
            name="mediaUrls"
            placeholder="https://example.com/image.jpg, https://example.com/document.pdf"
            value={formik.values.mediaUrls}
            onChange={formik.handleChange}
          />
        </FormControl>

        <Button
          type="submit"
          colorScheme="blue"
          size="lg"
          width="100%"
          isLoading={isLoading}
          loadingText="Sending..."
        >
          Send Message
        </Button>
      </VStack>
    </Box>
  );
};

export default MessageForm; 