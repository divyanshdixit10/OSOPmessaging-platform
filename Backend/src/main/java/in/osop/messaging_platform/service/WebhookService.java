package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.WebhookEndpointDto;
import in.osop.messaging_platform.model.WebhookEndpoint;

import java.util.List;
import java.util.Map;

public interface WebhookService {
    
    /**
     * Create a new webhook endpoint
     * @param tenantId The tenant ID
     * @param webhookDto The webhook data
     * @return The created webhook endpoint
     */
    WebhookEndpointDto createWebhook(Long tenantId, WebhookEndpointDto webhookDto);
    
    /**
     * Get all webhook endpoints for a tenant
     * @param tenantId The tenant ID
     * @return List of webhook endpoints
     */
    List<WebhookEndpointDto> getAllWebhooks(Long tenantId);
    
    /**
     * Get a webhook endpoint by ID
     * @param tenantId The tenant ID
     * @param webhookId The webhook ID
     * @return The webhook endpoint
     */
    WebhookEndpointDto getWebhookById(Long tenantId, Long webhookId);
    
    /**
     * Update a webhook endpoint
     * @param tenantId The tenant ID
     * @param webhookId The webhook ID
     * @param webhookDto The updated webhook data
     * @return The updated webhook endpoint
     */
    WebhookEndpointDto updateWebhook(Long tenantId, Long webhookId, WebhookEndpointDto webhookDto);
    
    /**
     * Delete a webhook endpoint
     * @param tenantId The tenant ID
     * @param webhookId The webhook ID
     */
    void deleteWebhook(Long tenantId, Long webhookId);
    
    /**
     * Toggle webhook status (enable/disable)
     * @param tenantId The tenant ID
     * @param webhookId The webhook ID
     * @param enabled The new enabled status
     * @return The updated webhook endpoint
     */
    WebhookEndpointDto toggleWebhookStatus(Long tenantId, Long webhookId, boolean enabled);
    
    /**
     * Get a list of all available webhook event types
     * @return List of event type names
     */
    List<String> getAvailableEvents();
    
    /**
     * Send a webhook notification for an event
     * @param tenantId The tenant ID
     * @param eventType The event type
     * @param payload The event payload
     */
    void sendWebhookNotification(Long tenantId, String eventType, Map<String, Object> payload);
    
    /**
     * Get webhook endpoints that are subscribed to a specific event
     * @param tenantId The tenant ID
     * @param eventType The event type
     * @return List of webhook endpoints
     */
    List<WebhookEndpoint> getWebhooksForEvent(Long tenantId, String eventType);
}
