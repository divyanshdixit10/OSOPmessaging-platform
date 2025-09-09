import { apiClient } from './client';
import { AxiosResponse } from 'axios';

export interface WebhookEndpoint {
  id: number;
  url: string;
  name: string;
  description?: string;
  events: string[];
  secretKey?: string;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface WebhookEndpointRequest {
  url: string;
  name: string;
  description?: string;
  events: string[];
  secretKey?: string;
  enabled?: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
}

export const webhookService = {
  // Create a new webhook endpoint
  async createWebhook(request: WebhookEndpointRequest): Promise<ApiResponse<WebhookEndpoint>> {
    try {
      const response: AxiosResponse<WebhookEndpoint> = await apiClient.post('/webhooks', request);
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to create webhook endpoint'
      };
    }
  },

  // Get all webhook endpoints
  async getAllWebhooks(): Promise<ApiResponse<WebhookEndpoint[]>> {
    try {
      const response: AxiosResponse<WebhookEndpoint[]> = await apiClient.get('/webhooks');
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to fetch webhook endpoints'
      };
    }
  },

  // Get webhook endpoint by ID
  async getWebhookById(id: number): Promise<ApiResponse<WebhookEndpoint>> {
    try {
      const response: AxiosResponse<WebhookEndpoint> = await apiClient.get(`/webhooks/${id}`);
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to fetch webhook endpoint'
      };
    }
  },

  // Update webhook endpoint
  async updateWebhook(id: number, request: WebhookEndpointRequest): Promise<ApiResponse<WebhookEndpoint>> {
    try {
      const response: AxiosResponse<WebhookEndpoint> = await apiClient.put(`/webhooks/${id}`, request);
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to update webhook endpoint'
      };
    }
  },

  // Delete webhook endpoint
  async deleteWebhook(id: number): Promise<ApiResponse<void>> {
    try {
      await apiClient.delete(`/webhooks/${id}`);
      return {
        success: true
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to delete webhook endpoint'
      };
    }
  },

  // Toggle webhook endpoint status (enable/disable)
  async toggleWebhookStatus(id: number, enabled: boolean): Promise<ApiResponse<WebhookEndpoint>> {
    try {
      const response: AxiosResponse<WebhookEndpoint> = await apiClient.post(`/webhooks/${id}/toggle`, { enabled });
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to update webhook status'
      };
    }
  },

  // Get available webhook events
  async getAvailableEvents(): Promise<ApiResponse<string[]>> {
    try {
      const response: AxiosResponse<string[]> = await apiClient.get('/webhooks/events');
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to fetch available events'
      };
    }
  }
};
