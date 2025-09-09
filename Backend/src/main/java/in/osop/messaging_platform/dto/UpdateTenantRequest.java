package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing tenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTenantRequest {
    
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;
    
    private String description;
    
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
    
    private String logoUrl;
    
    private String primaryColor;
    
    private String secondaryColor;
    
    private String timezone;
    
    private String locale;
}