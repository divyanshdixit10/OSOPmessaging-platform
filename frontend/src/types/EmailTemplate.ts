export interface EmailTemplate {
  id?: string;
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
}

export interface TemplateVariable {
  key: string;
  value: string;
  description?: string;
} 