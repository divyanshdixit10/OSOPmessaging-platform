package in.osop.messaging_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateApiKeyRequest {
    
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;
    
    private String description;
    
    private LocalDateTime expiresAt;
}
