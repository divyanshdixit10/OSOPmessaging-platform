package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private EmailTemplate template;
    
    @Column(name = "template_id", insertable = false, updatable = false)
    private Long templateId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageChannel channel;
    
    @Column(name = "total_recipients")
    private Integer totalRecipients;
    
    @Column(name = "sent_count")
    @Builder.Default
    private Integer sentCount = 0;
    
    @Column(name = "delivered_count")
    @Builder.Default
    private Integer deliveredCount = 0;
    
    @Column(name = "opened_count")
    @Builder.Default
    private Integer openedCount = 0;
    
    @Column(name = "clicked_count")
    @Builder.Default
    private Integer clickedCount = 0;
    
    @Column(name = "bounced_count")
    @Builder.Default
    private Integer bouncedCount = 0;
    
    @Column(name = "unsubscribed_count")
    @Builder.Default
    private Integer unsubscribedCount = 0;
    
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Tracking settings
    @Column(name = "track_opens")
    @Builder.Default
    private Boolean trackOpens = true;
    
    @Column(name = "track_clicks")
    @Builder.Default
    private Boolean trackClicks = true;
    
    @Column(name = "add_unsubscribe_link")
    @Builder.Default
    private Boolean addUnsubscribeLink = true;
    
    // Campaign settings
    @Column(name = "is_draft")
    @Builder.Default
    private Boolean isDraft = true;
    
    @Column(name = "is_test")
    @Builder.Default
    private Boolean isTest = false;
    
    @Column(name = "test_emails", columnDefinition = "TEXT")
    private String testEmails; // JSON array of test email addresses
    
    // Performance metrics
    @Column(name = "open_rate")
    private Double openRate;
    
    @Column(name = "click_rate")
    private Double clickRate;
    
    @Column(name = "bounce_rate")
    private Double bounceRate;
    
    @Column(name = "unsubscribe_rate")
    private Double unsubscribeRate;
    
    // Helper methods for analytics
    public Double getOpenRate() {
        if (sentCount == null || sentCount == 0) return 0.0;
        return openedCount != null ? (openedCount.doubleValue() / sentCount.doubleValue()) * 100 : 0.0;
    }
    
    public Double getClickRate() {
        if (sentCount == null || sentCount == 0) return 0.0;
        return clickedCount != null ? (clickedCount.doubleValue() / sentCount.doubleValue()) * 100 : 0.0;
    }
}
