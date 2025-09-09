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
@Table(name = "campaign_progress")
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
    @Column(nullable = false)
    private CampaignProgressStatus status;

    @Column(name = "total_recipients", nullable = false)
    private Integer totalRecipients;

    @Column(name = "emails_sent", nullable = false)
    private Integer emailsSent;

    @Column(name = "emails_success", nullable = false)
    private Integer emailsSuccess;

    @Column(name = "emails_failed", nullable = false)
    private Integer emailsFailed;

    @Column(name = "emails_in_progress", nullable = false)
    private Integer emailsInProgress;

    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage;

    @Column(name = "current_batch_number", nullable = false)
    private Integer currentBatchNumber;

    @Column(name = "total_batches", nullable = false)
    private Integer totalBatches;

    @Column(name = "batch_size", nullable = false)
    private Integer batchSize;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "paused_at")
    private LocalDateTime pausedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "rate_limit_per_minute")
    private Integer rateLimitPerMinute;

    @Column(name = "last_batch_sent_at")
    private LocalDateTime lastBatchSentAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CampaignProgressStatus {
        SCHEDULED, RUNNING, PAUSED, COMPLETED, CANCELLED, FAILED
    }

    /**
     * Calculate and update the progress percentage based on sent emails
     */
    public void updateProgressPercentage() {
        if (totalRecipients > 0) {
            this.progressPercentage = (double) emailsSent / totalRecipients * 100.0;
        } else {
            this.progressPercentage = 0.0;
        }
    }

    /**
     * Calculate success rate
     */
    public double getSuccessRate() {
        if (emailsSent > 0) {
            return (double) emailsSuccess / emailsSent * 100.0;
        }
        return 0.0;
    }

    /**
     * Calculate failure rate
     */
    public double getFailureRate() {
        if (emailsSent > 0) {
            return (double) emailsFailed / emailsSent * 100.0;
        }
        return 0.0;
    }
}