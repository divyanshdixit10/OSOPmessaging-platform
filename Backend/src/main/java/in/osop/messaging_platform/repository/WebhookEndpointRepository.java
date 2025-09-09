package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.WebhookEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, Long> {
    
    List<WebhookEndpoint> findByTenantId(Long tenantId);
    
    List<WebhookEndpoint> findByTenantIdAndEnabled(Long tenantId, Boolean enabled);
    
    @Query("SELECT w FROM WebhookEndpoint w WHERE w.tenantId = :tenantId AND w.enabled = true AND (w.events LIKE %:event% OR w.events LIKE '%*%')")
    List<WebhookEndpoint> findActiveWebhooksForEvent(@Param("tenantId") Long tenantId, @Param("event") String event);
}
