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
public class TemplateAnalyticsDto {
    private Long id;
    private String name;
    private String description;
    private Integer totalUsage; // How many emails sent using this template
    private Integer totalOpens;
    private Integer totalClicks;
    private Double openRate;
    private Double clickRate;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
}
