import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  tenantId: number;
  isTenantAdmin: boolean;
  permissions?: string;
}

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
  primaryColor?: string;
  secondaryColor?: string;
  logoUrl?: string;
  timezone: string;
  locale: string;
  trialEndsAt?: string;
  createdAt: string;
  updatedAt: string;
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

interface AppState {
  // User state
  user: User | null;
  isAuthenticated: boolean;
  token: string | null;
  
  // Tenant state
  currentTenant: Tenant | null;
  usageStats: UsageStats | null;
  
  // UI state
  sidebarCollapsed: boolean;
  theme: 'light' | 'dark';
  loading: boolean;
  notifications: Notification[];
  
  // Actions
  setUser: (user: User | null) => void;
  setToken: (token: string | null) => void;
  setCurrentTenant: (tenant: Tenant | null) => void;
  setUsageStats: (stats: UsageStats | null) => void;
  toggleSidebar: () => void;
  setTheme: (theme: 'light' | 'dark') => void;
  setLoading: (loading: boolean) => void;
  addNotification: (notification: Omit<Notification, 'id' | 'timestamp' | 'read'>) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
  
  // Auth actions
  login: (user: User, token: string) => void;
  logout: () => void;
  
  // Tenant actions
  updateTenantSettings: (settings: Partial<Tenant>) => void;
  updateUsageStats: (stats: Partial<UsageStats>) => void;
}

interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
}

export const useAppStore = create<AppState>()(
  persist(
    (set, get) => ({
      // Initial state
      user: null,
      isAuthenticated: false,
      token: null,
      currentTenant: null,
      usageStats: null,
      sidebarCollapsed: false,
      theme: 'light',
      loading: false,
      notifications: [],

      // Actions
      setUser: (user: User | null) => set({ user, isAuthenticated: !!user }),
      setToken: (token: string | null) => set({ token }),
      setCurrentTenant: (currentTenant: Tenant | null) => set({ currentTenant }),
      setUsageStats: (usageStats: UsageStats | null) => set({ usageStats }),
      toggleSidebar: () => set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed })),
      setTheme: (theme: 'light' | 'dark') => set({ theme }),
      setLoading: (loading: boolean) => set({ loading }),
      
      addNotification: (notification: Omit<Notification, 'id' | 'timestamp' | 'read'>) => set((state) => ({
        notifications: [
          ...state.notifications,
          {
            ...notification,
            id: Date.now().toString(),
            timestamp: new Date(),
            read: false,
          }
        ]
      })),
      
      removeNotification: (id: string) => set((state) => ({
        notifications: state.notifications.filter((n) => n.id !== id)
      })),
      
      clearNotifications: () => set({ notifications: [] }),
      
      // Auth actions
      login: (user: User, token: string) => set({
        user,
        token,
        isAuthenticated: true,
      }),
      
      logout: () => set({
        user: null,
        token: null,
        isAuthenticated: false,
        currentTenant: null,
        usageStats: null,
        notifications: [],
      }),
      
      // Tenant actions
      updateTenantSettings: (settings: Partial<Tenant>) => set((state) => ({
        currentTenant: state.currentTenant ? { ...state.currentTenant, ...settings } : null
      })),
      
      updateUsageStats: (stats: Partial<UsageStats>) => set((state) => ({
        usageStats: state.usageStats ? { ...state.usageStats, ...stats } : null
      })),
    }),
    {
      name: 'osop-messaging-store',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
        currentTenant: state.currentTenant,
        theme: state.theme,
        sidebarCollapsed: state.sidebarCollapsed,
      }),
    }
  )
);
