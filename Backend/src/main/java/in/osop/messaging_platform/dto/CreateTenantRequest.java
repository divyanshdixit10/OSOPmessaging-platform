package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new tenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Subdomain is required")
    @Size(min = 2, max = 50, message = "Subdomain must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Subdomain can only contain lowercase letters, numbers, and hyphens")
    private String subdomain;
    
    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;
    
    private String description;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;
    
    private String contactPhone;
    
    private String companyName;
    
    private String companyAddress;
    
    private Tenant.SubscriptionPlan plan;
    
    private Integer maxUsers;
    
    private Integer maxCampaignsPerMonth;
    
    private Integer maxEmailsPerMonth;
    
    private Integer maxSmsPerMonth;
    
    private Integer maxWhatsappPerMonth;
    
    private Long storageLimitMb;
    
    private String timezone;
    
    private String locale;
}