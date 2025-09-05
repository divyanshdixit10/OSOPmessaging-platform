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
public class EmailTemplateDto {
    private Long id;
    private String name;
    private String subject;
    private String body;
    private String category;
    private String type;
    private String description;
    private String variables;
    private Boolean isActive;
    private Boolean isDefault;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}