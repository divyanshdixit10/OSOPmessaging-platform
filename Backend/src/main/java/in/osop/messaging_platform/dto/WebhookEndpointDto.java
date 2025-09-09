package in.osop.messaging_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class WebhookEndpointDto {
    
    private Long id;
    
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String url;
    
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;
    
    private String description;
    
    @NotEmpty(message = "At least one event must be selected")
    private List<String> events;
    
    private String secretKey;
    
    private Boolean enabled;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
