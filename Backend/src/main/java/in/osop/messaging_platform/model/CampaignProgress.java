package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for tracking campaign sending progress in real-time.
 */
@Entity
@Table(name = "campaign_progress", indexes = {
    @Index(name = "idx_campaign_id", columnList = "campaignId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_scheduled_time", columnList = "scheduledTime")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignProgressStatus status;
    
    @Column(name = "total_recipients", nullable = false)
    private Integer totalRecipients;
    
    @Column(name = "emails_sent", nullable = false)
    @Builder.Default
    private Integer emailsSent = 0;
    
    @Column(name = "emails_success", nullable = false)
    @Builder.Default
    private Integer emailsSuccess = 0;
    
    @Column(name = "emails_failed", nullable = false)
    @Builder.Default
    private Integer emailsFailed = 0;
    
    @Column(name = "emails_in_progress", nullable = false)
    @Builder.Default
    private Integer emailsInProgress = 0;
    
    @Column(name = "batch_size")
    @Builder.Default
    private Integer batchSize = 50;
    
    @Column(name = "rate_limit_per_minute")
    @Builder.Default
    private Integer rateLimitPerMinute = 100;
    
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "last_batch_sent_at")
    private LocalDateTime lastBatchSentAt;
    
    @Column(name = "current_batch_number")
    @Builder.Default
    private Integer currentBatchNumber = 0;
    
    @Column(name = "total_batches")
    private Integer totalBatches;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    @Builder.Default
    private Integer maxRetries = 3;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper methods
    public Double getProgressPercentage() {
        if (totalRecipients == null || totalRecipients == 0) return 0.0;
        return (emailsSent.doubleValue() / totalRecipients.doubleValue()) * 100;
    }
    
    public Double getSuccessRate() {
        if (emailsSent == null || emailsSent == 0) return 0.0;
        return (emailsSuccess.doubleValue() / emailsSent.doubleValue()) * 100;
    }
    
    public Double getFailureRate() {
        if (emailsSent == null || emailsSent == 0) return 0.0;
        return (emailsFailed.doubleValue() / emailsSent.doubleValue()) * 100;
    }
    
    public boolean isCompleted() {
        return status == CampaignProgressStatus.COMPLETED || 
               status == CampaignProgressStatus.FAILED || 
               status == CampaignProgressStatus.CANCELLED;
    }
    
    public boolean isInProgress() {
        return status == CampaignProgressStatus.RUNNING || 
               status == CampaignProgressStatus.PAUSED;
    }
    
    public enum CampaignProgressStatus {
        SCHEDULED,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}
