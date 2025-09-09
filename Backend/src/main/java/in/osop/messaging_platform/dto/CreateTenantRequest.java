package in.osop.messaging_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {
    
    @NotBlank(message = "Tenant name is required")
    @Size(min = 2, max = 50, message = "Tenant name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Tenant name can only contain letters, numbers, hyphens, and underscores")
    private String name;
    
    @NotBlank(message = "Subdomain is required")
    @Size(min = 3, max = 30, message = "Subdomain must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Subdomain can only contain lowercase letters, numbers, and hyphens")
    private String subdomain;
    
    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Please provide a valid email address")
    private String contactEmail;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String contactPhone;
    
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName;
    
    @Size(max = 500, message = "Company address cannot exceed 500 characters")
    private String companyAddress;
    
    private String timezone;
    
    private String locale;
    
    private String primaryColor;
    
    private String secondaryColor;
}
