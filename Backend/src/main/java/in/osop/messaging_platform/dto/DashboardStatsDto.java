package in.osop.messaging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private Long totalEmailsSent;
    private Long activeSubscribers;
    private Double openRate;
    private Double clickRate;
    private Long totalCampaigns;
    private Long activeCampaigns;
    private List<RecentActivityDto> recentActivity;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDto {
        private String type;
        private String title;
        private String description;
        private String time;
        private String status;
    }
}
