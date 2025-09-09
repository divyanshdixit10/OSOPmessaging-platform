const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

export interface SendCampaignRequest {
  campaignId: number;
  userId: string;
  batchSize?: number;
  rateLimitPerMinute?: number;
}

export interface ScheduleCampaignRequest {
  campaignId: number;
  userId: string;
  scheduledTime: string;
  batchSize?: number;
  rateLimitPerMinute?: number;
}

export interface CampaignProgress {
  campaignId: number;
  status: string;
  totalRecipients: number;
  emailsSent: number;
  emailsSuccess: number;
  emailsFailed: number;
  emailsInProgress: number;
  progressPercentage: number;
  successRate: number;
  failureRate: number;
  currentBatchNumber: number;
  totalBatches: number;
  scheduledTime?: string;
  startedAt?: string;
  completedAt?: string;
  lastBatchSentAt?: string;
  errorMessage?: string;
}

export interface CampaignAnalytics {
  id: number;
  name: string;
  description: string;
  status: string;
  totalRecipients: number;
  sentCount: number;
  deliveredCount: number;
  openedCount: number;
  clickedCount: number;
  bouncedCount: number;
  unsubscribedCount: number;
  openRate: number;
  clickRate: number;
  bounceRate: number;
  unsubscribeRate: number;
  progressPercentage: number;
  createdAt: string;
  startedAt?: string;
  completedAt?: string;
}

export class CampaignService {
  private static async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
    return response.json();
  }

  // Send campaign immediately
  static async sendCampaign(request: SendCampaignRequest): Promise<{ success: boolean; message: string; campaignId: number }> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/send`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(request)
    });
    return this.handleResponse<{ success: boolean; message: string; campaignId: number }>(response);
  }

  // Schedule campaign for later
  static async scheduleCampaign(request: ScheduleCampaignRequest): Promise<{ success: boolean; message: string; campaignId: number; scheduledTime: string }> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/schedule`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(request)
    });
    return this.handleResponse<{ success: boolean; message: string; campaignId: number; scheduledTime: string }>(response);
  }

  // Get campaign progress
  static async getCampaignProgress(campaignId: number): Promise<CampaignProgress> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/${campaignId}/progress`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignProgress>(response);
  }

  // Pause campaign
  static async pauseCampaign(campaignId: number): Promise<{ success: boolean; message: string }> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/${campaignId}/pause`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<{ success: boolean; message: string }>(response);
  }

  // Resume campaign
  static async resumeCampaign(campaignId: number): Promise<{ success: boolean; message: string }> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/${campaignId}/resume`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<{ success: boolean; message: string }>(response);
  }

  // Cancel campaign
  static async cancelCampaign(campaignId: number): Promise<{ success: boolean; message: string }> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/${campaignId}/cancel`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<{ success: boolean; message: string }>(response);
  }

  // Retry failed emails
  static async retryFailedEmails(campaignId: number): Promise<{ success: boolean; message: string }> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/${campaignId}/retry-failed`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<{ success: boolean; message: string }>(response);
  }

  // Get campaign analytics
  static async getCampaignAnalytics(campaignId: number): Promise<CampaignAnalytics> {
    const response = await fetch(`${API_BASE_URL}/campaigns/enhanced/${campaignId}/analytics`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignAnalytics>(response);
  }
}

export default CampaignService;