package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.UsageTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsageTrackingRepository extends JpaRepository<UsageTracking, Long> {
    
    List<UsageTracking> findByTenantId(Long tenantId);
    
    List<UsageTracking> findByTenantIdAndResourceType(Long tenantId, UsageTracking.ResourceType resourceType);
    
    List<UsageTracking> findByTenantIdAndUsageDateBetween(Long tenantId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT u FROM UsageTracking u WHERE u.tenantId = :tenantId AND u.resourceType = :resourceType AND u.usageDate = :usageDate")
    Optional<UsageTracking> findByTenantIdAndResourceTypeAndUsageDate(
            @Param("tenantId") Long tenantId, 
            @Param("resourceType") UsageTracking.ResourceType resourceType, 
            @Param("usageDate") LocalDate usageDate);
    
    @Query("SELECT SUM(u.usageCount) FROM UsageTracking u WHERE u.tenantId = :tenantId AND u.resourceType = :resourceType AND u.usageDate BETWEEN :startDate AND :endDate")
    Long sumUsageByTenantAndResourceTypeAndDateRange(
            @Param("tenantId") Long tenantId, 
            @Param("resourceType") UsageTracking.ResourceType resourceType, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(u.usageCount) FROM UsageTracking u WHERE u.tenantId = :tenantId AND u.resourceType = :resourceType AND YEAR(u.usageDate) = :year AND MONTH(u.usageDate) = :month")
    Long sumUsageByTenantAndResourceTypeAndMonth(
            @Param("tenantId") Long tenantId, 
            @Param("resourceType") UsageTracking.ResourceType resourceType, 
            @Param("year") int year, 
            @Param("month") int month);
    
    List<UsageTracking> findByTenantIdAndResourceTypeAndUsageDateBetween(
            Long tenantId, UsageTracking.ResourceType resourceType, LocalDate startDate, LocalDate endDate);
}
