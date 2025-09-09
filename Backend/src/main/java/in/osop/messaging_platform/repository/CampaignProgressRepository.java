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
    
    Optional<CampaignProgress> findByCampaignId(Long campaignId);
    
    List<CampaignProgress> findByStatus(CampaignProgress.CampaignProgressStatus status);
    
    List<CampaignProgress> findByStatusIn(List<CampaignProgress.CampaignProgressStatus> statuses);
    
    @Query("SELECT cp FROM CampaignProgress cp WHERE cp.status = 'SCHEDULED' AND cp.scheduledTime <= :now")
    List<CampaignProgress> findScheduledCampaignsReadyToStart(@Param("now") LocalDateTime now);
}