package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.EmailEvent;
import in.osop.messaging_platform.model.EmailEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailEventRepository extends JpaRepository<EmailEvent, Long> {

    // Count events by campaign and event type
    long countByCampaignIdAndEventType(Long campaignId, EmailEventType eventType);

    // Count events by email and event type
    long countByEmailAndEventType(String email, EmailEventType eventType);

    // Find events by campaign
    List<EmailEvent> findByCampaignIdOrderByCreatedAtDesc(Long campaignId);

    // Find events by email
    List<EmailEvent> findByEmailOrderByCreatedAtDesc(String email);

    // Find events by campaign and event type
    List<EmailEvent> findByCampaignIdAndEventTypeOrderByCreatedAtDesc(Long campaignId, EmailEventType eventType);

    // Find events by date range
    List<EmailEvent> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    // Find events by campaign and date range
    List<EmailEvent> findByCampaignIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long campaignId, LocalDateTime startDate, LocalDateTime endDate);

    // Count total events for a campaign
    long countByCampaignId(Long campaignId);

    // Count events by event type
    long countByEventType(EmailEventType eventType);

    // Find unprocessed events
    List<EmailEvent> findByProcessedFalseOrderByCreatedAtAsc();

    // Get event statistics for a campaign
    @Query("SELECT e.eventType, COUNT(e) FROM EmailEvent e WHERE e.campaign.id = :campaignId GROUP BY e.eventType")
    List<Object[]> getEventStatsByCampaign(@Param("campaignId") Long campaignId);

    // Get event statistics for a date range
    @Query("SELECT e.eventType, COUNT(e) FROM EmailEvent e WHERE e.createdAt BETWEEN :startDate AND :endDate GROUP BY e.eventType")
    List<Object[]> getEventStatsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Get unique email addresses that opened emails for a campaign
    @Query("SELECT DISTINCT e.email FROM EmailEvent e WHERE e.campaign.id = :campaignId AND e.eventType = 'OPENED'")
    List<String> getUniqueOpenedEmailsByCampaign(@Param("campaignId") Long campaignId);

    // Get unique email addresses that clicked emails for a campaign
    @Query("SELECT DISTINCT e.email FROM EmailEvent e WHERE e.campaign.id = :campaignId AND e.eventType = 'CLICKED'")
    List<String> getUniqueClickedEmailsByCampaign(@Param("campaignId") Long campaignId);

    // Get recent events for a campaign
    @Query("SELECT e FROM EmailEvent e WHERE e.campaign.id = :campaignId ORDER BY e.createdAt DESC")
    List<EmailEvent> getRecentEventsByCampaign(@Param("campaignId") Long campaignId);

    // Get events by subscriber
    List<EmailEvent> findBySubscriberIdOrderByCreatedAtDesc(Long subscriberId);

    // Count events by subscriber and event type
    long countBySubscriberIdAndEventType(Long subscriberId, EmailEventType eventType);
    
    // Count events by event type and date range
    long countByEventTypeAndCreatedAtBetween(EmailEventType eventType, LocalDateTime startDate, LocalDateTime endDate);
    
    // Count distinct emails by date range
    @Query("SELECT COUNT(DISTINCT e.email) FROM EmailEvent e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    long countDistinctEmailByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find top recent events
    List<EmailEvent> findTop5ByOrderByCreatedAtDesc();
    
    List<EmailEvent> findByEventDataContaining(String eventData);
    
    List<EmailEvent> findByEventTypeAndCreatedAtAfter(EmailEventType eventType, LocalDateTime createdAt);
    
    // Additional methods for template analytics
    @Query("SELECT COUNT(e) FROM EmailEvent e WHERE e.campaign.template.id = :templateId")
    long countByCampaignTemplateId(@Param("templateId") Long templateId);
    
    @Query("SELECT COUNT(e) FROM EmailEvent e WHERE e.campaign.template.id = :templateId AND e.eventType = :eventType")
    long countByCampaignTemplateIdAndEventType(@Param("templateId") Long templateId, @Param("eventType") EmailEventType eventType);
    
    @Query("SELECT MAX(e.createdAt) FROM EmailEvent e WHERE e.campaign.template.id = :templateId")
    LocalDateTime findLatestByCampaignTemplateId(@Param("templateId") Long templateId);
}
