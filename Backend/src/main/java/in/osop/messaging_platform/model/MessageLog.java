package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for logging message sending attempts.
 */
@Entity
@Table(name = "message_logs", indexes = {
    @Index(name = "idx_channel", columnList = "channel"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_recipient", columnList = "recipient"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageChannel channel;
    
    @Column(nullable = false, length = 255)
    private String recipient;
    
    @Column(columnDefinition = "TEXT")
    private String messageContent;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageStatus status;
    
    @Column(length = 50)
    private String responseCode;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    // Campaign tracking fields
    @Column(name = "campaign_id")
    private Long campaignId;
    
    @Column(name = "batch_number")
    private Integer batchNumber;
    
    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    @Builder.Default
    private Integer maxRetries = 3;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "opened_at")
    private LocalDateTime openedAt;
    
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;
    
    @Column(name = "bounced_at")
    private LocalDateTime bouncedAt;
    
    @Column(name = "unsubscribed_at")
    private LocalDateTime unsubscribedAt;
    
    @Column(name = "provider_message_id")
    private String providerMessageId;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    // Helper methods
    public boolean isSuccess() {
        return status == MessageStatus.SENT || status == MessageStatus.DELIVERED;
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries && !isSuccess();
    }
    
    public void incrementRetry() {
        this.retryCount++;
    }
} 