package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import in.osop.messaging_platform.exception.MessagingException;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.model.MessageLog;
import in.osop.messaging_platform.model.MessageStatus;
import in.osop.messaging_platform.repository.MessageLogRepository;
import in.osop.messaging_platform.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final WebClient smsWebClient;
    private final MessageLogRepository messageLogRepository;

    @Value("${sms.api.url}")
    private String smsApiUrl;

    @Value("${sms.api.key}")
    private String smsApiKey;

    @Value("${sms.sender.id}")
    private String smsSenderId;

    @Override
    public MessageResponse sendSms(MessageRequest request) {
        Map<String, MessageStatus> details = new HashMap<>();
        List<String> recipients = request.getRecipients();
        
        for (String recipient : recipients) {
            try {
                sendSmsToRecipient(recipient, request);
                details.put(recipient, MessageStatus.SENT);
                logMessage(recipient, request.getMessage(), MessageStatus.SENT, null);
            } catch (Exception e) {
                log.error("Failed to send SMS to {}: {}", recipient, e.getMessage());
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
                .channel(MessageChannel.SMS)
                .recipients(recipients)
                .details(details)
                .build();
    }
    
    private void sendSmsToRecipient(String recipient, MessageRequest request) {
        // Prepare message content
        String messageContent = request.getMessage();
        
        // Apply template and placeholders if needed
        if (request.getTemplateId() != null && !request.getTemplateId().isEmpty() && 
            request.getPlaceholders() != null && !request.getPlaceholders().isEmpty()) {
            messageContent = applyTemplate(request.getTemplateId(), request.getPlaceholders());
        }
        
        // Build request body for SMS gateway
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("apikey", smsApiKey);
        requestBody.put("sender", smsSenderId);
        requestBody.put("mobile", recipient);
        requestBody.put("message", messageContent);
        
        // Make API call to SMS gateway
        try {
            String response = smsWebClient.post()
                    .uri(smsApiUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new MessagingException("SMS API error: " + errorBody)))
                    )
                    .bodyToMono(String.class)
                    .block();
            
            log.info("SMS sent successfully to {}: {}", recipient, response);
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage());
            throw new MessagingException("Failed to send SMS: " + e.getMessage(), e);
        }
    }
    
    private String applyTemplate(String templateId, Map<String, String> placeholders) {
        // This is a simplified implementation. In a real application,
        // you might fetch templates from a database or external service.
        String template = getTemplateContent(templateId);
        
        // Replace placeholders with actual values
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        return template;
    }
    
    private String getTemplateContent(String templateId) {
        // Simplified implementation - in a real app, fetch from DB or config
        // Here we just return a dummy template for demonstration
        return "This is a template message with ID: " + templateId + ". " +
               "It can contain placeholders like {{name}} and {{date}}.";
    }
    
    private void logMessage(String recipient, String content, MessageStatus status, String errorMessage) {
        MessageLog log = MessageLog.builder()
                .channel(MessageChannel.SMS)
                .recipient(recipient)
                .messageContent(content)
                .status(status)
                .errorMessage(errorMessage)
                .build();
        
        messageLogRepository.save(log);
    }
} 