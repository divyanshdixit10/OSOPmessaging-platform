package in.osop.messaging_platform.config;

import in.osop.messaging_platform.model.User;
import in.osop.messaging_platform.model.UserRole;
import in.osop.messaging_platform.model.Subscriber;
import in.osop.messaging_platform.model.SubscriptionStatus;
import in.osop.messaging_platform.model.EmailTemplate;
import in.osop.messaging_platform.repository.UserRepository;
import in.osop.messaging_platform.repository.SubscriberRepository;
import in.osop.messaging_platform.repository.EmailTemplateRepository;
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
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
        createDemoUser();
        createSampleSubscribers();
        createSampleTemplates();
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
                "welcome",
                "transactional"
            },
            {
                "Newsletter Template",
                "Monthly Newsletter - {{month}} {{year}}",
                "<h2>Monthly Newsletter</h2><p>Hello {{firstName}},</p><p>Here's what's new this month:</p><ul><li>Feature updates</li><li>New templates</li><li>Analytics improvements</li></ul><p>Thank you for being with us!</p>",
                "newsletter",
                "marketing"
            },
            {
                "Promotional Email",
                "Special Offer - {{discount}}% Off!",
                "<h1>Limited Time Offer!</h1><p>Hi {{firstName}},</p><p>Get {{discount}}% off on all our premium features!</p><p><a href='{{link}}' style='background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Claim Offer</a></p><p>Offer expires soon!</p>",
                "promotional",
                "marketing"
            },
            {
                "Simple Text Email",
                "Important Update",
                "<p>Hello {{firstName}},</p><p>This is an important update regarding your account.</p><p>Please review the information and let us know if you have any questions.</p><p>Best regards,<br>Support Team</p>",
                "transactional",
                "transactional"
            }
        };
        
        for (String[] data : templateData) {
            if (!emailTemplateRepository.existsByName(data[0])) {
                EmailTemplate template = EmailTemplate.builder()
                        .name(data[0])
                        .subject(data[1])
                        .body(data[2])
                        .category(data[3])
                        .type(data[4])
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
}
