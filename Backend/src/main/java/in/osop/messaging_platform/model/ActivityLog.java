package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for logging system activities for the activity feed.
 */
@Entity
@Table(name = "activity_logs", indexes = {
    @Index(name = "idx_activity_type", columnList = "activityType"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_user_id", columnList = "userId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityType activityType;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "entity_id")
    private Long entityId; // ID of the related entity (campaign, template, etc.)
    
    @Column(name = "entity_type", length = 50)
    private String entityType; // Type of entity (campaign, template, subscriber, etc.)
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string with additional data
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum ActivityType {
        CAMPAIGN_CREATED,
        CAMPAIGN_STARTED,
        CAMPAIGN_COMPLETED,
        CAMPAIGN_PAUSED,
        EMAIL_SENT,
        EMAIL_OPENED,
        EMAIL_CLICKED,
        EMAIL_BOUNCED,
        TEMPLATE_CREATED,
        TEMPLATE_UPDATED,
        SUBSCRIBER_ADDED,
        SUBSCRIBER_UNSUBSCRIBED,
        USER_LOGIN,
        USER_LOGOUT,
        SYSTEM_EVENT
    }
}
