package in.osop.messaging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStatsDto {
    private Long totalEmailsSent;
    private Long activeSubscribers;
    private Double openRate;
    private Double clickRate;
    private Long totalCampaigns;
    private Long activeCampaigns;
    private LocalDateTime lastUpdated;
    private List<RecentActivityDto> recentActivity;
}
