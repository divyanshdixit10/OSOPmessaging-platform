package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemplateRequest {
    
    @NotBlank(message = "Template name is required")
    private String name;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Content HTML is required")
    private String contentHtml;
    
    private String contentText;
    
    private EmailTemplate.TemplateCategory category;
    
    private EmailTemplate.TemplateType type;
    
    private String description;
    
    private String cssStyles;
    
    private String variables; // JSON string
    
    private List<String> tags;
    
    private Boolean isPublic;
    
    private Boolean isActive;
    
    private String metadata; // JSON string
    
    private String changeDescription; // For version history
}
