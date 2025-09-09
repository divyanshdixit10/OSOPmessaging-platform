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
public class ApiKeyDto {
    
    private Long id;
    private String apiKey;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
