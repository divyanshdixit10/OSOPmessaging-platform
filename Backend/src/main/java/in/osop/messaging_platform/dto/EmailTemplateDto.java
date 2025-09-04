package in.osop.messaging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateDto {
    
    private Long id;
    private String name;
    private String subject;
    private String body;
    private String category;
    private String type;
    private String createdBy;
    private Boolean isDefault;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private String thumbnailUrl;
    private Map<String, String> variables;
}
