import axios from './axios';
import { AxiosError } from 'axios';
import { MessageRequest, MessageResponse } from '../types/message';
import { BulkEmailRequest } from '../types/EmailRequest';

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Accept': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

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

  // Append tracking options
  if (data.trackOpens !== undefined) {
    formData.append('trackOpens', data.trackOpens.toString());
  }
  if (data.trackClicks !== undefined) {
    formData.append('trackClicks', data.trackClicks.toString());
  }
  if (data.addUnsubscribeLink !== undefined) {
    formData.append('addUnsubscribeLink', data.addUnsubscribeLink.toString());
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
      '/message/send-with-attachment',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
          ...getAuthHeaders()
        }
      }
    );

    console.log('Message response:', response.data);
    
    // Check if the response indicates any failures
    if (response.data.status === 'FAILED') {
      const failedRecipients = Object.entries(response.data.details || {})
        .filter(([_, status]) => status === 'FAILED')
        .map(([email, _]) => email);
      throw new Error(`Failed to send emails to: ${failedRecipients.join(', ')}`);
    } else if (response.data.status === 'PARTIAL') {
      const failedRecipients = Object.entries(response.data.details || {})
        .filter(([_, status]) => status === 'FAILED')
        .map(([email, _]) => email);
      console.warn('Partial success - some emails failed:', failedRecipients);
    }
    
    return response.data;

  } catch (error) {
    throw new Error(error instanceof Error ? error.message : 'Failed to send message');
  }
};

export const sendBulkEmail = async (formData: FormData): Promise<MessageResponse> => {
  try {
    const response = await axios.post<MessageResponse>(
      '/message/email/bulk',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
          ...getAuthHeaders()
        }
      }
    );
    
    console.log('Bulk email response:', response.data);
    
    // Check if the response indicates any failures
    if (response.data.status === 'FAILED') {
      const failedRecipients = Object.entries(response.data.details || {})
        .filter(([_, status]) => status === 'FAILED')
        .map(([email, _]) => email);
      throw new Error(`Failed to send emails to: ${failedRecipients.join(', ')}`);
    } else if (response.data.status === 'PARTIAL') {
      const failedRecipients = Object.entries(response.data.details || {})
        .filter(([_, status]) => status === 'FAILED')
        .map(([email, _]) => email);
      console.warn('Partial success - some emails failed:', failedRecipients);
    }
    
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