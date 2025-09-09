package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.ActivityLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDto {
    private Long id;
    private ActivityLog.ActivityType type;
    private String title;
    private String description;
    private String timeAgo;
    private LocalDateTime createdAt;
    private String entityType;
    private Long entityId;
    private String metadata;
}
