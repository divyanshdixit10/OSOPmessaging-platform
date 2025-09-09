import { api, ApiResponse, PaginatedResponse } from './client';

export interface Tenant {
  id: number;
  name: string;
  subdomain: string;
  displayName: string;
  description?: string;
  contactEmail: string;
  contactPhone?: string;
  companyName?: string;
  companyAddress?: string;
  status: 'ACTIVE' | 'SUSPENDED' | 'CANCELLED' | 'TRIAL';
  plan: 'FREE' | 'STARTER' | 'PROFESSIONAL' | 'ENTERPRISE';
  planStartDate?: string;
  planEndDate?: string;
  maxUsers: number;
  maxCampaignsPerMonth: number;
  maxEmailsPerMonth: number;
  maxSmsPerMonth: number;
  maxWhatsappPerMonth: number;
  storageLimitMb: number;
  currentStorageMb: number;
  stripeCustomerId?: string;
  stripeSubscriptionId?: string;
  billingEmail?: string;
  settings?: string;
  logoUrl?: string;
  primaryColor?: string;
  secondaryColor?: string;
  timezone: string;
  locale: string;
  createdAt: string;
  updatedAt: string;
  trialEndsAt?: string;
  lastBillingDate?: string;
  nextBillingDate?: string;
  currentUserCount?: number;
  currentMonthCampaigns?: number;
  currentMonthEmails?: number;
  currentMonthSms?: number;
  currentMonthWhatsapp?: number;
  storageUsagePercentage?: number;
  isTrialExpired?: boolean;
  isActive?: boolean;
  usageStats?: UsageStats;
}

export interface UsageStats {
  emailsUsed: number;
  emailsRemaining: number;
  smsUsed: number;
  smsRemaining: number;
  whatsappUsed: number;
  whatsappRemaining: number;
  campaignsUsed: number;
  campaignsRemaining: number;
  storageUsed: number;
  storageRemaining: number;
  emailUsagePercentage: number;
  smsUsagePercentage: number;
  whatsappUsagePercentage: number;
  campaignUsagePercentage: number;
  storageUsagePercentage: number;
}

export interface CreateTenantRequest {
  name: string;
  subdomain: string;
  displayName: string;
  description?: string;
  contactEmail: string;
  contactPhone?: string;
  companyName?: string;
  companyAddress?: string;
  timezone?: string;
  locale?: string;
  primaryColor?: string;
  secondaryColor?: string;
}

export interface UpdateTenantRequest {
  displayName?: string;
  description?: string;
  contactEmail?: string;
  contactPhone?: string;
  companyName?: string;
  companyAddress?: string;
  timezone?: string;
  locale?: string;
  primaryColor?: string;
  secondaryColor?: string;
  logoUrl?: string;
  settings?: string;
}

export interface TenantQuotaCheck {
  resourceType: 'EMAIL' | 'SMS' | 'WHATSAPP' | 'CAMPAIGN' | 'STORAGE';
  additionalCount?: number;
  additionalBytes?: number;
}

export interface BillingHistory {
  id: number;
  tenantId: number;
  stripeInvoiceId?: string;
  amount: number;
  currency: string;
  status: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  billingPeriodStart: string;
  billingPeriodEnd: string;
  planName: string;
  createdAt: string;
}

export const tenantService = {
  // Get current tenant
  getCurrentTenant: (): Promise<ApiResponse<Tenant>> =>
    api.get<Tenant>('/api/tenant/current'),

  // Get tenant by ID
  getTenantById: (id: number): Promise<ApiResponse<Tenant>> =>
    api.get<Tenant>(`/api/tenant/${id}`),

  // Get tenant by subdomain
  getTenantBySubdomain: (subdomain: string): Promise<ApiResponse<Tenant>> =>
    api.get<Tenant>(`/api/tenant/subdomain/${subdomain}`),

  // Get all tenants (admin only)
  getAllTenants: (page = 0, size = 20): Promise<ApiResponse<PaginatedResponse<Tenant>>> =>
    api.get<PaginatedResponse<Tenant>>(`/api/tenant?page=${page}&size=${size}`),

  // Create new tenant
  createTenant: (data: CreateTenantRequest): Promise<ApiResponse<Tenant>> =>
    api.post<Tenant>('/api/tenant', data),

  // Update tenant
  updateTenant: (id: number, data: UpdateTenantRequest): Promise<ApiResponse<Tenant>> =>
    api.put<Tenant>(`/api/tenant/${id}`, data),

  // Delete tenant
  deleteTenant: (id: number): Promise<ApiResponse<void>> =>
    api.delete<void>(`/api/tenant/${id}`),

  // Suspend tenant
  suspendTenant: (id: number, reason: string): Promise<ApiResponse<void>> =>
    api.post<void>(`/api/tenant/${id}/suspend`, { reason }),

  // Activate tenant
  activateTenant: (id: number): Promise<ApiResponse<void>> =>
    api.post<void>(`/api/tenant/${id}/activate`),

  // Check quota
  checkQuota: (data: TenantQuotaCheck): Promise<ApiResponse<{ allowed: boolean; reason?: string }>> =>
    api.post<{ allowed: boolean; reason?: string }>('/api/tenant/quota/check', data),

  // Get usage statistics
  getUsageStats: (): Promise<ApiResponse<UsageStats>> =>
    api.get<UsageStats>('/api/tenant/usage-stats'),

  // Upgrade plan
  upgradePlan: (plan: string): Promise<ApiResponse<Tenant>> =>
    api.post<Tenant>('/api/tenant/upgrade-plan', { plan }),

  // Downgrade plan
  downgradePlan: (plan: string): Promise<ApiResponse<Tenant>> =>
    api.post<Tenant>('/api/tenant/downgrade-plan', { plan }),

  // Get billing history
  getBillingHistory: (page = 0, size = 20): Promise<ApiResponse<PaginatedResponse<BillingHistory>>> =>
    api.get<PaginatedResponse<BillingHistory>>(`/api/tenant/billing-history?page=${page}&size=${size}`),

  // Create Stripe checkout session
  createCheckoutSession: (plan: string): Promise<ApiResponse<{ sessionId: string; url: string }>> =>
    api.post<{ sessionId: string; url: string }>('/api/tenant/billing/checkout', { plan }),

  // Create Stripe customer portal session
  createCustomerPortalSession: (): Promise<ApiResponse<{ url: string }>> =>
    api.post<{ url: string }>('/api/tenant/billing/portal'),

  // Upload logo
  uploadLogo: (file: File): Promise<ApiResponse<{ logoUrl: string }>> => {
    const formData = new FormData();
    formData.append('file', file);
    return api.upload<{ logoUrl: string }>('/api/tenant/upload-logo', formData);
  },

  // Get tenant settings
  getSettings: (): Promise<ApiResponse<Record<string, any>>> =>
    api.get<Record<string, any>>('/api/tenant/settings'),

  // Update tenant settings
  updateSettings: (settings: Record<string, any>): Promise<ApiResponse<void>> =>
    api.put<void>('/api/tenant/settings', settings),
};
