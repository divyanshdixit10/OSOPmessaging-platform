-- V3__Sample_data.sql
-- Sample data for OSOP Messaging Platform

-- Insert sample tenants
INSERT INTO tenants (
    name, subdomain, display_name, description, contact_email, 
    contact_phone, company_name, company_address, status, plan, 
    plan_start_date, max_users, max_campaigns_per_month, 
    max_emails_per_month, max_sms_per_month, max_whatsapp_per_month,
    storage_limit_mb, timezone, locale
) VALUES 
(
    'acme', 'acme', 'Acme Corporation', 'A global leader in innovative solutions', 
    'contact@acme.com', '+1-555-123-4567', 'Acme Corporation', 
    '123 Main Street, Anytown, USA', 'ACTIVE', 'PROFESSIONAL', 
    NOW(), 25, 1000, 50000, 5000, 2500, 1024, 'America/New_York', 'en_US'
),
(
    'globex', 'globex', 'Globex Corporation', 'International technology solutions provider', 
    'info@globex.com', '+1-555-987-6543', 'Globex Corporation', 
    '456 Tech Boulevard, Innovation City, USA', 'ACTIVE', 'ENTERPRISE', 
    NOW(), 100, 5000, 1000000, 50000, 25000, 5120, 'Europe/London', 'en_GB'
),
(
    'startup', 'startup', 'StartUp Inc', 'Innovative startup with big ideas', 
    'hello@startup.com', '+1-555-789-0123', 'StartUp Inc', 
    '789 Innovation Drive, Tech Valley, USA', 'TRIAL', 'FREE', 
    NOW(), 5, 100, 1000, 100, 50, 100, 'America/Los_Angeles', 'en_US'
);

-- Insert sample users for each tenant
INSERT INTO users (
    email, password, first_name, last_name, role, 
    enabled, email_verified, tenant_id, is_tenant_admin,
    created_at, last_login
) VALUES 
-- Acme users
(
    'admin@acme.com', 
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- password: admin123
    'John', 'Smith', 'ADMIN', 
    true, true, 2, true,
    NOW(), NOW()
),
(
    'user@acme.com', 
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- password: admin123
    'Jane', 'Doe', 'USER', 
    true, true, 2, false,
    NOW(), NOW()
),
-- Globex users
(
    'admin@globex.com', 
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- password: admin123
    'Robert', 'Johnson', 'ADMIN', 
    true, true, 3, true,
    NOW(), NOW()
),
(
    'user@globex.com', 
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- password: admin123
    'Sarah', 'Williams', 'USER', 
    true, true, 3, false,
    NOW(), NOW()
),
-- StartUp users
(
    'admin@startup.com', 
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- password: admin123
    'Michael', 'Brown', 'ADMIN', 
    true, true, 4, true,
    NOW(), NOW()
);

-- Insert sample email templates for each tenant
INSERT INTO email_templates (
    tenant_id, created_by, name, subject, content_html, 
    category, type, description, is_active, is_default, 
    version, variables, tags, css_styles
) VALUES
-- Default tenant templates
(
    1, 1, 'Welcome Email', 'Welcome to Our Platform!', 
    '<div><h1>Welcome to Our Platform!</h1><p>Dear {{firstName}},</p><p>Thank you for joining our platform. We are excited to have you on board!</p><p>Best regards,<br>The Team</p></div>', 
    'ONBOARDING', 'HTML', 'Default welcome email template', 
    true, true, 1, '["firstName", "lastName", "email"]', 
    'welcome,onboarding', 'body { font-family: Arial, sans-serif; }'
),
(
    1, 1, 'Password Reset', 'Reset Your Password', 
    '<div><h1>Password Reset Request</h1><p>Dear {{firstName}},</p><p>Click the link below to reset your password:</p><p><a href="{{resetLink}}">Reset Password</a></p><p>If you did not request a password reset, please ignore this email.</p><p>Best regards,<br>The Team</p></div>', 
    'ACCOUNT', 'HTML', 'Password reset email template', 
    true, true, 1, '["firstName", "lastName", "email", "resetLink"]', 
    'account,password,reset', 'body { font-family: Arial, sans-serif; }'
),
-- Acme tenant templates
(
    2, 3, 'Acme Welcome', 'Welcome to Acme Corporation!', 
    '<div style="font-family: Arial, sans-serif; color: #333;"><h1 style="color: #0066cc;">Welcome to Acme Corporation!</h1><p>Dear {{firstName}},</p><p>Thank you for joining Acme Corporation. We are excited to have you on board!</p><p>Best regards,<br>The Acme Team</p></div>', 
    'ONBOARDING', 'HTML', 'Acme welcome email template', 
    true, true, 1, '["firstName", "lastName", "email"]', 
    'welcome,onboarding,acme', 'body { font-family: Arial, sans-serif; color: #333; } h1 { color: #0066cc; }'
),
-- Globex tenant templates
(
    3, 5, 'Globex Newsletter', 'Globex Monthly Newsletter', 
    '<div style="font-family: Helvetica, sans-serif; color: #222;"><h1 style="color: #009900;">Globex Monthly Newsletter</h1><p>Dear {{firstName}},</p><p>Here are the latest updates from Globex Corporation:</p><ul><li>{{newsItem1}}</li><li>{{newsItem2}}</li><li>{{newsItem3}}</li></ul><p>Best regards,<br>The Globex Team</p></div>', 
    'NEWSLETTER', 'HTML', 'Globex monthly newsletter template', 
    true, true, 1, '["firstName", "lastName", "email", "newsItem1", "newsItem2", "newsItem3"]', 
    'newsletter,monthly,globex', 'body { font-family: Helvetica, sans-serif; color: #222; } h1 { color: #009900; }'
);

