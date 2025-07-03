package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import in.osop.messaging_platform.exception.MessagingException;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.service.EmailService;
import in.osop.messaging_platform.service.MessageService;
import in.osop.messaging_platform.service.SmsService;
import in.osop.messaging_platform.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final EmailService emailService;
    private final WhatsAppService whatsAppService;
    private final SmsService smsService;

    @Override
    public MessageResponse sendMessage(MessageRequest request) {
        // Validate request
        validateRequest(request);
        
        // Route to appropriate service based on channel
        return switch (request.getChannel()) {
            case EMAIL -> emailService.sendEmail(request);
            case WHATSAPP -> whatsAppService.sendWhatsAppMessage(request);
            case SMS -> smsService.sendSms(request);
            default -> throw new MessagingException("Unsupported channel: " + request.getChannel());
        };
    }

    @Override
    public MessageRequest handleAttachment(MultipartFile attachment, MessageRequest request) {
        if (attachment != null && !attachment.isEmpty()) {
            // Validate attachment is only for email
            if (request.getChannel() != MessageChannel.EMAIL) {
                throw new MessagingException("Attachments are only supported for EMAIL channel");
            }
            
            // Set attachment to request
            request.setAttachments(List.of(attachment));
        }
        return request;
    }
    
    private void validateRequest(MessageRequest request) {
        if (request.getChannel() == null) {
            throw new MessagingException("Channel must be specified");
        }
        
        if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
            throw new MessagingException("At least one recipient must be specified");
        }
        
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            // If template is provided, message can be empty
            if (request.getTemplateId() == null || request.getTemplateId().isBlank()) {
                throw new MessagingException("Message content is required when template is not provided");
            }
        }
        
        // Channel-specific validations
        switch (request.getChannel()) {
            case EMAIL:
                validateEmailRequest(request);
                break;
            case WHATSAPP:
                validateWhatsAppRequest(request);
                break;
            case SMS:
                validateSmsRequest(request);
                break;
        }
    }
    
    private void validateEmailRequest(MessageRequest request) {
        // Email should have a subject
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            log.warn("Email request without subject");
        }
        
        // Validate recipients format (basic check)
        for (String recipient : request.getRecipients()) {
            if (!recipient.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new MessagingException("Invalid email format: " + recipient);
            }
        }
        
        // Validate CC/BCC if provided
        if (request.getCc() != null) {
            for (String cc : request.getCc()) {
                if (!cc.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    throw new MessagingException("Invalid CC email format: " + cc);
                }
            }
        }
        
        if (request.getBcc() != null) {
            for (String bcc : request.getBcc()) {
                if (!bcc.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    throw new MessagingException("Invalid BCC email format: " + bcc);
                }
            }
        }
    }
    
    private void validateWhatsAppRequest(MessageRequest request) {
        // Basic phone number validation
        for (String recipient : request.getRecipients()) {
            if (!recipient.matches("^\\+?[0-9]{10,15}$")) {
                throw new MessagingException("Invalid phone number format for WhatsApp: " + recipient);
            }
        }
    }
    
    private void validateSmsRequest(MessageRequest request) {
        // Basic phone number validation
        for (String recipient : request.getRecipients()) {
            if (!recipient.matches("^\\+?[0-9]{10,15}$")) {
                throw new MessagingException("Invalid phone number format for SMS: " + recipient);
            }
        }
    }
} 