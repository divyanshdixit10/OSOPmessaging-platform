package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.WebhookEndpointDto;
import in.osop.messaging_platform.exception.MessagingException;
import in.osop.messaging_platform.model.WebhookEndpoint;
import in.osop.messaging_platform.repository.WebhookEndpointRepository;
import in.osop.messaging_platform.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookService {

    private final WebhookEndpointRepository webhookEndpointRepository;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public WebhookEndpointDto createWebhook(Long tenantId, WebhookEndpointDto webhookDto) {
        WebhookEndpoint webhook = WebhookEndpoint.builder()
                .tenantId(tenantId)
                .url(webhookDto.getUrl())
                .name(webhookDto.getName())
                .description(webhookDto.getDescription())
                .secretKey(webhookDto.getSecretKey())
                .enabled(webhookDto.getEnabled() != null ? webhookDto.getEnabled() : true)
                .build();
        
        // Set events list
        webhook.setEventsList(webhookDto.getEvents());
        
        WebhookEndpoint savedWebhook = webhookEndpointRepository.save(webhook);
        log.debug("Created webhook endpoint with ID: {} for tenant ID: {}", savedWebhook.getId(), tenantId);
        
        return convertToDto(savedWebhook);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookEndpointDto> getAllWebhooks(Long tenantId) {
        List<WebhookEndpoint> webhooks = webhookEndpointRepository.findByTenantId(tenantId);
        log.debug("Found {} webhook endpoints for tenant ID: {}", webhooks.size(), tenantId);
        
        return webhooks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WebhookEndpointDto getWebhookById(Long tenantId, Long webhookId) {
        WebhookEndpoint webhook = findWebhookByIdAndTenantId(webhookId, tenantId);
        log.debug("Found webhook endpoint with ID: {} for tenant ID: {}", webhookId, tenantId);
        
        return convertToDto(webhook);
    }

    @Override
    @Transactional
    public WebhookEndpointDto updateWebhook(Long tenantId, Long webhookId, WebhookEndpointDto webhookDto) {
        WebhookEndpoint webhook = findWebhookByIdAndTenantId(webhookId, tenantId);
        
        webhook.setUrl(webhookDto.getUrl());
        webhook.setName(webhookDto.getName());
        webhook.setDescription(webhookDto.getDescription());
        webhook.setSecretKey(webhookDto.getSecretKey());
        webhook.setEnabled(webhookDto.getEnabled() != null ? webhookDto.getEnabled() : webhook.getEnabled());
        webhook.setEventsList(webhookDto.getEvents());
        
        WebhookEndpoint updatedWebhook = webhookEndpointRepository.save(webhook);
        log.debug("Updated webhook endpoint with ID: {} for tenant ID: {}", webhookId, tenantId);
        
        return convertToDto(updatedWebhook);
    }

    @Override
    @Transactional
    public void deleteWebhook(Long tenantId, Long webhookId) {
        WebhookEndpoint webhook = findWebhookByIdAndTenantId(webhookId, tenantId);
        webhookEndpointRepository.delete(webhook);
        log.debug("Deleted webhook endpoint with ID: {} for tenant ID: {}", webhookId, tenantId);
    }

    @Override
    @Transactional
    public WebhookEndpointDto toggleWebhookStatus(Long tenantId, Long webhookId, boolean enabled) {
        WebhookEndpoint webhook = findWebhookByIdAndTenantId(webhookId, tenantId);
        webhook.setEnabled(enabled);
        
        WebhookEndpoint updatedWebhook = webhookEndpointRepository.save(webhook);
        log.debug("Toggled webhook endpoint with ID: {} to enabled: {} for tenant ID: {}", 
                webhookId, enabled, tenantId);
        
        return convertToDto(updatedWebhook);
    }

    @Override
    public List<String> getAvailableEvents() {
        return Arrays.stream(WebhookEndpoint.WebhookEvent.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    @Async
    public void sendWebhookNotification(Long tenantId, String eventType, Map<String, Object> payload) {
        List<WebhookEndpoint> webhooks = getWebhooksForEvent(tenantId, eventType);
        log.debug("Found {} webhook endpoints for tenant ID: {} and event type: {}", 
                webhooks.size(), tenantId, eventType);
        
        if (webhooks.isEmpty()) {
            return;
        }
        
        // Add metadata to payload
        Map<String, Object> fullPayload = new HashMap<>(payload);
        fullPayload.put("event_type", eventType);
        fullPayload.put("tenant_id", tenantId);
        fullPayload.put("timestamp", LocalDateTime.now().toString());
        
        // Send webhook notification to each endpoint
        for (WebhookEndpoint webhook : webhooks) {
            try {
                sendWebhookRequest(webhook, eventType, fullPayload);
            } catch (Exception e) {
                log.error("Failed to send webhook notification to endpoint: {} for event: {}", 
                        webhook.getUrl(), eventType, e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookEndpoint> getWebhooksForEvent(Long tenantId, String eventType) {
        return webhookEndpointRepository.findActiveWebhooksForEvent(tenantId, eventType);
    }
    
    private WebhookEndpoint findWebhookByIdAndTenantId(Long webhookId, Long tenantId) {
        return webhookEndpointRepository.findById(webhookId)
                .filter(webhook -> webhook.getTenantId().equals(tenantId))
                .orElseThrow(() -> new MessagingException("Webhook endpoint not found with ID: " + webhookId));
    }
    
    private WebhookEndpointDto convertToDto(WebhookEndpoint webhook) {
        return WebhookEndpointDto.builder()
                .id(webhook.getId())
                .url(webhook.getUrl())
                .name(webhook.getName())
                .description(webhook.getDescription())
                .events(webhook.getEventsList())
                .secretKey(webhook.getSecretKey())
                .enabled(webhook.getEnabled())
                .createdAt(webhook.getCreatedAt())
                .updatedAt(webhook.getUpdatedAt())
                .build();
    }
    
    private void sendWebhookRequest(WebhookEndpoint webhook, String eventType, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Webhook-Event", eventType);
        
        // Add signature if secret key is present
        if (webhook.getSecretKey() != null && !webhook.getSecretKey().isEmpty()) {
            String signature = generateSignature(payload, webhook.getSecretKey());
            headers.set("X-Webhook-Signature", signature);
        }
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        
        log.debug("Sending webhook notification to: {} for event: {}", webhook.getUrl(), eventType);
        restTemplate.postForEntity(webhook.getUrl(), request, String.class);
    }
    
    private String generateSignature(Map<String, Object> payload, String secretKey) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            
            byte[] signatureBytes = sha256Hmac.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
            
        } catch (Exception e) {
            log.error("Failed to generate webhook signature", e);
            return "";
        }
    }
}
