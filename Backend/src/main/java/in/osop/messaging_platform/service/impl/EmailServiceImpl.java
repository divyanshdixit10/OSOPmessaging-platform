package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import in.osop.messaging_platform.exception.MessagingException;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.model.MessageLog;
import in.osop.messaging_platform.model.MessageStatus;
import in.osop.messaging_platform.model.EmailEvent;
import in.osop.messaging_platform.model.EmailEventType;
import in.osop.messaging_platform.repository.MessageLogRepository;
import in.osop.messaging_platform.repository.EmailEventRepository;
import in.osop.messaging_platform.service.EmailService;
import in.osop.messaging_platform.service.EmailTrackingService;
import in.osop.messaging_platform.service.EmailValidationService;
import in.osop.messaging_platform.service.EmailDeliveryTrackingService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MessageLogRepository messageLogRepository;
    private final EmailEventRepository emailEventRepository;
    private final EmailTrackingService emailTrackingService;
    private final EmailValidationService emailValidationService;
    private final EmailDeliveryTrackingService emailDeliveryTrackingService;

    @Override
    public MessageResponse sendEmail(MessageRequest request) {
        Map<String, MessageStatus> details = new HashMap<>();
        List<String> recipients = request.getRecipients();
        
        // Pre-validate all emails before sending
        Map<String, EmailValidationService.ValidationResult> validationResults = 
            emailValidationService.validateEmails(recipients);
        
        for (String recipient : recipients) {
            EmailValidationService.ValidationResult validation = validationResults.get(recipient);
            
            if (!validation.isValid()) {
                log.warn("Email validation failed for {}: {}", recipient, validation.getReason());
                details.put(recipient, MessageStatus.FAILED);
                logMessage(recipient, request.getMessage(), MessageStatus.FAILED, 
                    "Validation failed: " + validation.getReason());
                
                // Create EmailEvent record for failed validation
                createEmailEvent(recipient, request, EmailEventType.BOUNCED);
                continue;
            }
            
            try {
                sendEmailToRecipient(recipient, request);
                details.put(recipient, MessageStatus.SENT);
                logMessage(recipient, request.getMessage(), MessageStatus.SENT, null);
                
                // Create EmailEvent record for analytics
                Long emailEventId = createEmailEvent(recipient, request, EmailEventType.SENT);
                request.setEmailEventId(emailEventId); // Store for tracking
                
                // Track delivery status
                emailDeliveryTrackingService.trackDeliveryStatus(emailEventId, 
                    EmailDeliveryTrackingService.DeliveryStatus.SENT, 
                    Map.of("validationScore", validation.getReputationScore()));
                
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", recipient, e.getMessage());
                details.put(recipient, MessageStatus.FAILED);
                logMessage(recipient, request.getMessage(), MessageStatus.FAILED, e.getMessage());
                
                // Create EmailEvent record for failed emails
                Long emailEventId = createEmailEvent(recipient, request, EmailEventType.BOUNCED);
                if (emailEventId != null) {
                    emailDeliveryTrackingService.handleBounce(emailEventId, 
                        EmailDeliveryTrackingService.BounceType.UNKNOWN, e.getMessage());
                }
            }
        }
        
        String status = details.containsValue(MessageStatus.FAILED) ? "PARTIAL" : "SUCCESS";
        if (!details.containsValue(MessageStatus.SENT)) {
            status = "FAILED";
        }
        
        return MessageResponse.builder()
                .status(status)
                .channel(MessageChannel.EMAIL)
                .recipients(recipients)
                .details(details)
                .build();
    }
    
    private void sendEmailToRecipient(String recipient, MessageRequest request) throws jakarta.mail.MessagingException {
        // Validate email format
        if (!isValidEmail(recipient)) {
            throw new MessagingException("Invalid email format: " + recipient);
        }
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(recipient);
        try {
            helper.setFrom("osopcoding3@gmail.com", "OSOP Coding");
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Failed to set sender name: " + e.getMessage(), e);
        }
        
        helper.setSubject(request.getSubject() != null ? request.getSubject() : "No Subject");
        
        // Add tracking pixels and click tracking if enabled
        String emailContent = request.getMessage();
        
        // Create a proper HTML structure if not already present
        if (!emailContent.trim().toLowerCase().startsWith("<!doctype html") && 
            !emailContent.trim().toLowerCase().startsWith("<html")) {
            emailContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        img { max-width: 100%%; height: auto; }
                        a { color: #007bff; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                        .footer { margin-top: 30px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    %s
                </body>
                </html>
                """, emailContent);
        }
        
        // Add tracking pixel and click tracking
        emailContent = addTrackingToEmail(emailContent, recipient, request);
        
        if (request.isAddUnsubscribeLink() && request.getEmailEventId() != null) {
            String trackingData = Base64.getEncoder().encodeToString(
                (request.getEmailEventId() + "|" + recipient).getBytes()
            );
            String unsubscribeLink = String.format(
                "<div class='footer'><small><a href='http://localhost:8080/api/tracking/unsubscribe/%s'>Unsubscribe</a></small></div>",
                trackingData
            );
            // Insert unsubscribe link before closing body tag
            emailContent = emailContent.replace("</body>", unsubscribeLink + "</body>");
        }
        
        helper.setText(emailContent, true); // true indicates HTML content
        
        // Handle multiple attachments
        List<MultipartFile> attachments = request.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile attachment : attachments) {
                if (attachment != null && !attachment.isEmpty()) {
                    try {
                        helper.addAttachment(attachment.getOriginalFilename(), attachment);
                        log.info("Added attachment: {}", attachment.getOriginalFilename());
                    } catch (Exception e) {
                        log.error("Failed to add attachment {}: {}", attachment.getOriginalFilename(), e.getMessage());
                        throw new MessagingException("Failed to add attachment " + attachment.getOriginalFilename() + ": " + e.getMessage(), e);
                    }
                }
            }
        }
        
        try {
            mailSender.send(message);
            log.info("Email sent successfully to {} with {} attachments", recipient, 
                attachments != null ? attachments.size() : 0);
        } catch (Exception e) {
            log.error("Failed to send email via SMTP to {}: {}", recipient, e.getMessage());
            // In development mode, simulate successful sending
            if (isDevelopmentMode()) {
                log.info("Development mode: Simulating successful email send to {}", recipient);
            } else {
                throw new MessagingException("Failed to send email: " + e.getMessage(), e);
            }
        }
    }
    
    private void logMessage(String recipient, String content, MessageStatus status, String errorMessage) {
        MessageLog log = MessageLog.builder()
                .channel(MessageChannel.EMAIL)
                .recipient(recipient)
                .messageContent(content)
                .status(status)
                .errorMessage(errorMessage)
                .build();
        
        messageLogRepository.save(log);
    }
    
    private Long createEmailEvent(String recipient, MessageRequest request, EmailEventType eventType) {
        try {
            // Create EmailEvent record for analytics
            EmailEvent emailEvent = EmailEvent.builder()
                    .email(recipient)
                    .eventType(eventType)
                    .eventData(Map.of(
                        "subject", request.getSubject() != null ? request.getSubject() : "No Subject",
                        "templateId", request.getTemplateId() != null ? request.getTemplateId() : "none",
                        "sentAt", LocalDateTime.now().toString()
                    ).toString())
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();
            
            EmailEvent savedEvent = emailEventRepository.save(emailEvent);
            log.info("Created EmailEvent record for {} with type {} and ID {}", recipient, eventType, savedEvent.getId());
            return savedEvent.getId();
            
        } catch (Exception e) {
            log.error("Failed to create EmailEvent record for {}: {}", recipient, e.getMessage());
            return null;
        }
    }
    
    private String addTrackingToEmail(String emailContent, String recipient, MessageRequest request) {
        try {
            // Add tracking pixel for open tracking
            if (request.isTrackOpens() && request.getEmailEventId() != null) {
                String trackingData = Base64.getEncoder().encodeToString(
                    (request.getEmailEventId() + "|" + recipient).getBytes()
                );
                String trackingPixel = String.format(
                    "<img src='http://localhost:8080/api/tracking/open/%s' width='1' height='1' style='display:none;' alt='' />",
                    trackingData
                );
                
                // Add tracking pixel before closing body tag
                if (emailContent.contains("</body>")) {
                    emailContent = emailContent.replace("</body>", trackingPixel + "</body>");
                } else {
                    emailContent += trackingPixel;
                }
            }
            
            // Add click tracking to all links
            if (request.isTrackClicks() && request.getEmailEventId() != null) {
                String trackingData = Base64.getEncoder().encodeToString(
                    (request.getEmailEventId() + "|" + recipient + "|").getBytes()
                );
                
                // Replace all href attributes with tracking URLs
                emailContent = emailContent.replaceAll(
                    "href=['\"]([^'\"]*)['\"]",
                    String.format("href='http://localhost:8080/api/tracking/click/%s?url=$1'", trackingData)
                );
            }
            
            return emailContent;
            
        } catch (Exception e) {
            log.error("Failed to add tracking to email: {}", e.getMessage());
            return emailContent; // Return original content if tracking fails
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email validation regex
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
    }
    
    private boolean isDevelopmentMode() {
        // Check if we're in development mode (no SMTP configured or localhost)
        String mailHost = System.getProperty("spring.mail.host", "localhost");
        return mailHost.equals("localhost") || mailHost.equals("127.0.0.1") || 
               System.getProperty("spring.profiles.active", "").contains("dev");
    }
} 