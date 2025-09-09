package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.ActivityLog;
import in.osop.messaging_platform.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for logging and retrieving activity logs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    
    /**
     * Log an activity
     */
    @Transactional
    public ActivityLog logActivity(ActivityLog.ActivityType activityType, String title, String description, 
                                  String performedBy, String entityType, Long entityId) {
        log.debug("Logging activity: {} - {}", activityType, title);
        
        ActivityLog activityLog = ActivityLog.builder()
                .activityType(activityType)
                .title(title)
                .description(description)
                .performedBy(performedBy)
                .entityType(entityType)
                .entityId(entityId)
                .createdAt(LocalDateTime.now())
                .build();
        
        return activityLogRepository.save(activityLog);
    }
    
    /**
     * Get activities for an entity
     */
    public Page<ActivityLog> getActivitiesForEntity(String entityType, Long entityId, Pageable pageable) {
        return activityLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                entityType, entityId, pageable);
    }
    
    /**
     * Get activities by user
     */
    public Page<ActivityLog> getActivitiesByUser(String performedBy, Pageable pageable) {
        return activityLogRepository.findByPerformedByOrderByCreatedAtDesc(performedBy, pageable);
    }
    
    /**
     * Get activities by type
     */
    public Page<ActivityLog> getActivitiesByType(ActivityLog.ActivityType activityType, Pageable pageable) {
        return activityLogRepository.findByActivityTypeOrderByCreatedAtDesc(activityType, pageable);
    }
    
    /**
     * Get recent activities
     */
    public Page<ActivityLog> getRecentActivities(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * Log email sent activity
     */
    @Transactional
    public void logEmailSent(String email, Long campaignId, String recipient) {
        logActivity(
            ActivityLog.ActivityType.EMAIL_SENT,
            "Email Sent",
            String.format("Email sent to %s for campaign %d", recipient, campaignId),
            "system",
            "email",
            null
        );
    }
}