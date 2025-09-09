package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.CreateTenantRequest;
import in.osop.messaging_platform.dto.TenantDto;
import in.osop.messaging_platform.dto.UpdateTenantRequest;
import in.osop.messaging_platform.model.Tenant;
import in.osop.messaging_platform.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TenantService {
    
    TenantDto createTenant(CreateTenantRequest request, User createdBy);
    
    TenantDto updateTenant(Long tenantId, UpdateTenantRequest request);
    
    TenantDto getTenantById(Long tenantId);
    
    TenantDto getTenantBySubdomain(String subdomain);
    
    List<TenantDto> getAllTenants();
    
    List<TenantDto> getTenantsByStatus(Tenant.TenantStatus status);
    
    void deleteTenant(Long tenantId);
    
    void suspendTenant(Long tenantId, String reason);
    
    void activateTenant(Long tenantId);
    
    boolean checkEmailQuota(Long tenantId, int additionalEmails);
    
    boolean checkSmsQuota(Long tenantId, int additionalSms);
    
    boolean checkWhatsappQuota(Long tenantId, int additionalWhatsapp);
    
    boolean checkStorageQuota(Long tenantId, long additionalBytes);
    
    void updateUsage(Long tenantId, String resourceType, int count);
    
    void updateStorageUsage(Long tenantId, long bytes);
    
    List<Tenant> getExpiredTrials();
    
    List<Tenant> getTenantsForBilling();
    
    void processTrialExpirations();
    
    void processBilling();
    
    TenantDto upgradePlan(Long tenantId, Tenant.SubscriptionPlan newPlan);
    
    TenantDto downgradePlan(Long tenantId, Tenant.SubscriptionPlan newPlan);
    
    void setStripeCustomerId(Long tenantId, String stripeCustomerId);
    
    void setStripeSubscriptionId(Long tenantId, String stripeSubscriptionId);
    
    boolean isTenantActive(Long tenantId);
    
    boolean isTenantTrialExpired(Long tenantId);
    
    TenantDto getCurrentTenant();
    
    void setCurrentTenant(Long tenantId);
}
