package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.ApiKeyDto;
import in.osop.messaging_platform.dto.CreateApiKeyRequest;
import in.osop.messaging_platform.exception.MessagingException;
import in.osop.messaging_platform.model.ApiKey;
import in.osop.messaging_platform.repository.ApiKeyRepository;
import in.osop.messaging_platform.repository.UserRepository;
import in.osop.messaging_platform.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private static final String API_KEY_PREFIX = "osop_";
    private static final int API_KEY_BYTES = 32; // 256 bits
    
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ApiKeyDto createApiKey(Long tenantId, String userEmail, CreateApiKeyRequest request) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new MessagingException("User not found with email: " + userEmail))
                .getId();
        
        String apiKeyValue = generateApiKey();
        
        ApiKey apiKey = ApiKey.builder()
                .tenantId(tenantId)
                .userId(userId)
                .apiKey(apiKeyValue)
                .name(request.getName())
                .description(request.getDescription())
                .enabled(true)
                .expiresAt(request.getExpiresAt())
                .build();
        
        ApiKey savedApiKey = apiKeyRepository.save(apiKey);
        log.debug("Created API key with ID: {} for tenant ID: {}", savedApiKey.getId(), tenantId);
        
        return convertToDto(savedApiKey, userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyDto> getAllApiKeys(Long tenantId) {
        List<ApiKey> apiKeys = apiKeyRepository.findByTenantId(tenantId);
        log.debug("Found {} API keys for tenant ID: {}", apiKeys.size(), tenantId);
        
        return apiKeys.stream()
                .map(apiKey -> {
                    String createdBy = userRepository.findById(apiKey.getUserId())
                            .map(user -> user.getEmail())
                            .orElse("Unknown");
                    return convertToDto(apiKey, createdBy);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiKeyDto getApiKeyById(Long tenantId, Long apiKeyId) {
        ApiKey apiKey = findApiKeyByIdAndTenantId(apiKeyId, tenantId);
        log.debug("Found API key with ID: {} for tenant ID: {}", apiKeyId, tenantId);
        
        String createdBy = userRepository.findById(apiKey.getUserId())
                .map(user -> user.getEmail())
                .orElse("Unknown");
        
        return convertToDto(apiKey, createdBy);
    }

    @Override
    @Transactional
    public void deleteApiKey(Long tenantId, Long apiKeyId) {
        ApiKey apiKey = findApiKeyByIdAndTenantId(apiKeyId, tenantId);
        apiKeyRepository.delete(apiKey);
        log.debug("Deleted API key with ID: {} for tenant ID: {}", apiKeyId, tenantId);
    }

    @Override
    @Transactional
    public ApiKeyDto toggleApiKeyStatus(Long tenantId, Long apiKeyId, boolean enabled) {
        ApiKey apiKey = findApiKeyByIdAndTenantId(apiKeyId, tenantId);
        apiKey.setEnabled(enabled);
        
        ApiKey updatedApiKey = apiKeyRepository.save(apiKey);
        log.debug("Toggled API key with ID: {} to enabled: {} for tenant ID: {}", 
                apiKeyId, enabled, tenantId);
        
        String createdBy = userRepository.findById(apiKey.getUserId())
                .map(user -> user.getEmail())
                .orElse("Unknown");
        
        return convertToDto(updatedApiKey, createdBy);
    }

    @Override
    @Transactional
    public ApiKeyDto regenerateApiKey(Long tenantId, Long apiKeyId) {
        ApiKey apiKey = findApiKeyByIdAndTenantId(apiKeyId, tenantId);
        apiKey.setApiKey(generateApiKey());
        
        ApiKey updatedApiKey = apiKeyRepository.save(apiKey);
        log.debug("Regenerated API key with ID: {} for tenant ID: {}", apiKeyId, tenantId);
        
        String createdBy = userRepository.findById(apiKey.getUserId())
                .map(user -> user.getEmail())
                .orElse("Unknown");
        
        return convertToDto(updatedApiKey, createdBy);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> validateApiKey(String apiKey) {
        return apiKeyRepository.findByApiKey(apiKey)
                .filter(key -> key.getEnabled())
                .filter(key -> key.getExpiresAt() == null || key.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(ApiKey::getTenantId);
    }

    @Override
    @Transactional
    public void updateApiKeyLastUsed(String apiKey) {
        apiKeyRepository.findByApiKey(apiKey)
                .ifPresent(key -> {
                    key.setLastUsedAt(LocalDateTime.now());
                    apiKeyRepository.save(key);
                    log.debug("Updated last used timestamp for API key: {}", key.getId());
                });
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void cleanupExpiredApiKeys() {
        LocalDateTime now = LocalDateTime.now();
        List<ApiKey> expiredKeys = apiKeyRepository.findByExpiresAtBefore(now);
        
        if (!expiredKeys.isEmpty()) {
            log.info("Cleaning up {} expired API keys", expiredKeys.size());
            apiKeyRepository.deleteAll(expiredKeys);
        }
    }
    
    private ApiKey findApiKeyByIdAndTenantId(Long apiKeyId, Long tenantId) {
        return apiKeyRepository.findById(apiKeyId)
                .filter(apiKey -> apiKey.getTenantId().equals(tenantId))
                .orElseThrow(() -> new MessagingException("API key not found with ID: " + apiKeyId));
    }
    
    private String generateApiKey() {
        byte[] randomBytes = new byte[API_KEY_BYTES];
        secureRandom.nextBytes(randomBytes);
        return API_KEY_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    private ApiKeyDto convertToDto(ApiKey apiKey, String createdBy) {
        return ApiKeyDto.builder()
                .id(apiKey.getId())
                .apiKey(apiKey.getApiKey())
                .name(apiKey.getName())
                .description(apiKey.getDescription())
                .enabled(apiKey.getEnabled())
                .expiresAt(apiKey.getExpiresAt())
                .lastUsedAt(apiKey.getLastUsedAt())
                .createdBy(createdBy)
                .createdAt(apiKey.getCreatedAt())
                .updatedAt(apiKey.getUpdatedAt())
                .build();
    }
}
