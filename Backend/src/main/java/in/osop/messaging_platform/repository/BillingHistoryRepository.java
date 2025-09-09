package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.BillingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillingHistoryRepository extends JpaRepository<BillingHistory, Long> {
    
    List<BillingHistory> findByTenantId(Long tenantId);
    
    List<BillingHistory> findByTenantIdAndStatus(Long tenantId, BillingHistory.BillingStatus status);
    
    List<BillingHistory> findByTenantIdAndBillingDateBetween(Long tenantId, LocalDate startDate, LocalDate endDate);
    
    List<BillingHistory> findByStatus(BillingHistory.BillingStatus status);
    
    List<BillingHistory> findByBillingDateBefore(LocalDate date);
}
