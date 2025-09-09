package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    
    private Long id;
    private String name;
    private String subdomain;
    private String displayName;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private String companyName;
    private String companyAddress;
    private Tenant.TenantStatus status;
    private Tenant.SubscriptionPlan plan;
    private LocalDateTime planStartDate;
    private LocalDateTime planEndDate;
    private Integer maxUsers;
    private Integer maxCampaignsPerMonth;
    private Integer maxEmailsPerMonth;
    private Integer maxSmsPerMonth;
    private Integer maxWhatsappPerMonth;
    private Long storageLimitMb;
    private Long currentStorageMb;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String billingEmail;
    private String settings;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String timezone;
    private String locale;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime trialEndsAt;
    private LocalDateTime lastBillingDate;
    private LocalDateTime nextBillingDate;
    
    // Computed fields
    private Long currentUserCount;
    private Integer currentMonthCampaigns;
    private Integer currentMonthEmails;
    private Integer currentMonthSms;
    private Integer currentMonthWhatsapp;
    private Double storageUsagePercentage;
    private Boolean isTrialExpired;
    private Boolean isActive;
    
    // Usage statistics
    private UsageStats usageStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageStats {
        private Integer emailsUsed;
        private Integer emailsRemaining;
        private Integer smsUsed;
        private Integer smsRemaining;
        private Integer whatsappUsed;
        private Integer whatsappRemaining;
        private Integer campaignsUsed;
        private Integer campaignsRemaining;
        private Long storageUsed;
        private Long storageRemaining;
        private Double emailUsagePercentage;
        private Double smsUsagePercentage;
        private Double whatsappUsagePercentage;
        private Double campaignUsagePercentage;
        private Double storageUsagePercentage;
    }
}
