-- Initial schema for OSOP Messaging Platform SaaS
-- This migration creates all necessary tables for multi-tenant messaging platform

-- Create tenants table for multi-tenancy
CREATE TABLE tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    subdomain VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(50),
    company_name VARCHAR(255),
    company_address TEXT,
    status ENUM('ACTIVE', 'SUSPENDED', 'CANCELLED', 'TRIAL') NOT NULL DEFAULT 'ACTIVE',
    plan ENUM('FREE', 'STARTER', 'PROFESSIONAL', 'ENTERPRISE') NOT NULL DEFAULT 'FREE',
    plan_start_date DATETIME,
    plan_end_date DATETIME,
    max_users INT DEFAULT 5,
    max_campaigns_per_month INT DEFAULT 100,
    max_emails_per_month INT DEFAULT 1000,
    max_sms_per_month INT DEFAULT 100,
    max_whatsapp_per_month INT DEFAULT 50,
    storage_limit_mb BIGINT DEFAULT 100,
    current_storage_mb BIGINT DEFAULT 0,
    stripe_customer_id VARCHAR(255),
    stripe_subscription_id VARCHAR(255),
    billing_email VARCHAR(255),
    settings TEXT,
    logo_url VARCHAR(500),
    primary_color VARCHAR(7),
    secondary_color VARCHAR(7),
    timezone VARCHAR(50) DEFAULT 'UTC',
    locale VARCHAR(10) DEFAULT 'en_US',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    trial_ends_at DATETIME,
    last_billing_date DATETIME,
    next_billing_date DATETIME,
    INDEX idx_tenants_subdomain (subdomain),
    INDEX idx_tenants_status (status),
    INDEX idx_tenants_plan (plan)
);

-- Update users table to support multi-tenancy
ALTER TABLE users 
ADD COLUMN tenant_id BIGINT NOT NULL AFTER last_login,
ADD COLUMN is_tenant_admin BOOLEAN DEFAULT FALSE AFTER tenant_id,
ADD COLUMN permissions TEXT AFTER is_tenant_admin,
ADD CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
ADD INDEX idx_users_tenant (tenant_id);

-- Update subscribers table to support multi-tenancy
ALTER TABLE subscribers 
ADD COLUMN tenant_id BIGINT NOT NULL AFTER id,
ADD CONSTRAINT fk_subscribers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
ADD INDEX idx_subscribers_tenant (tenant_id),
ADD INDEX idx_subscribers_tenant_email (tenant_id, email);

-- Update campaigns table to support multi-tenancy
ALTER TABLE campaigns 
ADD COLUMN tenant_id BIGINT NOT NULL AFTER id,
ADD COLUMN created_by BIGINT AFTER tenant_id,
ADD CONSTRAINT fk_campaigns_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
ADD CONSTRAINT fk_campaigns_created_by FOREIGN KEY (created_by) REFERENCES users(id),
ADD INDEX idx_campaigns_tenant (tenant_id),
ADD INDEX idx_campaigns_tenant_status (tenant_id, status);

-- Update email_templates table to support multi-tenancy
ALTER TABLE email_templates 
ADD COLUMN tenant_id BIGINT NOT NULL AFTER id,
ADD COLUMN created_by BIGINT AFTER tenant_id,
ADD CONSTRAINT fk_email_templates_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
ADD CONSTRAINT fk_email_templates_created_by FOREIGN KEY (created_by) REFERENCES users(id),
ADD INDEX idx_email_templates_tenant (tenant_id),
ADD INDEX idx_email_templates_tenant_category (tenant_id, category);

-- Update email_events table to support multi-tenancy
ALTER TABLE email_events 
ADD COLUMN tenant_id BIGINT NOT NULL AFTER id,
ADD CONSTRAINT fk_email_events_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
ADD INDEX idx_email_events_tenant (tenant_id),
ADD INDEX idx_email_events_tenant_campaign (tenant_id, campaign_id),
ADD INDEX idx_email_events_tenant_type (tenant_id, event_type);

