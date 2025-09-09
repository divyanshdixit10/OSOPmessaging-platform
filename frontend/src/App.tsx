import React from 'react';
import { ChakraProvider, CSSReset } from '@chakra-ui/react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { WebSocketProvider } from './contexts/WebSocketContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AppLayout } from './components/layout/AppLayout';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { SendEmailPage } from './pages/SendEmailPage';
import { TemplatesPage } from './pages/TemplatesPage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { EnhancedCampaignPage } from './pages/EnhancedCampaignPage';
import { EnhancedTemplatesPage } from './pages/EnhancedTemplatesPage';
import { SettingsPage } from './pages/SettingsPage';
import { theme } from './theme';

function App() {
  return (
    <ChakraProvider theme={theme}>
      <CSSReset />
      <AuthProvider>
        <WebSocketProvider>
          <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
            <Routes>
            {/* Public routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            
            {/* Protected routes */}
            <Route path="/" element={
              <ProtectedRoute>
                <AppLayout>
                  <Navigate to="/dashboard" replace />
                </AppLayout>
              </ProtectedRoute>
            } />
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <AppLayout>
                  <DashboardPage />
                </AppLayout>
              </ProtectedRoute>
            } />
            <Route path="/send-email" element={
              <ProtectedRoute>
                <AppLayout>
                  <SendEmailPage />
                </AppLayout>
              </ProtectedRoute>
            } />
            <Route path="/templates" element={
              <ProtectedRoute>
                <AppLayout>
                  <TemplatesPage />
                </AppLayout>
              </ProtectedRoute>
            } />
            <Route path="/templates-enhanced" element={
              <ProtectedRoute>
                <AppLayout>
                  <EnhancedTemplatesPage />
                </AppLayout>
              </ProtectedRoute>
            } />
            <Route path="/analytics" element={
              <ProtectedRoute>
                <AppLayout>
                  <AnalyticsPage />
                </AppLayout>
              </ProtectedRoute>
            } />
            <Route path="/campaigns" element={
              <ProtectedRoute>
                <AppLayout>
                  <EnhancedCampaignPage />
                </AppLayout>
              </ProtectedRoute>
            } />
            <Route path="/settings" element={
              <ProtectedRoute>
                <AppLayout>
                  <SettingsPage />
                </AppLayout>
              </ProtectedRoute>
            } />
            
            {/* Catch all route */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </Router>
        </WebSocketProvider>
      </AuthProvider>
    </ChakraProvider>
  );
}

export default App;
