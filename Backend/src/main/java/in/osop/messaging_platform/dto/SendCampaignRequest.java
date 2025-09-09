package in.osop.messaging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCampaignRequest {
    private Long campaignId;
    private String userId;
    private Integer batchSize;
    private Integer rateLimitPerMinute;
}
