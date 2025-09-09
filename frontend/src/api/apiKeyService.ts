import { apiClient } from './client';
import { AxiosResponse } from 'axios';

export interface ApiKey {
  id: number;
  apiKey: string;
  name: string;
  description?: string;
  enabled: boolean;
  expiresAt?: string;
  lastUsedAt?: string;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateApiKeyRequest {
  name: string;
  description?: string;
  expiresAt?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
}

export const apiKeyService = {
  // Create a new API key
  async createApiKey(request: CreateApiKeyRequest): Promise<ApiResponse<ApiKey>> {
    try {
      const response: AxiosResponse<ApiKey> = await apiClient.post('/api-keys', request);
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to create API key'
      };
    }
  },

  // Get all API keys
  async getAllApiKeys(): Promise<ApiResponse<ApiKey[]>> {
    try {
      const response: AxiosResponse<ApiKey[]> = await apiClient.get('/api-keys');
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to fetch API keys'
      };
    }
  },

  // Get API key by ID
  async getApiKeyById(id: number): Promise<ApiResponse<ApiKey>> {
    try {
      const response: AxiosResponse<ApiKey> = await apiClient.get(`/api-keys/${id}`);
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to fetch API key'
      };
    }
  },

  // Delete API key
  async deleteApiKey(id: number): Promise<ApiResponse<void>> {
    try {
      await apiClient.delete(`/api-keys/${id}`);
      return {
        success: true
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to delete API key'
      };
    }
  },

  // Toggle API key status (enable/disable)
  async toggleApiKeyStatus(id: number, enabled: boolean): Promise<ApiResponse<ApiKey>> {
    try {
      const response: AxiosResponse<ApiKey> = await apiClient.post(`/api-keys/${id}/toggle`, { enabled });
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to update API key status'
      };
    }
  },

  // Regenerate API key
  async regenerateApiKey(id: number): Promise<ApiResponse<ApiKey>> {
    try {
      const response: AxiosResponse<ApiKey> = await apiClient.post(`/api-keys/${id}/regenerate`);
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.response?.data?.message || 'Failed to regenerate API key'
      };
    }
  }
};
