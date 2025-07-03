export interface EmailTemplate {
  id?: string;
  name: string;
  subject: string;
  body: string;
  createdAt?: string;
  updatedAt?: string;
  isDefault?: boolean;
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