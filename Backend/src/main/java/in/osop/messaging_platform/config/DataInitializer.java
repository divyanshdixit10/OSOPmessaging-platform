package in.osop.messaging_platform.config;

import in.osop.messaging_platform.model.*;
import in.osop.messaging_platform.repository.UserRepository;
import in.osop.messaging_platform.repository.SubscriberRepository;
import in.osop.messaging_platform.repository.EmailTemplateRepository;
import in.osop.messaging_platform.repository.CampaignRepository;
import in.osop.messaging_platform.repository.EmailEventRepository;
import in.osop.messaging_platform.repository.ActivityLogRepository;
import in.osop.messaging_platform.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final SubscriberRepository subscriberRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final CampaignRepository campaignRepository;
    private final EmailEventRepository emailEventRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
        createDemoUser();
        createSampleSubscribers();
        createSampleTemplates();
        createSampleCampaigns();
        createSampleEmailEvents();
        createSampleActivityLogs();
    }
    
    private void createDefaultAdmin() {
        // Check if admin already exists
        if (userRepository.existsByEmail("admin@osop.com")) {
            log.info("Admin user already exists");
            return;
        }
        
        // Create default admin user
        User admin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@osop.com")
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .enabled(true)
                .emailVerified(true)
                .build();
        
        userRepository.save(admin);
        log.info("Default admin user created successfully!");
        log.info("Email: admin@osop.com");
        log.info("Password: admin123");
    }
    
    private void createDemoUser() {
        // Check if demo user already exists
        if (userRepository.existsByEmail("demo@osop.com")) {
            log.info("Demo user already exists");
            return;
        }
        
        // Create demo user
        User demoUser = User.builder()
                .firstName("Demo")
                .lastName("User")
                .email("demo@osop.com")
                .password(passwordEncoder.encode("demo123"))
                .role(UserRole.USER)
                .enabled(true)
                .emailVerified(true)
                .build();
        
        userRepository.save(demoUser);
        log.info("Demo user created successfully!");
        log.info("Email: demo@osop.com");
        log.info("Password: demo123");
    }
    
    private void createSampleSubscribers() {
        // Create sample subscribers for testing
        String[] sampleEmails = {
            "john.doe@example.com",
            "jane.smith@example.com", 
            "bob.wilson@example.com",
            "alice.brown@example.com",
            "charlie.davis@example.com"
        };
        
        String[] firstNames = {"John", "Jane", "Bob", "Alice", "Charlie"};
        String[] lastNames = {"Doe", "Smith", "Wilson", "Brown", "Davis"};
        
        for (int i = 0; i < sampleEmails.length; i++) {
            if (!subscriberRepository.existsByEmail(sampleEmails[i])) {
                Subscriber subscriber = Subscriber.builder()
                        .email(sampleEmails[i])
                        .firstName(firstNames[i])
                        .lastName(lastNames[i])
                        .status(SubscriptionStatus.ACTIVE)
                        .isVerified(true)
                        .source("manual")
                        .build();
                
                subscriberRepository.save(subscriber);
                log.info("Created sample subscriber: {}", sampleEmails[i]);
            }
        }
        
        log.info("Sample subscribers created successfully!");
    }
    
    private void createSampleTemplates() {
        // Create sample email templates for testing
        String[][] templateData = {
            {
                "Welcome Email",
                "Welcome to OSOP Messaging Platform!",
                "<h1>Welcome {{firstName}}!</h1><p>Thank you for joining our platform. We're excited to have you on board!</p><p>Best regards,<br>The OSOP Team</p>",
                "WELCOME",
                "HTML"
            },
            {
                "Newsletter Template",
                "Monthly Newsletter - {{month}} {{year}}",
                "<h2>Monthly Newsletter</h2><p>Hello {{firstName}},</p><p>Here's what's new this month:</p><ul><li>Feature updates</li><li>New templates</li><li>Analytics improvements</li></ul><p>Thank you for being with us!</p>",
                "NEWSLETTER",
                "HTML"
            },
            {
                "Promotional Email",
                "Special Offer - {{discount}}% Off!",
                "<h1>Limited Time Offer!</h1><p>Hi {{firstName}},</p><p>Get {{discount}}% off on all our premium features!</p><p><a href='{{link}}' style='background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Claim Offer</a></p><p>Offer expires soon!</p>",
                "PROMOTION",
                "HTML"
            },
            {
                "Simple Text Email",
                "Important Update",
                "<p>Hello {{firstName}},</p><p>This is an important update regarding your account.</p><p>Please review the information and let us know if you have any questions.</p><p>Best regards,<br>Support Team</p>",
                "TRANSACTIONAL",
                "HTML"
            }
        };
        
        for (String[] data : templateData) {
            if (!emailTemplateRepository.existsByName(data[0])) {
                EmailTemplate template = EmailTemplate.builder()
                        .name(data[0])
                        .subject(data[1])
                        .contentHtml(data[2])
                        .category(EmailTemplate.TemplateCategory.valueOf(data[3]))
                        .type(EmailTemplate.TemplateType.valueOf(data[4]))
                        .description("Sample template for " + data[3] + " emails")
                        .variables("firstName,lastName,email")
                        .isActive(true)
                        .isDefault(false)
                        .createdBy("admin@osop.com")
                        .build();
                
                emailTemplateRepository.save(template);
                log.info("Created sample template: {}", data[0]);
            }
        }
        
        log.info("Sample email templates created successfully!");
    }
    
    private void createSampleCampaigns() {
        // Get templates for campaigns
        var templates = emailTemplateRepository.findAll();
        if (templates.isEmpty()) {
            log.info("No templates found, skipping campaign creation");
            return;
        }
        
        // Create sample campaigns
        String[][] campaignData = {
            {
                "Welcome Series - Day 1",
                "Welcome new subscribers to our platform",
                "Welcome to OSOP! We're excited to have you on board.",
                "RUNNING",
                "EMAIL"
            },
            {
                "Monthly Newsletter - December 2024",
                "Monthly newsletter with updates and news",
                "Here's what's new this month in OSOP Messaging Platform.",
                "COMPLETED",
                "EMAIL"
            },
            {
                "Product Launch Announcement",
                "Announcement for new product features",
                "Exciting news! We've launched new features for better email marketing.",
                "SCHEDULED",
                "EMAIL"
            },
            {
                "Holiday Promotion",
                "Special holiday offer for premium features",
                "Get 50% off on all premium features this holiday season!",
                "DRAFT",
                "EMAIL"
            }
        };
        
        for (int i = 0; i < campaignData.length; i++) {
            String campaignName = campaignData[i][0];
            if (campaignRepository.findByName(campaignName).isEmpty()) {
                Campaign campaign = Campaign.builder()
                        .name(campaignName)
                        .description(campaignData[i][1])
                        .subject(campaignData[i][2])
                        .body(templates.get(i % templates.size()).getContentHtml())
                        .template(templates.get(i % templates.size()))
                        .status(CampaignStatus.valueOf(campaignData[i][3]))
                        .channel(MessageChannel.valueOf(campaignData[i][4]))
                        .totalRecipients(100 + (i * 50))
                        .sentCount(i == 0 ? 75 : i == 1 ? 100 : i == 2 ? 0 : 0)
                        .deliveredCount(i == 0 ? 70 : i == 1 ? 95 : 0)
                        .openedCount(i == 0 ? 35 : i == 1 ? 28 : 0)
                        .clickedCount(i == 0 ? 7 : i == 1 ? 5 : 0)
                        .bouncedCount(i == 0 ? 5 : i == 1 ? 5 : 0)
                        .unsubscribedCount(i == 0 ? 1 : i == 1 ? 2 : 0)
                        .createdBy("admin@osop.com")
                        .build();
                
                campaignRepository.save(campaign);
                log.info("Created sample campaign: {}", campaignName);
            }
        }
        
        log.info("Sample campaigns created successfully!");
    }
    
    private void createSampleEmailEvents() {
        var campaigns = campaignRepository.findAll();
        var subscribers = subscriberRepository.findAll();
        
        if (campaigns.isEmpty() || subscribers.isEmpty()) {
            log.info("No campaigns or subscribers found, skipping email events creation");
            return;
        }
        
        // Create sample email events for the first campaign
        Campaign firstCampaign = campaigns.get(0);
        int eventCount = 0;
        
        for (Subscriber subscriber : subscribers) {
            // Email sent event
            EmailEvent sentEvent = EmailEvent.builder()
                    .campaign(firstCampaign)
                    .subscriber(subscriber)
                    .email(subscriber.getEmail())
                    .eventType(EmailEventType.SENT)
                    .createdAt(java.time.LocalDateTime.now().minusHours(eventCount))
                    .processed(true)
                    .processedAt(java.time.LocalDateTime.now().minusHours(eventCount))
                    .build();
            emailEventRepository.save(sentEvent);
            eventCount++;
            
            // Random open event (70% chance)
            if (Math.random() < 0.7) {
                EmailEvent openEvent = EmailEvent.builder()
                        .campaign(firstCampaign)
                        .subscriber(subscriber)
                        .email(subscriber.getEmail())
                        .eventType(EmailEventType.OPENED)
                        .createdAt(java.time.LocalDateTime.now().minusHours(eventCount - 1))
                        .processed(true)
                        .processedAt(java.time.LocalDateTime.now().minusHours(eventCount - 1))
                        .build();
                emailEventRepository.save(openEvent);
            }
            
            // Random click event (20% chance)
            if (Math.random() < 0.2) {
                EmailEvent clickEvent = EmailEvent.builder()
                        .campaign(firstCampaign)
                        .subscriber(subscriber)
                        .email(subscriber.getEmail())
                        .eventType(EmailEventType.CLICKED)
                        .createdAt(java.time.LocalDateTime.now().minusHours(eventCount - 2))
                        .processed(true)
                        .processedAt(java.time.LocalDateTime.now().minusHours(eventCount - 2))
                        .build();
                emailEventRepository.save(clickEvent);
            }
        }
        
        log.info("Sample email events created successfully!");
    }
    
    private void createSampleActivityLogs() {
        var campaigns = campaignRepository.findAll();
        var templates = emailTemplateRepository.findAll();
        var subscribers = subscriberRepository.findAll();
        
        // Create sample activity logs
        String[] activities = {
            "Campaign 'Welcome Series - Day 1' has been created",
            "Email sent to john.doe@example.com",
            "Email opened by jane.smith@example.com",
            "Email clicked by bob.wilson@example.com",
            "Template 'Welcome Email' has been created",
            "New subscriber added: alice.brown@example.com",
            "Campaign 'Monthly Newsletter - December 2024' completed",
            "Email sent to charlie.davis@example.com",
            "Email opened by john.doe@example.com",
            "Template 'Newsletter Template' has been updated"
        };
        
        ActivityLog.ActivityType[] activityTypes = {
            ActivityLog.ActivityType.CAMPAIGN_CREATED,
            ActivityLog.ActivityType.EMAIL_SENT,
            ActivityLog.ActivityType.EMAIL_OPENED,
            ActivityLog.ActivityType.EMAIL_CLICKED,
            ActivityLog.ActivityType.TEMPLATE_CREATED,
            ActivityLog.ActivityType.SUBSCRIBER_ADDED,
            ActivityLog.ActivityType.CAMPAIGN_COMPLETED,
            ActivityLog.ActivityType.EMAIL_SENT,
            ActivityLog.ActivityType.EMAIL_OPENED,
            ActivityLog.ActivityType.TEMPLATE_UPDATED
        };
        
        for (int i = 0; i < activities.length; i++) {
            ActivityLog activityLog = ActivityLog.builder()
                    .activityType(activityTypes[i])
                    .title(activities[i].split(" - ")[0])
                    .description(activities[i])
                    .performedBy("admin@osop.com")
                    .entityType(i < 3 ? "campaign" : i < 5 ? "template" : "subscriber")
                    .entityId(i < 3 ? (campaigns.isEmpty() ? 1L : campaigns.get(0).getId()) : 
                             i < 5 ? (templates.isEmpty() ? 1L : templates.get(0).getId()) : 
                             (subscribers.isEmpty() ? 1L : subscribers.get(0).getId()))
                    .createdAt(java.time.LocalDateTime.now().minusHours(i))
                    .build();
            
            activityLogRepository.save(activityLog);
        }
        
        log.info("Sample activity logs created successfully!");
    }
}
