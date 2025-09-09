package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.CampaignProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignProgressRepository extends JpaRepository<CampaignProgress, Long> {
    
    /**
     * Find progress by campaign ID
     */
    Optional<CampaignProgress> findByCampaignId(Long campaignId);
    
    /**
     * Find all running campaigns
     */
    List<CampaignProgress> findByStatus(CampaignProgress.CampaignProgressStatus status);
    
    /**
     * Find scheduled campaigns that are ready to start
     */
    @Query("SELECT cp FROM CampaignProgress cp WHERE cp.status = 'SCHEDULED' AND cp.scheduledTime <= :now")
    List<CampaignProgress> findScheduledCampaignsReadyToStart(@Param("now") LocalDateTime now);
    
    /**
     * Find campaigns that need batch processing
     */
    @Query("SELECT cp FROM CampaignProgress cp WHERE cp.status = 'RUNNING' AND " +
           "(cp.lastBatchSentAt IS NULL OR cp.lastBatchSentAt <= :cutoffTime)")
    List<CampaignProgress> findCampaignsNeedingBatchProcessing(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Find campaigns by status list
     */
    List<CampaignProgress> findByStatusIn(List<CampaignProgress.CampaignProgressStatus> statuses);
    
    /**
     * Count campaigns by status
     */
    long countByStatus(CampaignProgress.CampaignProgressStatus status);
    
    /**
     * Find campaigns that have been running too long (for cleanup)
     */
    @Query("SELECT cp FROM CampaignProgress cp WHERE cp.status = 'RUNNING' AND cp.startedAt < :cutoffTime")
    List<CampaignProgress> findStuckCampaigns(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Find campaigns that need retry
     */
    @Query("SELECT cp FROM CampaignProgress cp WHERE cp.status = 'FAILED' AND cp.retryCount < cp.maxRetries")
    List<CampaignProgress> findCampaignsNeedingRetry();
    
    /**
     * Delete old completed campaigns (cleanup)
     */
    void deleteByStatusAndCompletedAtBefore(CampaignProgress.CampaignProgressStatus status, LocalDateTime cutoffTime);
}
