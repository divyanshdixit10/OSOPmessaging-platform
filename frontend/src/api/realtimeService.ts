import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

// WebSocket connection for real-time updates
class RealTimeService {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectInterval = 3000;
  private listeners: Map<string, Function[]> = new Map();

  constructor() {
    this.connect();
  }

  private connect() {
    try {
      const wsUrl = `ws://localhost:8080/ws`;
      this.ws = new WebSocket(wsUrl);

      this.ws.onopen = () => {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        this.emit('connected', {});
      };

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          this.emit('message', data);
          
          // Emit specific event types
          if (data.type) {
            this.emit(data.type, data);
          }
        } catch (error) {
          console.error('Failed to parse WebSocket message:', error);
        }
      };

      this.ws.onclose = () => {
        console.log('WebSocket disconnected');
        this.emit('disconnected', {});
        this.reconnect();
      };

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error);
        this.emit('error', error);
      };

    } catch (error) {
      console.error('Failed to connect WebSocket:', error);
      this.reconnect();
    }
  }

  private reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect WebSocket (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      
      setTimeout(() => {
        this.connect();
      }, this.reconnectInterval);
    } else {
      console.error('Max WebSocket reconnection attempts reached');
      this.emit('reconnect_failed', {});
    }
  }

  public subscribe(event: string, callback: Function) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, []);
    }
    this.listeners.get(event)!.push(callback);
  }

  public unsubscribe(event: string, callback: Function) {
    const callbacks = this.listeners.get(event);
    if (callbacks) {
      const index = callbacks.indexOf(callback);
      if (index > -1) {
        callbacks.splice(index, 1);
      }
    }
  }

  private emit(event: string, data: any) {
    const callbacks = this.listeners.get(event);
    if (callbacks) {
      callbacks.forEach(callback => {
        try {
          callback(data);
        } catch (error) {
          console.error(`Error in WebSocket listener for ${event}:`, error);
        }
      });
    }
  }

  public send(message: any) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message));
    } else {
      console.warn('WebSocket is not connected');
    }
  }

  public disconnect() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }
}

// Create singleton instance
const realTimeService = new RealTimeService();

// API methods for real-time analytics
export const RealTimeAnalyticsService = {
  // Get delivery status for a specific email event
  async getDeliveryStatus(emailEventId: number): Promise<any> {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/analytics/realtime/delivery-status/${emailEventId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to get delivery status:', error);
      throw error;
    }
  },

  // Get campaign statistics
  async getCampaignStats(campaignId: number): Promise<any> {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/analytics/realtime/campaign-stats/${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to get campaign stats:', error);
      throw error;
    }
  },

  // Get bounced emails
  async getBouncedEmails(hours: number = 24): Promise<string[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/analytics/realtime/bounced-emails?hours=${hours}`);
      return response.data;
    } catch (error) {
      console.error('Failed to get bounced emails:', error);
      throw error;
    }
  },

  // Get live statistics
  async getLiveStats(): Promise<any> {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/analytics/realtime/live-stats`);
      return response.data;
    } catch (error) {
      console.error('Failed to get live stats:', error);
      throw error;
    }
  },

  // Get email reputation
  async getEmailReputation(email: string): Promise<any> {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/analytics/realtime/email-reputation/${email}`);
      return response.data;
    } catch (error) {
      console.error('Failed to get email reputation:', error);
      throw error;
    }
  }
};

// WebSocket service for real-time updates
export const WebSocketService = {
  // Subscribe to real-time updates
  subscribe: (event: string, callback: Function) => {
    realTimeService.subscribe(event, callback);
  },

  // Unsubscribe from real-time updates
  unsubscribe: (event: string, callback: Function) => {
    realTimeService.unsubscribe(event, callback);
  },

  // Send message via WebSocket
  send: (message: any) => {
    realTimeService.send(message);
  },

  // Subscribe to email updates
  subscribeToEmailUpdates: (callback: Function) => {
    realTimeService.subscribe('email_update', callback);
  },

  // Subscribe to analytics updates
  subscribeToAnalytics: (callback: Function) => {
    realTimeService.subscribe('analytics_update', callback);
  },

  // Subscribe to dashboard updates
  subscribeToDashboard: (callback: Function) => {
    realTimeService.subscribe('dashboard_update', callback);
  },

  // Request analytics data
  requestAnalytics: () => {
    realTimeService.send({
      type: 'analytics.request',
      data: {}
    });
  },

  // Subscribe to dashboard
  subscribeToDashboardUpdates: () => {
    realTimeService.send({
      type: 'dashboard.subscribe',
      data: {}
    });
  }
};

export default realTimeService;
