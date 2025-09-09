package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    
    Optional<ApiKey> findByApiKey(String apiKey);
    
    List<ApiKey> findByTenantId(Long tenantId);
    
    List<ApiKey> findByUserId(Long userId);
    
    List<ApiKey> findByTenantIdAndEnabled(Long tenantId, Boolean enabled);
    
    List<ApiKey> findByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByApiKey(String apiKey);
}
