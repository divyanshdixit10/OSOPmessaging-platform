package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.WebhookEndpointDto;
import in.osop.messaging_platform.model.WebhookEndpoint;
import in.osop.messaging_platform.service.TenantService;
import in.osop.messaging_platform.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhook API", description = "APIs for managing webhook endpoints")
public class WebhookController {

    private final WebhookService webhookService;
    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create webhook endpoint", description = "Create a new webhook endpoint for the current tenant")
    @ApiResponse(responseCode = "201", description = "Webhook endpoint created successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<WebhookEndpointDto> createWebhook(@Valid @RequestBody WebhookEndpointDto webhookDto) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Creating webhook endpoint for tenant ID: {}", tenantId);
        WebhookEndpointDto createdWebhook = webhookService.createWebhook(tenantId, webhookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWebhook);
    }

    @GetMapping
    @Operation(summary = "Get all webhooks", description = "Get all webhook endpoints for the current tenant")
    @ApiResponse(responseCode = "200", description = "Webhook endpoints retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<List<WebhookEndpointDto>> getAllWebhooks() {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting all webhook endpoints for tenant ID: {}", tenantId);
        List<WebhookEndpointDto> webhooks = webhookService.getAllWebhooks(tenantId);
        return ResponseEntity.ok(webhooks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get webhook by ID", description = "Get a webhook endpoint by its ID")
    @ApiResponse(responseCode = "200", description = "Webhook endpoint retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<WebhookEndpointDto> getWebhookById(@PathVariable Long id) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting webhook endpoint with ID: {} for tenant ID: {}", id, tenantId);
        WebhookEndpointDto webhook = webhookService.getWebhookById(tenantId, id);
        return ResponseEntity.ok(webhook);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update webhook", description = "Update an existing webhook endpoint")
    @ApiResponse(responseCode = "200", description = "Webhook endpoint updated successfully")
    @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<WebhookEndpointDto> updateWebhook(
            @PathVariable Long id, 
            @Valid @RequestBody WebhookEndpointDto webhookDto) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Updating webhook endpoint with ID: {} for tenant ID: {}", id, tenantId);
        WebhookEndpointDto updatedWebhook = webhookService.updateWebhook(tenantId, id, webhookDto);
        return ResponseEntity.ok(updatedWebhook);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete webhook", description = "Delete a webhook endpoint by its ID")
    @ApiResponse(responseCode = "204", description = "Webhook endpoint deleted successfully")
    @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<Void> deleteWebhook(@PathVariable Long id) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Deleting webhook endpoint with ID: {} for tenant ID: {}", id, tenantId);
        webhookService.deleteWebhook(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle")
    @Operation(summary = "Toggle webhook status", description = "Enable or disable a webhook endpoint")
    @ApiResponse(responseCode = "200", description = "Webhook endpoint status toggled successfully")
    @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<WebhookEndpointDto> toggleWebhookStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, Boolean> request) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }
        
        log.debug("Toggling webhook endpoint with ID: {} to enabled: {} for tenant ID: {}", 
                id, enabled, tenantId);
        WebhookEndpointDto updatedWebhook = webhookService.toggleWebhookStatus(tenantId, id, enabled);
        return ResponseEntity.ok(updatedWebhook);
    }

    @GetMapping("/events")
    @Operation(summary = "Get available webhook events", description = "Get a list of all available webhook event types")
    @ApiResponse(responseCode = "200", description = "Webhook events retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<List<String>> getAvailableEvents() {
        log.debug("Getting available webhook events");
        List<String> events = webhookService.getAvailableEvents();
        return ResponseEntity.ok(events);
    }
}
