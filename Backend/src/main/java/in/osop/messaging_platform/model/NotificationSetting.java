package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private Boolean enabled;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum NotificationType {
        EMAIL, PUSH, IN_APP
    }

    public enum EventType {
        CAMPAIGN_CREATED,
        CAMPAIGN_STARTED,
        CAMPAIGN_COMPLETED,
        CAMPAIGN_FAILED,
        QUOTA_WARNING,
        QUOTA_EXCEEDED,
        SUBSCRIBER_IMPORT_COMPLETED,
        SUBSCRIBER_IMPORT_FAILED,
        TRIAL_ENDING_SOON,
        BILLING_PAYMENT_SUCCESS,
        BILLING_PAYMENT_FAILED,
        SECURITY_ALERT,
        NEW_USER_JOINED,
        SYSTEM_MAINTENANCE
    }
}
