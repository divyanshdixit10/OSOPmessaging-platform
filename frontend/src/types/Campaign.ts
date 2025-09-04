export interface CampaignDto {
  id: number;
  name: string;
  description?: string;
  subject: string;
  body: string;
  templateId?: number;
  templateName?: string;
  status: CampaignStatus;
  channel: MessageChannel;
  totalRecipients?: number;
  sentCount?: number;
  deliveredCount?: number;
  openedCount?: number;
  clickedCount?: number;
  bouncedCount?: number;
  unsubscribedCount?: number;
  scheduledAt?: string | null;
  startedAt?: string | null;
  completedAt?: string | null;
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
  trackOpens?: boolean;
  trackClicks?: boolean;
  addUnsubscribeLink?: boolean;
  isDraft?: boolean;
  isTest?: boolean;
  testEmails?: string[];
  openRate?: number | null;
  clickRate?: number | null;
  bounceRate?: number | null;
  unsubscribeRate?: number | null;
  recipients?: string[];
}

export enum CampaignStatus {
  DRAFT = 'DRAFT',
  SCHEDULED = 'SCHEDULED',
  RUNNING = 'RUNNING',
  PAUSED = 'PAUSED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  FAILED = 'FAILED'
}

export enum MessageChannel {
  EMAIL = 'EMAIL',
  SMS = 'SMS',
  WHATSAPP = 'WHATSAPP'
}

export interface CampaignFormData {
  name: string;
  description?: string;
  subject: string;
  body: string;
  templateId?: number;
  channel: MessageChannel;
  totalRecipients?: number;
  scheduledAt?: string;
  trackOpens?: boolean;
  trackClicks?: boolean;
  addUnsubscribeLink?: boolean;
  isDraft?: boolean;
  isTest?: boolean;
  testEmails?: string[];
  recipients?: string[];
}

export interface CampaignStats {
  totalCampaigns: number;
  activeCampaigns: number;
  completedCampaigns: number;
  totalEmailsSent: number;
  averageOpenRate: number;
  averageClickRate: number;
  totalSubscribers: number;
  recentActivity: CampaignActivity[];
}

export interface CampaignActivity {
  id: number;
  name: string;
  status: CampaignStatus;
  action: string;
  timestamp: string;
  user: string;
}
