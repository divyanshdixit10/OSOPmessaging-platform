package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Multi-tenant entity for SaaS architecture
 */
@Entity
@Table(name = "tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String subdomain;
    
    @Column(nullable = false)
    private String displayName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TenantStatus status = TenantStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubscriptionPlan plan = SubscriptionPlan.FREE;
    
    @Column(name = "plan_start_date")
    private LocalDateTime planStartDate;
    
    @Column(name = "plan_end_date")
    private LocalDateTime planEndDate;
    
    @Column(name = "max_users")
    @Builder.Default
    private Integer maxUsers = 5;
    
    @Column(name = "max_campaigns_per_month")
    @Builder.Default
    private Integer maxCampaignsPerMonth = 100;
    
    @Column(name = "max_emails_per_month")
    @Builder.Default
    private Integer maxEmailsPerMonth = 1000;
    
    @Column(name = "max_sms_per_month")
    @Builder.Default
    private Integer maxSmsPerMonth = 100;
    
    @Column(name = "max_whatsapp_per_month")
    @Builder.Default
    private Integer maxWhatsappPerMonth = 50;
    
    @Column(name = "storage_limit_mb")
    @Builder.Default
    private Long storageLimitMb = 100L;
    
    @Column(name = "current_storage_mb")
    @Builder.Default
    private Long currentStorageMb = 0L;
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;
    
    @Column(name = "billing_email")
    private String billingEmail;
    
    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings; // JSON string with tenant-specific settings
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "primary_color")
    private String primaryColor;
    
    @Column(name = "secondary_color")
    private String secondaryColor;
    
    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "UTC";
    
    @Column(name = "locale")
    @Builder.Default
    private String locale = "en_US";
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "trial_ends_at")
    private LocalDateTime trialEndsAt;
    
    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;
    
    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;
    
    public enum TenantStatus {
        ACTIVE, SUSPENDED, CANCELLED, TRIAL
    }
    
    public enum SubscriptionPlan {
        FREE, STARTER, PROFESSIONAL, ENTERPRISE
    }
    
    // Helper methods
    public boolean isActive() {
        return status == TenantStatus.ACTIVE || status == TenantStatus.TRIAL;
    }
    
    public boolean isTrial() {
        return status == TenantStatus.TRIAL;
    }
    
    public boolean isTrialExpired() {
        return isTrial() && trialEndsAt != null && trialEndsAt.isBefore(LocalDateTime.now());
    }
    
    public boolean hasReachedEmailLimit(int currentMonthEmails) {
        return currentMonthEmails >= maxEmailsPerMonth;
    }
    
    public boolean hasReachedSmsLimit(int currentMonthSms) {
        return currentMonthSms >= maxSmsPerMonth;
    }
    
    public boolean hasReachedWhatsappLimit(int currentMonthWhatsapp) {
        return currentMonthWhatsapp >= maxWhatsappPerMonth;
    }
    
    public boolean hasReachedStorageLimit(long additionalBytes) {
        return (currentStorageMb + (additionalBytes / (1024 * 1024))) > storageLimitMb;
    }
}
