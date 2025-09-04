import { EmailTemplate, EmailTemplateFormData } from '../types/EmailTemplate';

const API_BASE_URL = 'http://localhost:8080/api';

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

    const response = await fetch(`${API_BASE_URL}/templates?${params.toString()}`);
    return this.handleResponse<PaginatedResponse<EmailTemplate>>(response);
  }

  static async getTemplateById(id: string): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`);
    return this.handleResponse<EmailTemplate>(response);
  }

  static async createTemplate(template: EmailTemplateFormData): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(template),
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async updateTemplate(id: string, template: EmailTemplateFormData): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(template),
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async deleteTemplate(id: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
  }

  static async duplicateTemplate(id: string): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}/duplicate`, {
      method: 'POST',
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async updateTemplateStatus(id: string, isActive: boolean): Promise<EmailTemplate> {
    const response = await fetch(`${API_BASE_URL}/templates/${id}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ isActive }),
    });
    return this.handleResponse<EmailTemplate>(response);
  }

  static async getCategories(): Promise<string[]> {
    const response = await fetch(`${API_BASE_URL}/templates/categories`);
    return this.handleResponse<string[]>(response);
  }

  static async getTypes(): Promise<string[]> {
    const response = await fetch(`${API_BASE_URL}/templates/types`);
    return this.handleResponse<string[]>(response);
  }

  static async getTemplateStats(): Promise<{ activeTemplates: number }> {
    const response = await fetch(`${API_BASE_URL}/templates/stats/count`);
    return this.handleResponse<{ activeTemplates: number }>(response);
  }

  // Fallback mock data for development when API is not available
  private static getMockTemplates(): EmailTemplate[] {
    return [
      {
        id: '1',
        name: 'Welcome Email',
        subject: 'Welcome to Our Platform!',
        body: '<h1>Welcome {{firstName}}!</h1><p>We\'re excited to have you on board.</p>',
        category: 'Onboarding',
        type: 'Transactional',
        createdBy: 'System',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
        isDefault: true,
        isActive: true,
        description: 'Welcome email for new users',
        variables: '{"firstName": "User\'s first name"}'
      },
      {
        id: '2',
        name: 'Newsletter Template',
        subject: 'Weekly Newsletter - {{weekNumber}}',
        body: '<h1>Weekly Newsletter</h1><p>Here\'s what\'s new this week...</p>',
        category: 'Marketing',
        type: 'Newsletter',
        createdBy: 'Marketing Team',
        createdAt: '2024-01-02T00:00:00Z',
        updatedAt: '2024-01-02T00:00:00Z',
        isDefault: false,
        isActive: true,
        description: 'Weekly newsletter template',
        variables: '{"weekNumber": "Current week number"}'
      },
      {
        id: '3',
        name: 'Password Reset',
        subject: 'Reset Your Password',
        body: '<h1>Password Reset Request</h1><p>Click the link below to reset your password: {{resetLink}}</p>',
        category: 'Security',
        type: 'Transactional',
        createdBy: 'System',
        createdAt: '2024-01-03T00:00:00Z',
        updatedAt: '2024-01-03T00:00:00Z',
        isDefault: false,
        isActive: true,
        description: 'Password reset email template',
        variables: '{"resetLink": "Password reset link"}'
      }
    ];
  }

  // Enhanced error handling with fallback to mock data
  static async getTemplatesWithFallback(filters: TemplateFilters = {}, page: number = 0, size: number = 20): Promise<PaginatedResponse<EmailTemplate>> {
    try {
      return await this.getTemplates(filters, page, size);
    } catch (error) {
      console.warn('API call failed, using mock data:', error);
      const mockTemplates = this.getMockTemplates();
      const filteredTemplates = mockTemplates.filter(template => {
        if (filters.name && !template.name.toLowerCase().includes(filters.name.toLowerCase())) return false;
        if (filters.category && template.category !== filters.category) return false;
        if (filters.type && template.type !== filters.type) return false;
        if (filters.isActive !== undefined && template.isActive !== filters.isActive) return false;
        return true;
      });
      
      const startIndex = page * size;
      const endIndex = startIndex + size;
      const content = filteredTemplates.slice(startIndex, endIndex);
      
      return {
        content,
        totalElements: filteredTemplates.length,
        totalPages: Math.ceil(filteredTemplates.length / size),
        size,
        number: page
      };
    }
  }
}

export default TemplateService; 