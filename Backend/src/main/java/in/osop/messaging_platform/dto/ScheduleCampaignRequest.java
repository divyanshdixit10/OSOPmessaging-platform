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
public class ScheduleCampaignRequest {
    private Long campaignId;
    private String userId;
    private LocalDateTime scheduledTime;
    private Integer batchSize;
    private Integer rateLimitPerMinute;
}
