package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.ApiKeyDto;
import in.osop.messaging_platform.dto.CreateApiKeyRequest;

import java.util.List;
import java.util.Optional;

public interface ApiKeyService {
    
    /**
     * Create a new API key
     * @param tenantId The tenant ID
     * @param userEmail The email of the user creating the API key
     * @param request The API key creation request
     * @return The created API key
     */
    ApiKeyDto createApiKey(Long tenantId, String userEmail, CreateApiKeyRequest request);
    
    /**
     * Get all API keys for a tenant
     * @param tenantId The tenant ID
     * @return List of API keys
     */
    List<ApiKeyDto> getAllApiKeys(Long tenantId);
    
    /**
     * Get an API key by ID
     * @param tenantId The tenant ID
     * @param apiKeyId The API key ID
     * @return The API key
     */
    ApiKeyDto getApiKeyById(Long tenantId, Long apiKeyId);
    
    /**
     * Delete an API key
     * @param tenantId The tenant ID
     * @param apiKeyId The API key ID
     */
    void deleteApiKey(Long tenantId, Long apiKeyId);
    
    /**
     * Toggle API key status (enable/disable)
     * @param tenantId The tenant ID
     * @param apiKeyId The API key ID
     * @param enabled The new enabled status
     * @return The updated API key
     */
    ApiKeyDto toggleApiKeyStatus(Long tenantId, Long apiKeyId, boolean enabled);
    
    /**
     * Regenerate an API key with a new value
     * @param tenantId The tenant ID
     * @param apiKeyId The API key ID
     * @return The updated API key
     */
    ApiKeyDto regenerateApiKey(Long tenantId, Long apiKeyId);
    
    /**
     * Validate an API key
     * @param apiKey The API key value
     * @return Optional containing the tenant ID if valid, empty if invalid
     */
    Optional<Long> validateApiKey(String apiKey);
    
    /**
     * Update the last used timestamp for an API key
     * @param apiKey The API key value
     */
    void updateApiKeyLastUsed(String apiKey);
    
    /**
     * Cleanup expired API keys
     */
    void cleanupExpiredApiKeys();
}