-- Insert sample subscribers for each tenant
INSERT INTO subscribers (
    tenant_id, email, first_name, last_name, 
    status, source, tags, custom_fields, 
    subscribed_at, created_at
) VALUES
-- Default tenant subscribers
(
    1, 'subscriber1@example.com', 'Alex', 'Johnson', 
    'ACTIVE', 'WEBSITE', 'newsletter,updates', '{"company": "ABC Corp", "industry": "Technology"}', 
    NOW(), NOW()
),
(
    1, 'subscriber2@example.com', 'Taylor', 'Smith', 
    'ACTIVE', 'IMPORT', 'newsletter', '{"company": "XYZ Inc", "industry": "Finance"}', 
    NOW(), NOW()
),
-- Acme tenant subscribers
(
    2, 'customer1@example.com', 'Chris', 'Brown', 
    'ACTIVE', 'WEBSITE', 'newsletter,product', '{"company": "Brown LLC", "industry": "Retail"}', 
    NOW(), NOW()
),
(
    2, 'customer2@example.com', 'Jordan', 'Lee', 
    'ACTIVE', 'REFERRAL', 'product', '{"company": "Lee Enterprises", "industry": "Manufacturing"}', 
    NOW(), NOW()
),
-- Globex tenant subscribers
(
    3, 'client1@example.com', 'Morgan', 'Davis', 
    'ACTIVE', 'WEBSITE', 'newsletter,enterprise', '{"company": "Davis Group", "industry": "Healthcare"}', 
    NOW(), NOW()
),
(
    3, 'client2@example.com', 'Casey', 'Wilson', 
    'ACTIVE', 'IMPORT', 'enterprise', '{"company": "Wilson Co", "industry": "Education"}', 
    NOW(), NOW()
);

-- Insert sample campaigns for each tenant
INSERT INTO campaigns (
    tenant_id, created_by, name, description, subject, 
    body, status, channel, total_recipients, 
    track_opens, track_clicks, add_unsubscribe_link, 
    is_draft, created_at
) VALUES
-- Default tenant campaigns
(
    1, 1, 'Welcome Campaign', 'Initial welcome email for new users', 
    'Welcome to Our Platform!', 
    '<div><h1>Welcome to Our Platform!</h1><p>Dear {{firstName}},</p><p>Thank you for joining our platform. We are excited to have you on board!</p><p>Best regards,<br>The Team</p></div>', 
    'COMPLETED', 'EMAIL', 100, 
    true, true, true, 
    false, NOW()
),
-- Acme tenant campaigns
(
    2, 3, 'Product Launch', 'New product announcement', 
    'Introducing Our New Product', 
    '<div><h1>Introducing Our New Product!</h1><p>Dear {{firstName}},</p><p>We are excited to announce our latest product. Check it out today!</p><p>Best regards,<br>The Acme Team</p></div>', 
    'DRAFT', 'EMAIL', 0, 
    true, true, true, 
    true, NOW()
),
-- Globex tenant campaigns
(
    3, 5, 'Monthly Newsletter', 'June 2023 newsletter', 
    'Globex Monthly Newsletter - June 2023', 
    '<div><h1>Globex Monthly Newsletter</h1><p>Dear {{firstName}},</p><p>Here are the latest updates from Globex Corporation for June 2023.</p><p>Best regards,<br>The Globex Team</p></div>', 
    'SCHEDULED', 'EMAIL', 0, 
    true, true, true, 
    false, DATE_ADD(NOW(), INTERVAL 1 DAY)
);

-- Insert sample email events for tracking
INSERT INTO email_events (
    tenant_id, campaign_id, email, event_type, 
    event_data, ip_address, user_agent, 
    created_at, processed
) VALUES
-- Default tenant events
(
    1, 1, 'subscriber1@example.com', 'SENT', 
    '{"sentAt": "' || NOW() || '"}', 
    NULL, NULL, 
    NOW(), true
),
(
    1, 1, 'subscriber1@example.com', 'OPENED', 
    '{"openedAt": "' || DATE_ADD(NOW(), INTERVAL 1 HOUR) || '", "ipAddress": "192.168.1.1", "userAgent": "Mozilla/5.0"}', 
    '192.168.1.1', 'Mozilla/5.0', 
    DATE_ADD(NOW(), INTERVAL 1 HOUR), true
),
(
    1, 1, 'subscriber2@example.com', 'SENT', 
    '{"sentAt": "' || NOW() || '"}', 
    NULL, NULL, 
    NOW(), true
);

-- Insert sample usage tracking data
INSERT INTO usage_tracking (
    tenant_id, user_id, resource_type, 
    usage_count, usage_date
) VALUES
-- Default tenant usage
(
    1, 1, 'EMAIL', 100, CURRENT_DATE()
),
(
    1, 1, 'SMS', 20, CURRENT_DATE()
),
-- Acme tenant usage
(
    2, 3, 'EMAIL', 500, CURRENT_DATE()
),
(
    2, 3, 'CAMPAIGN', 5, CURRENT_DATE()
),
-- Globex tenant usage
(
    3, 5, 'EMAIL', 2000, CURRENT_DATE()
),
(
    3, 5, 'STORAGE', 500, CURRENT_DATE()
);
