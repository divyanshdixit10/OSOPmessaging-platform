export interface EmailTemplate {
  id?: number;
  name: string;
  subject: string;
  body: string;
  category?: string;
  type?: string;
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
  isDefault?: boolean;
  isActive?: boolean;
  description?: string;
  variables?: string;
}

export interface EmailTemplateFormData {
  name: string;
  subject: string;
  body: string;
  category?: string;
  type?: string;
  description?: string;
  variables?: string;
  isActive?: boolean;
  isDefault?: boolean;
}

export interface TemplateVariable {
  key: string;
  value: string;
  description?: string;
} 