package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.ActivityLog;
import in.osop.messaging_platform.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {
    
    private final ActivityLogRepository activityLogRepository;
    
    public void logActivity(ActivityLog.ActivityType activityType, String title, String description, 
                           String userId, String entityType, Long entityId) {
        ActivityLog activityLog = ActivityLog.builder()
            .activityType(activityType)
            .title(title)
            .description(description)
            .userId(userId)
            .entityType(entityType)
            .entityId(entityId)
            .createdAt(LocalDateTime.now())
            .build();
        
        activityLogRepository.save(activityLog);
        log.info("Logged activity: {} - {}", activityType, title);
    }
    
    public void logCampaignCreated(Long campaignId, String campaignName, String userId) {
        logActivity(
            ActivityLog.ActivityType.CAMPAIGN_CREATED,
            "Campaign Created",
            "New campaign '" + campaignName + "' has been created",
            userId,
            "campaign",
            campaignId
        );
    }
    
    public void logEmailSent(String email, Long campaignId, String userId) {
        logActivity(
            ActivityLog.ActivityType.EMAIL_SENT,
            "Email Sent",
            "Email sent to " + email,
            userId,
            "campaign",
            campaignId
        );
    }
    
    public void logEmailOpened(String email, Long campaignId) {
        logActivity(
            ActivityLog.ActivityType.EMAIL_OPENED,
            "Email Opened",
            "Email opened by " + email,
            null,
            "campaign",
            campaignId
        );
    }
    
    public void logEmailClicked(String email, Long campaignId) {
        logActivity(
            ActivityLog.ActivityType.EMAIL_CLICKED,
            "Email Clicked",
            "Email clicked by " + email,
            null,
            "campaign",
            campaignId
        );
    }
    
    public void logTemplateCreated(Long templateId, String templateName, String userId) {
        logActivity(
            ActivityLog.ActivityType.TEMPLATE_CREATED,
            "Template Created",
            "New template '" + templateName + "' has been created",
            userId,
            "template",
            templateId
        );
    }
    
    public void logSubscriberAdded(Long subscriberId, String email, String userId) {
        logActivity(
            ActivityLog.ActivityType.SUBSCRIBER_ADDED,
            "Subscriber Added",
            "New subscriber added: " + email,
            userId,
            "subscriber",
            subscriberId
        );
    }
}
