package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import in.osop.messaging_platform.exception.MessagingException;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.model.MessageLog;
import in.osop.messaging_platform.model.MessageStatus;
import in.osop.messaging_platform.repository.MessageLogRepository;
import in.osop.messaging_platform.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MessageLogRepository messageLogRepository;

    @Override
    public MessageResponse sendEmail(MessageRequest request) {
        Map<String, MessageStatus> details = new HashMap<>();
        List<String> recipients = request.getRecipients();
        
        for (String recipient : recipients) {
            try {
                sendEmailToRecipient(recipient, request);
                details.put(recipient, MessageStatus.SENT);
                logMessage(recipient, request.getMessage(), MessageStatus.SENT, null);
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", recipient, e.getMessage());
                details.put(recipient, MessageStatus.FAILED);
                logMessage(recipient, request.getMessage(), MessageStatus.FAILED, e.getMessage());
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
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(recipient);
        try {
            helper.setFrom("divyansh.osop@gmail.com", "OSOP Coding");
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Failed to set sender name: " + e.getMessage(), e);
        }
        
        helper.setSubject(request.getSubject() != null ? request.getSubject() : "No Subject");
        
        // Add tracking pixels and click tracking if enabled
        String emailContent = request.getMessage();
        String messageId = UUID.randomUUID().toString();
        
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
        
        if (request.isTrackOpens()) {
            String trackingPixel = String.format(
                "<img src='%s/api/tracking/open/%s' style='display:none' alt='' />",
                "http://localhost:8080", messageId
            );
            // Insert tracking pixel before closing body tag
            emailContent = emailContent.replace("</body>", trackingPixel + "</body>");
        }
        
        if (request.isTrackClicks()) {
            // Replace all links with tracking links, preserving HTML structure
            emailContent = emailContent.replaceAll(
                "href=['\"]([^'\"]*)['\"]",
                String.format("href='http://localhost:8080/api/tracking/click/%s?url=$1'", messageId)
            );
        }
        
        if (request.isAddUnsubscribeLink()) {
            String unsubscribeLink = String.format(
                "<div class='footer'><small><a href='http://localhost:8080/api/unsubscribe/%s'>Unsubscribe</a></small></div>",
                messageId
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
        
        mailSender.send(message);
        log.info("Email sent successfully to {} with {} attachments", recipient, 
            attachments != null ? attachments.size() : 0);
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
} 