export interface Recipient {
  email: string;
  name?: string;
  variables?: Record<string, string>;
  isValid?: boolean;
}

export interface EmailGroup {
  id?: string;
  name: string;
  recipients: Recipient[];
  createdAt?: string;
}

export interface BulkEmailRequest {
  templateId?: string;
  subject: string;
  body: string;
  recipients: Recipient[];
  cc?: string[];
  bcc?: string[];
  attachments?: File[];
  mediaUrls?: string[];
  trackOpens?: boolean;
  trackClicks?: boolean;
  scheduledTime?: string;
  addUnsubscribeLink?: boolean;
  senderProfile?: string;
  placeholders?: Record<string, string>;
}

export interface EmailLog {
  id: string;
  subject: string;
  recipients: Recipient[];
  status: 'SENT' | 'FAILED' | 'PENDING';
  error?: string;
  sentAt: string;
  templateId?: string;
  attachmentCount: number;
}

export interface EmailTemplate {
  id?: string;
  name: string;
  subject: string;
  body: string;
  placeholders?: string[];
  createdAt?: string;
  updatedAt?: string;
} 