export type MessageChannel = 'EMAIL' | 'WHATSAPP' | 'SMS';

export interface MessageRequest {
  channel: MessageChannel;
  recipients: string[];
  subject?: string;
  message: string;
  mediaUrls?: string[];
  attachments?: File[];
  trackOpens?: boolean;
  trackClicks?: boolean;
  addUnsubscribeLink?: boolean;
}

export interface MessageResponse {
  status: string;
  channel: MessageChannel;
  recipients: string[];
  details: { [email: string]: 'SENT' | 'FAILED' | 'PENDING' | 'DELIVERED' };
  errorMessage?: string;
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