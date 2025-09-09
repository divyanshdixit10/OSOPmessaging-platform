package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "webhook_endpoints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String events; // Comma-separated list of events

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "is_active", nullable = false)
    private Boolean enabled;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods for events handling
    public List<String> getEventsList() {
        if (events == null || events.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(events.split(","));
    }

    public void setEventsList(List<String> eventsList) {
        if (eventsList == null || eventsList.isEmpty()) {
            this.events = "";
        } else {
            this.events = String.join(",", eventsList);
        }
    }

    public boolean isSubscribedToEvent(String event) {
        return getEventsList().contains(event) || getEventsList().contains("*");
    }

    public enum WebhookEvent {
        EMAIL_SENT,
        EMAIL_OPENED,
        EMAIL_CLICKED,
        EMAIL_BOUNCED,
        EMAIL_UNSUBSCRIBED,
        CAMPAIGN_STARTED,
        CAMPAIGN_COMPLETED,
        CAMPAIGN_FAILED,
        SUBSCRIBER_CREATED,
        SUBSCRIBER_UPDATED,
        SUBSCRIBER_DELETED,
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        TENANT_CREATED,
        TENANT_UPDATED,
        TENANT_DELETED,
        TEMPLATE_CREATED,
        TEMPLATE_UPDATED,
        TEMPLATE_DELETED
    }
}
