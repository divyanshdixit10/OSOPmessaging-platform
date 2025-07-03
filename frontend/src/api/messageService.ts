import axios from './axios';
import { AxiosError } from 'axios';
import { MessageRequest, MessageResponse } from '../types/message';
import { BulkEmailRequest } from '../types/EmailRequest';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  headers: {
    'Accept': 'application/json'
  },
  auth: {
    username: 'admin',
    password: 'admin123'
  },
  withCredentials: true
});

export const sendMessage = async (data: MessageRequest): Promise<MessageResponse> => {
  // Validate required fields
  if (!data.channel) throw new Error('Channel is required');
  if (!data.recipients?.length) throw new Error('At least one recipient is required');
  if (!data.message) throw new Error('Message content is required');
  if (data.channel === 'EMAIL' && !data.subject) throw new Error('Subject is required for email messages');

  const formData = new FormData();

  // Append basic fields
  formData.append('channel', data.channel);
  
  // Append recipients as individual entries
  data.recipients.forEach(recipient => {
    formData.append('recipients', recipient.trim());
  });

  // Append optional subject for email
  if (data.subject) {
    formData.append('subject', data.subject);
  }

  // Append message content
  formData.append('message', data.message);

  // Append media URLs if present
  if (data.mediaUrls?.length) {
    data.mediaUrls.forEach(url => {
      formData.append('mediaUrls', url.trim());
    });
  }

  // Append file attachments if present
  if (data.attachments?.length) {
    data.attachments.forEach(file => {
      formData.append('attachments', file);
    });
  }

  try {
    console.log('Sending message:', {
      channel: data.channel,
      recipients: data.recipients,
      subject: data.subject,
      messageLength: data.message.length,
      mediaUrlsCount: data.mediaUrls?.length || 0,
      attachmentsCount: data.attachments?.length || 0
    });

    const response = await axios.post<MessageResponse>(
      '/api/message/send-with-attachment',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    );

    console.log('Message sent successfully:', response.data);
    return response.data;

  } catch (error) {
    throw new Error(error instanceof Error ? error.message : 'Failed to send message');
  }
};

export const sendBulkEmail = async (formData: FormData): Promise<MessageResponse> => {
  try {
    const response = await axios.post<MessageResponse>(
      '/api/message/email/bulk',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    );
    return response.data;
  } catch (error) {
    if (error instanceof AxiosError) {
      // Handle specific error cases
      if (error.response?.status === 400) {
        throw new Error('Invalid request: Please check your input data');
      } else if (error.response?.status === 500) {
        throw new Error('Server error: Please try again later');
      }
      throw new Error(error.response?.data?.message || error.message);
    }
    throw new Error('Failed to send bulk email');
  }
}; 