-- V2__Additional_tables.sql
-- Additional tables for OSOP Messaging Platform

-- Create campaign_progress table to track campaign execution progress
CREATE TABLE campaign_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,  -- SCHEDULED, RUNNING, PAUSED, COMPLETED, CANCELLED, FAILED
    total_recipients INT NOT NULL DEFAULT 0,
    emails_sent INT NOT NULL DEFAULT 0,
    emails_success INT NOT NULL DEFAULT 0,
    emails_failed INT NOT NULL DEFAULT 0,
    emails_in_progress INT NOT NULL DEFAULT 0,
    progress_percentage DOUBLE NOT NULL DEFAULT 0.0,
    current_batch_number INT NOT NULL DEFAULT 0,
    total_batches INT NOT NULL DEFAULT 0,
    batch_size INT NOT NULL DEFAULT 50,
    started_at TIMESTAMP NULL,
    paused_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    scheduled_time TIMESTAMP NULL,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE
);

-- Create activity_logs table to track user and system activities
CREATE TABLE activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    performed_by VARCHAR(255) NOT NULL, -- User email or 'system'
    entity_type VARCHAR(50),            -- 'campaign', 'user', 'template', etc.
    entity_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tenant_id BIGINT,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- Create usage_tracking table for tenant resource usage tracking
CREATE TABLE usage_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    resource_type VARCHAR(50) NOT NULL, -- EMAIL, SMS, WHATSAPP, STORAGE, CAMPAIGN
    usage_count BIGINT NOT NULL DEFAULT 0,
    usage_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create billing_history table for tenant billing records
CREATE TABLE billing_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(50) NOT NULL, -- PENDING, PAID, FAILED, REFUNDED
    billing_date DATE NOT NULL,
    payment_method VARCHAR(50),
    payment_reference VARCHAR(255),
    invoice_number VARCHAR(50),
    invoice_url VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- Create file_uploads table for storing uploaded files
CREATE TABLE file_uploads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    purpose VARCHAR(50), -- SUBSCRIBER_IMPORT, TEMPLATE_IMPORT, ATTACHMENT, etc.
    reference_id BIGINT, -- ID of the related entity (campaign, template, etc.)
    reference_type VARCHAR(50), -- Type of the related entity
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create notification_settings table for user notification preferences
CREATE TABLE notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- EMAIL, PUSH, IN_APP
    event_type VARCHAR(50) NOT NULL, -- CAMPAIGN_COMPLETED, QUOTA_EXCEEDED, etc.
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    UNIQUE KEY (user_id, notification_type, event_type)
);

-- Create api_keys table for API authentication
CREATE TABLE api_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at TIMESTAMP NULL,
    last_used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY (api_key)
);

-- Create webhook_endpoints table for webhook integrations
CREATE TABLE webhook_endpoints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    events TEXT NOT NULL, -- Comma-separated list of events to trigger this webhook
    secret_key VARCHAR(255), -- For webhook signature verification
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- Add indexes for performance
CREATE INDEX idx_campaign_progress_campaign_id ON campaign_progress(campaign_id);
CREATE INDEX idx_campaign_progress_status ON campaign_progress(status);
CREATE INDEX idx_activity_logs_tenant_id ON activity_logs(tenant_id);
CREATE INDEX idx_activity_logs_entity ON activity_logs(entity_type, entity_id);
CREATE INDEX idx_usage_tracking_tenant_date ON usage_tracking(tenant_id, usage_date);
CREATE INDEX idx_billing_history_tenant_date ON billing_history(tenant_id, billing_date);
CREATE INDEX idx_file_uploads_tenant_id ON file_uploads(tenant_id);
CREATE INDEX idx_file_uploads_reference ON file_uploads(reference_type, reference_id);
CREATE INDEX idx_notification_settings_user_id ON notification_settings(user_id);
CREATE INDEX idx_api_keys_tenant_id ON api_keys(tenant_id);
CREATE INDEX idx_webhook_endpoints_tenant_id ON webhook_endpoints(tenant_id);

-- Add missing columns to existing tables

-- Add missing columns to tenants table
ALTER TABLE tenants
ADD COLUMN current_storage_mb BIGINT DEFAULT 0 AFTER storage_limit_mb,
ADD COLUMN stripe_customer_id VARCHAR(255) NULL AFTER current_storage_mb,
ADD COLUMN stripe_subscription_id VARCHAR(255) NULL AFTER stripe_customer_id,
ADD COLUMN billing_email VARCHAR(255) NULL AFTER stripe_subscription_id,
ADD COLUMN next_billing_date TIMESTAMP NULL AFTER billing_email,
ADD COLUMN settings JSON NULL AFTER next_billing_date;

-- Add missing columns to campaigns table
ALTER TABLE campaigns 
ADD COLUMN scheduled_at TIMESTAMP NULL AFTER is_draft,
ADD COLUMN started_at TIMESTAMP NULL AFTER scheduled_at,
ADD COLUMN completed_at TIMESTAMP NULL AFTER started_at,
ADD COLUMN template_id BIGINT NULL AFTER completed_at,
ADD COLUMN segment_id BIGINT NULL AFTER template_id,
ADD COLUMN utm_source VARCHAR(255) NULL AFTER segment_id,
ADD COLUMN utm_medium VARCHAR(255) NULL AFTER utm_source,
ADD COLUMN utm_campaign VARCHAR(255) NULL AFTER utm_medium,
ADD COLUMN utm_term VARCHAR(255) NULL AFTER utm_campaign,
ADD COLUMN utm_content VARCHAR(255) NULL AFTER utm_term;

-- Add missing columns to users table
ALTER TABLE users
ADD COLUMN phone_number VARCHAR(50) NULL AFTER last_name,
ADD COLUMN profile_picture_url VARCHAR(255) NULL AFTER phone_number,
ADD COLUMN timezone VARCHAR(50) DEFAULT 'UTC' AFTER profile_picture_url,
ADD COLUMN locale VARCHAR(10) DEFAULT 'en_US' AFTER timezone,
ADD COLUMN last_password_change TIMESTAMP NULL AFTER locale,
ADD COLUMN permissions JSON NULL AFTER is_tenant_admin;