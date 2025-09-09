export interface EmailTemplate {
  id?: number;
  name: string;
  subject: string;
  body: string;
  contentHtml?: string;
  contentText?: string;
  category?: string;
  type?: string;
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
  isDefault?: boolean;
  isActive?: boolean;
  isPublic?: boolean;
  description?: string;
  variables?: string;
  version?: number;
  parentTemplateId?: number;
  usageCount?: number;
  lastUsedAt?: string;
  tags?: string[];
  cssStyles?: string;
  metadata?: string;
  thumbnailUrl?: string;
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