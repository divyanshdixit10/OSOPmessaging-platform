import { EmailTemplate, EmailTemplateFormData } from '../types/EmailTemplate';

const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

export interface TemplateFilters {
  name?: string;
  category?: string;
  type?: string;
  isActive?: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export class TemplateService {
  private static async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
    return response.json();
  }

  static async getTemplates(filters: TemplateFilters = {}, page: number = 0, size: number = 20): Promise<PaginatedResponse<EmailTemplate>> {
    const params = new URLSearchParams();
    
    if (filters.name) params.append('name', filters.name);
    if (filters.category) params.append('category', filters.category);
    if (filters.type) params.append('type', filters.type);
    if (filters.isActive !== undefined) params.append('isActive', filters.isActive.toString());
    
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await fetch(`${API_BASE_URL}/templates?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<PaginatedResponse<EmailTemplate>>(response);
  }

  static async getTemplateById(id: number): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async createTemplate(template: EmailTemplateFormData): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(template),
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async updateTemplate(id: number, template: EmailTemplateFormData): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(template),
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async deleteTemplate(id: number): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
  }

  static async duplicateTemplate(id: number): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}/duplicate`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async updateTemplateStatus(id: number, isActive: boolean): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}/status`, {
      method: 'PATCH',
      headers: getAuthHeaders(),
      body: JSON.stringify({ isActive }),
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async getCategories(): Promise<string[]> {
    const response = await fetch(`${API_BASE_URL}/templates/categories`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<string[]>(response);
  }

  static async getTypes(): Promise<string[]> {
    const response = await fetch(`${API_BASE_URL}/templates/types`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<string[]>(response);
  }

  static async getTemplateStats(): Promise<{ activeTemplates: number }> {
    const response = await fetch(`${API_BASE_URL}/templates/stats/count`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<{ activeTemplates: number }>(response);
  }


  // Real backend data only - no fallbacks
  static async getTemplatesReal(filters: TemplateFilters = {}, page: number = 0, size: number = 20): Promise<PaginatedResponse<EmailTemplate>> {
    return await this.getTemplates(filters, page, size);
  }
}

export default TemplateService; 