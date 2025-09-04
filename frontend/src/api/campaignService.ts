import { CampaignDto, CampaignStatus, MessageChannel } from '../types/Campaign';

const API_BASE_URL = 'http://localhost:8080/api';

export interface CampaignFilters {
  name?: string;
  status?: string;
  channel?: string;
  isDraft?: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export class CampaignService {
  private static async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
    return response.json();
  }

  static async getCampaigns(filters: CampaignFilters = {}, page: number = 0, size: number = 20): Promise<PaginatedResponse<CampaignDto>> {
    const params = new URLSearchParams();
    
    if (filters.name) params.append('name', filters.name);
    if (filters.status) params.append('status', filters.status);
    if (filters.channel) params.append('channel', filters.channel);
    if (filters.isDraft !== undefined) params.append('isDraft', filters.isDraft.toString());
    
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await fetch(`${API_BASE_URL}/campaigns?${params.toString()}`);
    return this.handleResponse<PaginatedResponse<CampaignDto>>(response);
  }

  static async getCampaignById(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}`);
    return this.handleResponse<CampaignDto>(response);
  }

  static async createCampaign(campaign: CampaignDto): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(campaign),
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async updateCampaign(id: string, campaign: CampaignDto): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(campaign),
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async deleteCampaign(id: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
  }

  static async startCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/start`, {
      method: 'POST',
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async pauseCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/pause`, {
      method: 'POST',
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async resumeCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/resume`, {
      method: 'POST',
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async cancelCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/cancel`, {
      method: 'POST',
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async getCampaignStats(id: string): Promise<Record<string, any>> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/stats`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getOverallStats(): Promise<Record<string, any>> {
    const response = await fetch(`${API_BASE_URL}/campaigns/stats/overview`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getCampaignsByStatus(status: string): Promise<CampaignDto[]> {
    const response = await fetch(`${API_BASE_URL}/campaigns/status/${status}`);
    return this.handleResponse<CampaignDto[]>(response);
  }

  static async getScheduledCampaigns(): Promise<CampaignDto[]> {
    const response = await fetch(`${API_BASE_URL}/campaigns/scheduled`);
    return this.handleResponse<CampaignDto[]>(response);
  }

  static async getRunningCampaigns(): Promise<CampaignDto[]> {
    const response = await fetch(`${API_BASE_URL}/campaigns/running`);
    return this.handleResponse<CampaignDto[]>(response);
  }

  // Fallback mock data for development when API is not available
  private static getMockCampaigns(): CampaignDto[] {
    return [
      {
        id: 1,
        name: 'Welcome Series Campaign',
        description: 'Welcome emails for new subscribers',
        subject: 'Welcome to Our Platform!',
        body: 'Welcome to our platform! We\'re excited to have you on board.',
        templateId: 1,
        templateName: 'Welcome Email',
        status: CampaignStatus.RUNNING,
        channel: MessageChannel.EMAIL,
        totalRecipients: 1000,
        sentCount: 750,
        deliveredCount: 720,
        openedCount: 180,
        clickedCount: 45,
        bouncedCount: 30,
        unsubscribedCount: 5,
        scheduledAt: null,
        startedAt: '2024-01-15T10:00:00Z',
        completedAt: null,
        createdBy: 'Marketing Team',
        createdAt: '2024-01-15T09:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z',
        trackOpens: true,
        trackClicks: true,
        addUnsubscribeLink: true,
        isDraft: false,
        isTest: false,
        testEmails: [],
        openRate: 25.0,
        clickRate: 6.25,
        bounceRate: 4.17,
        unsubscribeRate: 0.69,
        recipients: ['user1@example.com', 'user2@example.com']
      },
      {
        id: 2,
        name: 'Product Launch Announcement',
        description: 'Announcement for new product launch',
        subject: 'New Product Launch - Check It Out!',
        body: 'We\'re excited to announce our new product!',
        templateId: 2,
        templateName: 'Newsletter Template',
        status: CampaignStatus.SCHEDULED,
        channel: MessageChannel.EMAIL,
        totalRecipients: 2000,
        sentCount: 0,
        deliveredCount: 0,
        openedCount: 0,
        clickedCount: 0,
        bouncedCount: 0,
        unsubscribedCount: 0,
        scheduledAt: '2024-01-20T14:00:00Z',
        startedAt: null,
        completedAt: null,
        createdBy: 'Product Team',
        createdAt: '2024-01-16T09:00:00Z',
        updatedAt: '2024-01-16T09:00:00Z',
        trackOpens: true,
        trackClicks: true,
        addUnsubscribeLink: true,
        isDraft: false,
        isTest: false,
        testEmails: [],
        openRate: null,
        clickRate: null,
        bounceRate: null,
        unsubscribeRate: null,
        recipients: ['user3@example.com', 'user4@example.com']
      }
    ];
  }

  // Enhanced error handling with fallback to mock data
  static async getCampaignsWithFallback(filters: CampaignFilters = {}, page: number = 0, size: number = 20): Promise<PaginatedResponse<CampaignDto>> {
    try {
      return await this.getCampaigns(filters, page, size);
    } catch (error) {
      console.warn('API call failed, using mock data:', error);
      const mockCampaigns = this.getMockCampaigns();
      const filteredCampaigns = mockCampaigns.filter(campaign => {
        if (filters.name && !campaign.name.toLowerCase().includes(filters.name.toLowerCase())) return false;
        if (filters.status && campaign.status !== filters.status) return false;
        if (filters.channel && campaign.channel !== filters.channel) return false;
        if (filters.isDraft !== undefined && campaign.isDraft !== filters.isDraft) return false;
        return true;
      });
      
      const startIndex = page * size;
      const endIndex = startIndex + size;
      const content = filteredCampaigns.slice(startIndex, endIndex);
      
      return {
        content,
        totalElements: filteredCampaigns.length,
        totalPages: Math.ceil(filteredCampaigns.length / size),
        size,
        number: page
      };
    }
  }
}

export default CampaignService;
