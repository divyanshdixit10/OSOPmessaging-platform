const API_BASE_URL = 'http://localhost:8080/api/templates/enhanced';

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    'X-User-Id': 'current-user', // Replace with actual user ID
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

export interface Template {
  id: number;
  name: string;
  subject: string;
  contentHtml: string;
  contentText?: string;
  category: string;
  type: string;
  createdBy: string;
  isDefault: boolean;
  isActive: boolean;
  isPublic: boolean;
  createdAt: string;
  updatedAt: string;
  description?: string;
  thumbnailUrl?: string;
  variables?: string;
  version: number;
  parentTemplateId?: number;
  usageCount: number;
  lastUsedAt?: string;
  tags?: string[];
  cssStyles?: string;
  metadata?: string;
}

export interface CreateTemplateRequest {
  name: string;
  subject: string;
  contentHtml: string;
  contentText?: string;
  category: string;
  type: string;
  description?: string;
  cssStyles?: string;
  variables?: string;
  tags?: string[];
  isPublic?: boolean;
  metadata?: string;
}

export interface UpdateTemplateRequest {
  name: string;
  subject: string;
  contentHtml: string;
  contentText?: string;
  category?: string;
  type?: string;
  description?: string;
  cssStyles?: string;
  variables?: string;
  tags?: string[];
  isPublic?: boolean;
  isActive?: boolean;
  metadata?: string;
  changeDescription?: string;
}

export interface TemplateVersion {
  id: number;
  templateId: number;
  versionNumber: number;
  contentHtml: string;
  contentText?: string;
  subject: string;
  cssStyles?: string;
  variables?: string;
  changeDescription?: string;
  createdBy: string;
  createdAt: string;
  isCurrentVersion: boolean;
  fileSize: number;
  metadata?: string;
}

export interface TemplateImportRequest {
  name: string;
  subject: string;
  contentHtml: string;
  contentText?: string;
  category?: string;
  type?: string;
  description?: string;
  cssStyles?: string;
  variables?: string;
  tags?: string[];
  isPublic?: boolean;
  metadata?: string;
  overwriteExisting?: boolean;
  importSource?: string;
}

export interface TemplateStatistics {
  totalTemplates: number;
  activeTemplates: number;
  defaultTemplates: number;
  categories: string[];
  types: string[];
}

export interface TemplateFilters {
  search?: string;
  category?: string;
  type?: string;
  isActive?: boolean;
  createdBy?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
}

export class TemplateService {
  private static async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
    return response.json();
  }

  // Create a new template
  static async createTemplate(request: CreateTemplateRequest): Promise<Template> {
    const response = await fetch(`${API_BASE_URL}`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(request)
    });
    return this.handleResponse<Template>(response);
  }

  // Get all templates with filtering and pagination
  static async getTemplates(filters: TemplateFilters = {}): Promise<{
    content: Template[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
  }> {
    const params = new URLSearchParams();
    
    if (filters.search) params.append('search', filters.search);
    if (filters.category) params.append('category', filters.category);
    if (filters.type) params.append('type', filters.type);
    if (filters.isActive !== undefined) params.append('isActive', filters.isActive.toString());
    if (filters.createdBy) params.append('createdBy', filters.createdBy);
    if (filters.page !== undefined) params.append('page', filters.page.toString());
    if (filters.size !== undefined) params.append('size', filters.size.toString());
    if (filters.sortBy) params.append('sortBy', filters.sortBy);
    if (filters.sortDir) params.append('sortDir', filters.sortDir);

    const response = await fetch(`${API_BASE_URL}?${params.toString()}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<{
      content: Template[];
      totalElements: number;
      totalPages: number;
      size: number;
      number: number;
    }>(response);
  }

  // Get template by ID
  static async getTemplateById(id: number): Promise<Template> {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Template>(response);
  }

  // Update template
  static async updateTemplate(id: number, request: UpdateTemplateRequest): Promise<Template> {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(request)
    });
    return this.handleResponse<Template>(response);
  }

  // Delete template
  static async deleteTemplate(id: number): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
  }

  // Clone template
  static async cloneTemplate(id: number, newName: string): Promise<Template> {
    const response = await fetch(`${API_BASE_URL}/${id}/clone`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ name: newName })
    });
    return this.handleResponse<Template>(response);
  }

  // Get template versions
  static async getTemplateVersions(id: number): Promise<TemplateVersion[]> {
    const response = await fetch(`${API_BASE_URL}/${id}/versions`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<TemplateVersion[]>(response);
  }

  // Revert template to version
  static async revertToVersion(id: number, versionNumber: number): Promise<Template> {
    const response = await fetch(`${API_BASE_URL}/${id}/revert/${versionNumber}`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    return this.handleResponse<Template>(response);
  }

  // Import template
  static async importTemplate(request: TemplateImportRequest): Promise<Template> {
    const response = await fetch(`${API_BASE_URL}/import`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(request)
    });
    return this.handleResponse<Template>(response);
  }

  // Export template
  static async exportTemplate(id: number): Promise<string> {
    const response = await fetch(`${API_BASE_URL}/${id}/export`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<string>(response);
  }

  // Get template statistics
  static async getTemplateStatistics(): Promise<TemplateStatistics> {
    const response = await fetch(`${API_BASE_URL}/statistics`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<TemplateStatistics>(response);
  }

  // Get template categories
  static async getTemplateCategories(): Promise<string[]> {
    const response = await fetch(`${API_BASE_URL}/categories`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<string[]>(response);
  }

  // Get template types
  static async getTemplateTypes(): Promise<string[]> {
    const response = await fetch(`${API_BASE_URL}/types`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<string[]>(response);
  }

  // Increment template usage
  static async incrementTemplateUsage(id: number): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/${id}/use`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }
  }

  // Get popular templates
  static async getPopularTemplates(limit: number = 10): Promise<Template[]> {
    const response = await fetch(`${API_BASE_URL}/popular?limit=${limit}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Template[]>(response);
  }

  // Get recent templates
  static async getRecentTemplates(limit: number = 10): Promise<Template[]> {
    const response = await fetch(`${API_BASE_URL}/recent?limit=${limit}`, {
      headers: getAuthHeaders()
    });
    return this.handleResponse<Template[]>(response);
  }
}

export default TemplateService;