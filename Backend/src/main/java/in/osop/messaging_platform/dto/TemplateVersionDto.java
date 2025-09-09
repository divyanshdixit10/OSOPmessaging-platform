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
public class TemplateVersionDto {
    private Long id;
    private Long templateId;
    private Integer versionNumber;
    private String contentHtml;
    private String contentText;
    private String subject;
    private String cssStyles;
    private String variables;
    private String changeDescription;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isCurrentVersion;
    private Long fileSize;
    private String metadata;
}
