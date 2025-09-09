package in.osop.messaging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignProgressDto {
    private Long campaignId;
    private String status;
    private Integer totalRecipients;
    private Integer emailsSent;
    private Integer emailsSuccess;
    private Integer emailsFailed;
    private Integer emailsInProgress;
    private Double progressPercentage;
    private Double successRate;
    private Double failureRate;
    private Integer currentBatchNumber;
    private Integer totalBatches;
    private LocalDateTime scheduledTime;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastBatchSentAt;
    private String errorMessage;
}
