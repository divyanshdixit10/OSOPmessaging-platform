package in.osop.messaging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewMetricsDto {
    private Long totalEmailsSent;
    private Double openRate;
    private Double clickRate;
    private Double bounceRate;
    private Long totalRecipients;
    private Double deliveredRate;
    private Double unsubscribeRate;
    private Double spamComplaintRate;
}
