import { CampaignDto, CampaignStatus, MessageChannel } from '../types/Campaign';

const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

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

    const response = await fetch(`${API_BASE_URL}/campaigns?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<PaginatedResponse<CampaignDto>>(response);
  }

  static async getCampaignById(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async createCampaign(campaign: CampaignDto): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(campaign),
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async updateCampaign(id: string, campaign: CampaignDto): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(campaign),
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async deleteCampaign(id: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
  }

  static async startCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/start`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async pauseCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/pause`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async resumeCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/resume`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async cancelCampaign(id: string): Promise<CampaignDto> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/cancel`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto>(response);
  }

  static async getCampaignStats(id: string): Promise<Record<string, any>> {
    const response = await fetch(`${API_BASE_URL}/campaigns/${id}/stats`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getOverallStats(): Promise<Record<string, any>> {
    const response = await fetch(`${API_BASE_URL}/campaigns/stats/overview`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getCampaignsByStatus(status: string): Promise<CampaignDto[]> {
    const response = await fetch(`${API_BASE_URL}/campaigns/status/${status}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto[]>(response);
  }

  static async getScheduledCampaigns(): Promise<CampaignDto[]> {
    const response = await fetch(`${API_BASE_URL}/campaigns/scheduled`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto[]>(response);
  }

  static async getRunningCampaigns(): Promise<CampaignDto[]> {
    const response = await fetch(`${API_BASE_URL}/campaigns/running`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<CampaignDto[]>(response);
  }


  // Real backend data only - no fallbacks
  static async getCampaignsReal(filters: CampaignFilters = {}, page: number = 0, size: number = 20): Promise<PaginatedResponse<CampaignDto>> {
    return await this.getCampaigns(filters, page, size);
  }
}

export default CampaignService;
