export type MessageChannel = 'EMAIL' | 'WHATSAPP' | 'SMS';

export interface MessageRequest {
  channel: MessageChannel;
  recipients: string[];
  subject?: string;
  message: string;
  mediaUrls?: string[];
  attachments?: File[];
}

export interface MessageResponse {
  messageId: string;
  status: string;
  message: string;
  timestamp: string;
}

export interface MessageHistory {
  id: string;
  channel: MessageChannel;
  recipients: string[];
  subject?: string;
  message: string;
  status: 'SUCCESS' | 'FAILED';
  timestamp: string;
} 