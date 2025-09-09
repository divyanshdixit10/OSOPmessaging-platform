package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.Campaign;
import in.osop.messaging_platform.model.CampaignStatus;
import in.osop.messaging_platform.model.MessageChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    
    List<Campaign> findByStatus(CampaignStatus status);
    
    List<Campaign> findByChannel(MessageChannel channel);
    
    List<Campaign> findByCreatedBy(String createdBy);
    
    List<Campaign> findByIsDraftTrue();
    
    List<Campaign> findByIsTestTrue();
    
    @Query("SELECT c FROM Campaign c WHERE " +
           "(:name IS NULL OR c.name LIKE %:name%) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:channel IS NULL OR c.channel = :channel) AND " +
           "(:isDraft IS NULL OR c.isDraft = :isDraft)")
    Page<Campaign> findByFilters(
        @Param("name") String name,
        @Param("status") CampaignStatus status,
        @Param("channel") MessageChannel channel,
        @Param("isDraft") Boolean isDraft,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.status = :status")
    long countByStatus(@Param("status") CampaignStatus status);
    
    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.status = :status AND c.createdAt >= :startDate")
    long countByStatusAndDateAfter(@Param("status") CampaignStatus status, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT c FROM Campaign c WHERE c.scheduledAt IS NOT NULL AND c.scheduledAt >= :now AND c.status = 'SCHEDULED'")
    List<Campaign> findScheduledCampaigns(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Campaign c WHERE c.status = 'RUNNING'")
    List<Campaign> findRunningCampaigns();
    
    @Query("SELECT SUM(c.totalRecipients) FROM Campaign c WHERE c.status = 'COMPLETED' AND c.createdAt >= :startDate")
    Long getTotalRecipientsForPeriod(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT AVG(c.openRate) FROM Campaign c WHERE c.status = 'COMPLETED' AND c.openRate IS NOT NULL")
    Double getAverageOpenRate();
    
    @Query("SELECT AVG(c.clickRate) FROM Campaign c WHERE c.status = 'COMPLETED' AND c.clickRate IS NOT NULL")
    Double getAverageClickRate();
    
    // Count campaigns by status list
    long countByStatusIn(List<CampaignStatus> statuses);
    
    // Find top recent campaigns
    List<Campaign> findTop5ByOrderByCreatedAtDesc();
    
    // Find campaign by name
    List<Campaign> findByName(String name);
}