-- Update message_logs table to support multi-tenancy
ALTER TABLE message_logs 
ADD COLUMN tenant_id BIGINT NOT NULL AFTER id,
ADD CONSTRAINT fk_message_logs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
ADD INDEX idx_message_logs_tenant (tenant_id),
ADD INDEX idx_message_logs_tenant_channel (tenant_id, channel);

-- Update activity_logs table to support multi-tenancy
ALTER TABLE activity_logs 
ADD COLUMN tenant_id BIGINT NOT NULL AFTER id,
ADD CONSTRAINT fk_activity_logs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
ADD INDEX idx_activity_logs_tenant (tenant_id),
ADD INDEX idx_activity_logs_tenant_type (tenant_id, activity_type);

-- Create usage_tracking table for quota management
CREATE TABLE usage_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    resource_type ENUM('EMAIL', 'SMS', 'WHATSAPP', 'CAMPAIGN', 'STORAGE') NOT NULL,
    usage_count INT NOT NULL DEFAULT 0,
    usage_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_usage_tracking_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_usage_tracking_user FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_usage_tracking_tenant_resource_date (tenant_id, resource_type, usage_date),
    INDEX idx_usage_tracking_tenant (tenant_id),
    INDEX idx_usage_tracking_date (usage_date)
);

-- Create billing_history table for payment tracking
CREATE TABLE billing_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    stripe_invoice_id VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL,
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    plan_name VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_billing_history_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    INDEX idx_billing_history_tenant (tenant_id),
    INDEX idx_billing_history_status (status),
    INDEX idx_billing_history_period (billing_period_start, billing_period_end)
);

-- Create file_uploads table for attachment management
CREATE TABLE file_uploads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_hash VARCHAR(64),
    upload_type ENUM('ATTACHMENT', 'TEMPLATE_IMAGE', 'LOGO', 'CSV_IMPORT') NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_file_uploads_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_file_uploads_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_file_uploads_tenant (tenant_id),
    INDEX idx_file_uploads_user (user_id),
    INDEX idx_file_uploads_type (upload_type)
);

-- Create notification_settings table for user preferences
CREATE TABLE notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    campaign_completed BOOLEAN DEFAULT TRUE,
    campaign_failed BOOLEAN DEFAULT TRUE,
    quota_warning BOOLEAN DEFAULT TRUE,
    quota_exceeded BOOLEAN DEFAULT TRUE,
    billing_reminder BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_settings_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_notification_settings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_notification_settings_user (user_id)
);

-- Create api_keys table for external integrations
CREATE TABLE api_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    key_name VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    permissions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    last_used_at DATETIME,
    expires_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_api_keys_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_api_keys_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_api_keys_tenant (tenant_id),
    INDEX idx_api_keys_key (api_key),
    INDEX idx_api_keys_active (is_active)
);

-- Create webhook_endpoints table for external integrations
CREATE TABLE webhook_endpoints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    events TEXT NOT NULL, -- JSON array of event types
    secret_key VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    last_triggered_at DATETIME,
    failure_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_webhook_endpoints_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_webhook_endpoints_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_webhook_endpoints_tenant (tenant_id),
    INDEX idx_webhook_endpoints_active (is_active)
);

-- Create default tenant for existing data
INSERT INTO tenants (
    name, subdomain, display_name, contact_email, 
    status, plan, max_users, max_campaigns_per_month, 
    max_emails_per_month, max_sms_per_month, max_whatsapp_per_month
) VALUES (
    'default', 'default', 'Default Tenant', 'admin@osop.com',
    'ACTIVE', 'FREE', 5, 100, 1000, 100, 50
);

-- Update existing users to belong to default tenant
UPDATE users SET tenant_id = 1 WHERE tenant_id IS NULL;

-- Update existing data to belong to default tenant
UPDATE subscribers SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE campaigns SET tenant_id = 1, created_by = 1 WHERE tenant_id IS NULL;
UPDATE email_templates SET tenant_id = 1, created_by = 1 WHERE tenant_id IS NULL;
UPDATE email_events SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE message_logs SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE activity_logs SET tenant_id = 1 WHERE tenant_id IS NULL;
