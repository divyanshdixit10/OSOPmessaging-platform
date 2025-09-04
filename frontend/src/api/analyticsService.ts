const API_BASE_URL = 'http://localhost:8080/api';

export interface AnalyticsFilters {
  startDate?: string;
  endDate?: string;
  granularity?: string;
}

export class AnalyticsService {
  private static async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
    return response.json();
  }

  static async getDashboardStats(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/dashboard?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getOverviewMetrics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/overview?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getEngagementMetrics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/engagement?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getCampaignPerformance(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/campaigns?${params.toString()}`);
    return this.handleResponse<Record<string, any>>(response);
  }

  static async getSubscriberAnalytics(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    const params = new URLSearchParams();
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(`${API_BASE_URL}/analytics/subscribers?${params.toString()}`);
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

  // Fallback mock data for development when API is not available
  private static getMockDashboardStats(): Record<string, any> {
    return {
      totalEmailsSent: 12470,
      activeSubscribers: 8234,
      openRate: 24.8,
      clickRate: 3.2,
      totalCampaigns: 45,
      activeCampaigns: 3,
      recentActivity: [
        { type: 'email', title: 'Newsletter Campaign', description: 'Sent to 2,450 subscribers', time: '2 hours ago', status: 'success' },
        { type: 'template', title: 'Welcome Email Template', description: 'Template created and saved', time: '4 hours ago', status: 'success' },
        { type: 'campaign', title: 'Product Launch Campaign', description: 'Campaign scheduled for tomorrow', time: '6 hours ago', status: 'pending' }
      ]
    };
  }

  private static getMockOverviewMetrics(): Record<string, any> {
    return {
      totalEmailsSent: 8000,
      openRate: 26.8,
      clickRate: 4.2,
      bounceRate: 2.1,
      totalRecipients: 10000,
      deliveredRate: 97.9,
      unsubscribeRate: 0.3,
      spamComplaintRate: 0.02
    };
  }

  private static getMockEngagementMetrics(): Record<string, any> {
    return {
      averageTimeToOpen: '2.4 hrs',
      clickToOpenRate: 15.7,
      unsubscribeRate: 0.3,
      spamComplaints: 0.02,
      forwardRate: 1.2,
      printRate: 0.5,
      engagementScore: 78.5
    };
  }

  // Enhanced error handling with fallback to mock data
  static async getDashboardStatsWithFallback(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    try {
      return await this.getDashboardStats(filters);
    } catch (error) {
      console.warn('API call failed, using mock data:', error);
      return this.getMockDashboardStats();
    }
  }

  static async getOverviewMetricsWithFallback(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    try {
      return await this.getOverviewMetrics(filters);
    } catch (error) {
      console.warn('API call failed, using mock data:', error);
      return this.getMockOverviewMetrics();
    }
  }

  static async getEngagementMetricsWithFallback(filters: AnalyticsFilters = {}): Promise<Record<string, any>> {
    try {
      return await this.getEngagementMetrics(filters);
    } catch (error) {
      console.warn('API call failed, using mock data:', error);
      return this.getMockEngagementMetrics();
    }
  }
}

export default AnalyticsService;
