package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignAnalyticsDto {
    private Long id;
    private String name;
    private String description;
    private CampaignStatus status;
    private Integer totalRecipients;
    private Integer sentCount;
    private Integer deliveredCount;
    private Integer openedCount;
    private Integer clickedCount;
    private Integer bouncedCount;
    private Integer unsubscribedCount;
    private Double openRate;
    private Double clickRate;
    private Double bounceRate;
    private Double unsubscribeRate;
    private Double progressPercentage; // sent / total_recipients * 100
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
