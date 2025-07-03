package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import in.osop.messaging_platform.exception.MessagingException;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.model.MessageLog;
import in.osop.messaging_platform.model.MessageStatus;
import in.osop.messaging_platform.repository.MessageLogRepository;
import in.osop.messaging_platform.service.WhatsAppService;
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
public class WhatsAppServiceImpl implements WhatsAppService {

    private final WebClient whatsappWebClient;
    private final MessageLogRepository messageLogRepository;

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token}")
    private String whatsappApiToken;

    @Value("${whatsapp.phone.id}")
    private String whatsappPhoneId;

    @Override
    public MessageResponse sendWhatsAppMessage(MessageRequest request) {
        Map<String, MessageStatus> details = new HashMap<>();
        List<String> recipients = request.getRecipients();
        
        for (String recipient : recipients) {
            try {
                sendWhatsAppToRecipient(recipient, request);
                details.put(recipient, MessageStatus.SENT);
                logMessage(recipient, request.getMessage(), MessageStatus.SENT, null);
            } catch (Exception e) {
                log.error("Failed to send WhatsApp message to {}: {}", recipient, e.getMessage());
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
                .channel(MessageChannel.WHATSAPP)
                .recipients(recipients)
                .details(details)
                .build();
    }
    
    private void sendWhatsAppToRecipient(String recipient, MessageRequest request) {
        String endpoint = whatsappApiUrl + "/" + whatsappPhoneId + "/messages";
        
        // Normalize phone number to WhatsApp format if needed
        String normalizedPhone = normalizePhoneNumber(recipient);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messaging_product", "whatsapp");
        requestBody.put("recipient_type", "individual");
        requestBody.put("to", normalizedPhone);
        
        // Handle template-based or text message
        if (request.getTemplateId() != null && !request.getTemplateId().isEmpty()) {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("name", request.getTemplateId());
            
            // Handle template parameters if available
            if (request.getPlaceholders() != null && !request.getPlaceholders().isEmpty()) {
                Map<String, Object> components = new HashMap<>();
                components.put("type", "body");
                
                // Format parameters for WhatsApp API
                List<Map<String, String>> parameters = request.getPlaceholders().entrySet().stream()
                        .map(entry -> {
                            Map<String, String> param = new HashMap<>();
                            param.put("type", "text");
                            param.put("text", entry.getValue());
                            return param;
                        })
                        .toList();
                
                components.put("parameters", parameters);
                templateData.put("components", List.of(components));
            }
            
            requestBody.put("template", templateData);
        } else {
            // Regular text message
            Map<String, Object> textMessage = new HashMap<>();
            textMessage.put("preview_url", false);
            textMessage.put("body", request.getMessage());
            requestBody.put("type", "text");
            requestBody.put("text", textMessage);
        }
        
        // // Handle media if present
        // if (request.getMediaUrl() != null && !request.getMediaUrl().isEmpty()) {
        //     String mediaType = determineMediaType(request.getMediaUrl());
        //     Map<String, Object> mediaMessage = new HashMap<>();
        //     mediaMessage.put("link", request.getMediaUrl());
            
        //     requestBody.put("type", mediaType);
        //     requestBody.put(mediaType, mediaMessage);
        // }
        
        // Make API call to WhatsApp
        try {
            String response = whatsappWebClient.post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + whatsappApiToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new MessagingException("WhatsApp API error: " + errorBody)))
                    )
                    .bodyToMono(String.class)
                    .block();
            
            log.info("WhatsApp message sent successfully to {}: {}", recipient, response);
        } catch (Exception e) {
            log.error("Error sending WhatsApp message: {}", e.getMessage());
            throw new MessagingException("Failed to send WhatsApp message: " + e.getMessage(), e);
        }
    }
    
    private String normalizePhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String digits = phoneNumber.replaceAll("\\D", "");
        
        // Ensure the number has the international format required by WhatsApp
        if (!digits.startsWith("1") && !digits.startsWith("91")) {
            // Add default country code if missing (adjust as needed)
            return "91" + digits;
        }
        
        return digits;
    }
    
    private String determineMediaType(String mediaUrl) {
        String lowerCaseUrl = mediaUrl.toLowerCase();
        if (lowerCaseUrl.endsWith(".jpg") || lowerCaseUrl.endsWith(".jpeg") || lowerCaseUrl.endsWith(".png")) {
            return "image";
        } else if (lowerCaseUrl.endsWith(".pdf")) {
            return "document";
        } else if (lowerCaseUrl.endsWith(".mp4") || lowerCaseUrl.endsWith(".mov")) {
            return "video";
        } else {
            return "document"; // Default to document type
        }
    }
    
    private void logMessage(String recipient, String content, MessageStatus status, String errorMessage) {
        MessageLog log = MessageLog.builder()
                .channel(MessageChannel.WHATSAPP)
                .recipient(recipient)
                .messageContent(content)
                .status(status)
                .errorMessage(errorMessage)
                .build();
        
        messageLogRepository.save(log);
    }
} 