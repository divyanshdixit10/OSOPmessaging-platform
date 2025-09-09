package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    Optional<Tenant> findBySubdomain(String subdomain);
    
    Optional<Tenant> findByName(String name);
    
    List<Tenant> findByStatus(Tenant.TenantStatus status);
    
    List<Tenant> findByPlan(Tenant.SubscriptionPlan plan);
    
    @Query("SELECT t FROM Tenant t WHERE t.status = 'TRIAL' AND t.trialEndsAt < :now")
    List<Tenant> findExpiredTrials(@Param("now") LocalDateTime now);
    
    @Query("SELECT t FROM Tenant t WHERE t.nextBillingDate <= :date AND t.status = 'ACTIVE'")
    List<Tenant> findTenantsForBilling(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.tenant.id = :tenantId")
    Long countUsersByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.tenant.id = :tenantId AND MONTH(c.createdAt) = MONTH(:date) AND YEAR(c.createdAt) = YEAR(:date)")
    Long countCampaignsThisMonth(@Param("tenantId") Long tenantId, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(ee) FROM EmailEvent ee WHERE ee.tenant.id = :tenantId AND ee.eventType = 'SENT' AND MONTH(ee.createdAt) = MONTH(:date) AND YEAR(ee.createdAt) = YEAR(:date)")
    Long countEmailsThisMonth(@Param("tenantId") Long tenantId, @Param("date") LocalDateTime date);
    
    boolean existsBySubdomain(String subdomain);
    
    boolean existsByName(String name);
}
