const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

export interface AnalyticsFilters {
  startDate?: string;
  endDate?: string;
  granularity?: string;
  campaignId?: string;
  subscriberId?: string;
}

export interface DashboardStats {
  totalEmailsSent: number;
  activeSubscribers: number;
  openRate: number;
  clickRate: number;
  totalCampaigns: number;
  activeCampaigns: number;
  recentActivity: Array<{
    type: string;
    title: string;
    description: string;
    time: string;
    status: string;
  }>;
}

export interface OverviewMetrics {
  totalEmailsSent: number;
  openRate: number;
  clickRate: number;
  bounceRate: number;
  totalRecipients: number;
  deliveredRate: number;
  unsubscribeRate: number;
  spamComplaintRate: number;
}

export class AnalyticsService {
  private static async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
    return response.json();
  }

  static async getDashboardStats(filters: AnalyticsFilters = {}): Promise<DashboardStats> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/dashboard?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<DashboardStats>(response);
  }

  static async getOverviewMetrics(filters: AnalyticsFilters = {}): Promise<OverviewMetrics> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/overview?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<OverviewMetrics>(response);
  }

  static async getEngagementMetrics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/engagement?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getCampaignPerformance(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/campaigns?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getSubscriberAnalytics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/subscribers?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getPerformanceTrends(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);
    if (filters.granularity) params.append('granularity', filters.granularity);

    const response = await fetch(`${API_BASE_URL}/analytics/trends?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getGeographicAnalytics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/geographic?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getDeviceAnalytics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/device?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getBrowserAnalytics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/browser?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getTimeBasedAnalytics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/time?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async exportAnalytics(filters: AnalyticsFilters = {}, format: string = 'json'): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);
    params.append('format', format);

    const response = await fetch(`${API_BASE_URL}/analytics/export?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getRealTimeAnalytics(): Promise<Record<string, any>> {
    const response = await fetch(`${API_BASE_URL}/analytics/realtime`);
    return this.handleResponse<Record<string, any>>(response);
  }


  // Real backend data only - no fallbacks
  static async getDashboardStatsReal(filters: AnalyticsFilters = {}): Promise<DashboardStats> {
    return await this.getDashboardStats(filters);
  }

  static async getOverviewMetricsReal(filters: AnalyticsFilters = {}): Promise<OverviewMetrics> {
    return await this.getOverviewMetrics(filters);
  }

  static async getEngagementMetricsReal(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    return await this.getEngagementMetrics(filters);
  }

  // Real-time data fetching - backend only
  static async getRealTimeDashboardStats(filters: AnalyticsFilters = {}): Promise<DashboardStats> {
    const response = await fetch(`${API_BASE_URL}/analytics/realtime/dashboard?${new URLSearchParams(filters as any).toString()}`);
    return this.handleResponse<DashboardStats>(response);
  }

  // Live campaign tracking - backend only
  static async getLiveCampaignStats(campaignId: string): Promise<Record<string, any>> {
    const response = await fetch(`${API_BASE_URL}/analytics/campaigns/${campaignId}/live`);
    return this.handleResponse<Record<string, any>>(response);
  }

  // Live subscriber tracking - backend only
  static async getLiveSubscriberStats(): Promise<Record<string, any>> {
    const response = await fetch(`${API_BASE_URL}/analytics/subscribers/live`);
    return this.handleResponse<Record<string, any>>(response);
  }
}

export default AnalyticsService;
