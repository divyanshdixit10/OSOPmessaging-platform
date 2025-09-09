package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for tracking user and system activities
 */
@Entity
@Table(name = "activity_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "performed_by", nullable = false)
    private String performedBy;
    
    @Column(name = "entity_type")
    private String entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(name = "tenant_id")
    private Long tenantId;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Types of activities that can be logged
     */
    public enum ActivityType {
        USER_LOGIN, USER_REGISTER, USER_UPDATE, USER_DELETE,
        TENANT_CREATED, TENANT_UPDATED, TENANT_SUSPENDED, TENANT_ACTIVATED,
        CAMPAIGN_CREATED, CAMPAIGN_UPDATED, CAMPAIGN_DELETED, CAMPAIGN_STARTED,
        CAMPAIGN_PAUSED, CAMPAIGN_RESUMED, CAMPAIGN_CANCELLED, CAMPAIGN_COMPLETED,
        CAMPAIGN_SCHEDULED, CAMPAIGN_TEST_SENT,
        EMAIL_SENT, EMAIL_OPENED, EMAIL_CLICKED, EMAIL_BOUNCED, EMAIL_UNSUBSCRIBED,
        TEMPLATE_CREATED, TEMPLATE_UPDATED, TEMPLATE_DELETED, TEMPLATE_IMPORTED, TEMPLATE_EXPORTED,
        SUBSCRIBER_ADDED, SUBSCRIBER_UPDATED, SUBSCRIBER_DELETED, SUBSCRIBER_IMPORTED,
        SETTINGS_UPDATED, API_KEY_CREATED, API_KEY_DELETED, WEBHOOK_CREATED, WEBHOOK_DELETED,
        BILLING_PROCESSED, PLAN_UPGRADED, PLAN_DOWNGRADED,
        SYSTEM_ALERT, SYSTEM_ERROR
    }
}