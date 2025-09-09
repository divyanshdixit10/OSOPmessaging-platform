package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.EmailTemplate;
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
public class TemplateDto {
    private Long id;
    private String name;
    private String subject;
    private String contentHtml;
    private String contentText;
    private EmailTemplate.TemplateCategory category;
    private EmailTemplate.TemplateType type;
    private String createdBy;
    private Boolean isDefault;
    private Boolean isActive;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private String thumbnailUrl;
    private String variables;
    private Integer version;
    private Long parentTemplateId;
    private Integer usageCount;
    private LocalDateTime lastUsedAt;
    private List<String> tags;
    private String cssStyles;
    private String metadata;
}
