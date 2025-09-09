package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.ApiKeyDto;
import in.osop.messaging_platform.dto.CreateApiKeyRequest;
import in.osop.messaging_platform.service.ApiKeyService;
import in.osop.messaging_platform.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/api-keys")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "API Key Management", description = "APIs for managing API keys for authentication")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create API key", description = "Create a new API key for the current tenant")
    @ApiResponse(responseCode = "201", description = "API key created successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<ApiKeyDto> createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request,
            Authentication authentication) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        String userEmail = authentication.getName();
        log.debug("Creating API key for tenant ID: {} by user: {}", tenantId, userEmail);
        
        ApiKeyDto apiKey = apiKeyService.createApiKey(tenantId, userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKey);
    }

    @GetMapping
    @Operation(summary = "Get all API keys", description = "Get all API keys for the current tenant")
    @ApiResponse(responseCode = "200", description = "API keys retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<List<ApiKeyDto>> getAllApiKeys() {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting all API keys for tenant ID: {}", tenantId);
        
        List<ApiKeyDto> apiKeys = apiKeyService.getAllApiKeys(tenantId);
        return ResponseEntity.ok(apiKeys);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get API key by ID", description = "Get an API key by its ID")
    @ApiResponse(responseCode = "200", description = "API key retrieved successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<ApiKeyDto> getApiKeyById(@PathVariable Long id) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting API key with ID: {} for tenant ID: {}", id, tenantId);
        
        ApiKeyDto apiKey = apiKeyService.getApiKeyById(tenantId, id);
        return ResponseEntity.ok(apiKey);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete API key", description = "Delete an API key by its ID")
    @ApiResponse(responseCode = "204", description = "API key deleted successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<Void> deleteApiKey(@PathVariable Long id) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Deleting API key with ID: {} for tenant ID: {}", id, tenantId);
        
        apiKeyService.deleteApiKey(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle")
    @Operation(summary = "Toggle API key status", description = "Enable or disable an API key")
    @ApiResponse(responseCode = "200", description = "API key status toggled successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<ApiKeyDto> toggleApiKeyStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, Boolean> request) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }
        
        log.debug("Toggling API key with ID: {} to enabled: {} for tenant ID: {}", 
                id, enabled, tenantId);
        ApiKeyDto apiKey = apiKeyService.toggleApiKeyStatus(tenantId, id, enabled);
        return ResponseEntity.ok(apiKey);
    }

    @PostMapping("/{id}/regenerate")
    @Operation(summary = "Regenerate API key", description = "Regenerate an API key with a new value")
    @ApiResponse(responseCode = "200", description = "API key regenerated successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<ApiKeyDto> regenerateApiKey(@PathVariable Long id) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Regenerating API key with ID: {} for tenant ID: {}", id, tenantId);
        
        ApiKeyDto apiKey = apiKeyService.regenerateApiKey(tenantId, id);
        return ResponseEntity.ok(apiKey);
    }
}
