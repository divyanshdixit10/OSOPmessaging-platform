package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.config.TenantContext;
import in.osop.messaging_platform.dto.CreateTenantRequest;
import in.osop.messaging_platform.dto.TenantDto;
import in.osop.messaging_platform.dto.UpdateTenantRequest;
import in.osop.messaging_platform.model.Tenant;
import in.osop.messaging_platform.model.User;
import in.osop.messaging_platform.repository.TenantRepository;
import in.osop.messaging_platform.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    
    @Override
    public TenantDto createTenant(CreateTenantRequest request, User createdBy) {
        log.info("Creating new tenant: {}", request.getName());
        
        // Check if tenant name or subdomain already exists
        if (tenantRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tenant name already exists");
        }
        
        if (tenantRepository.existsBySubdomain(request.getSubdomain())) {
            throw new IllegalArgumentException("Tenant subdomain already exists");
        }
        
        // Create new tenant
        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .subdomain(request.getSubdomain())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .companyName(request.getCompanyName())
                .companyAddress(request.getCompanyAddress())
                .status(Tenant.TenantStatus.ACTIVE)
                .plan(request.getPlan() != null ? request.getPlan() : Tenant.SubscriptionPlan.FREE)
                .planStartDate(LocalDateTime.now())
                .maxUsers(request.getMaxUsers() != null ? request.getMaxUsers() : 5)
                .maxCampaignsPerMonth(request.getMaxCampaignsPerMonth() != null ? request.getMaxCampaignsPerMonth() : 100)
                .maxEmailsPerMonth(request.getMaxEmailsPerMonth() != null ? request.getMaxEmailsPerMonth() : 1000)
                .maxSmsPerMonth(request.getMaxSmsPerMonth() != null ? request.getMaxSmsPerMonth() : 100)
                .maxWhatsappPerMonth(request.getMaxWhatsappPerMonth() != null ? request.getMaxWhatsappPerMonth() : 50)
                .storageLimitMb(request.getStorageLimitMb() != null ? request.getStorageLimitMb() : 100L)
                .currentStorageMb(0L)
                .timezone(request.getTimezone() != null ? request.getTimezone() : "UTC")
                .locale(request.getLocale() != null ? request.getLocale() : "en_US")
                .build();
        
        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Created tenant: {}", savedTenant.getId());
        
        return convertToDto(savedTenant);
    }
    
    @Override
    public TenantDto updateTenant(Long tenantId, UpdateTenantRequest request) {
        log.info("Updating tenant: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // Update tenant fields
        if (request.getDisplayName() != null) {
            tenant.setDisplayName(request.getDisplayName());
        }
        
        if (request.getDescription() != null) {
            tenant.setDescription(request.getDescription());
        }
        
        if (request.getContactEmail() != null) {
            tenant.setContactEmail(request.getContactEmail());
        }
        
        if (request.getContactPhone() != null) {
            tenant.setContactPhone(request.getContactPhone());
        }
        
        if (request.getCompanyName() != null) {
            tenant.setCompanyName(request.getCompanyName());
        }
        
        if (request.getCompanyAddress() != null) {
            tenant.setCompanyAddress(request.getCompanyAddress());
        }
        
        if (request.getLogoUrl() != null) {
            tenant.setLogoUrl(request.getLogoUrl());
        }
        
        if (request.getPrimaryColor() != null) {
            tenant.setPrimaryColor(request.getPrimaryColor());
        }
        
        if (request.getSecondaryColor() != null) {
            tenant.setSecondaryColor(request.getSecondaryColor());
        }
        
        if (request.getTimezone() != null) {
            tenant.setTimezone(request.getTimezone());
        }
        
        if (request.getLocale() != null) {
            tenant.setLocale(request.getLocale());
        }
        
        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("Updated tenant: {}", updatedTenant.getId());
        
        return convertToDto(updatedTenant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TenantDto getTenantById(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        return convertToDto(tenant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TenantDto getTenantBySubdomain(String subdomain) {
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        return convertToDto(tenant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TenantDto> getTenantsByStatus(Tenant.TenantStatus status) {
        return tenantRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteTenant(Long tenantId) {
        log.info("Deleting tenant: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenantRepository.delete(tenant);
        log.info("Deleted tenant: {}", tenantId);
    }
    
    @Override
    public void suspendTenant(Long tenantId, String reason) {
        log.info("Suspending tenant: {} with reason: {}", tenantId, reason);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        tenantRepository.save(tenant);
        log.info("Tenant suspended: {}", tenantId);
    }
    
    @Override
    public void activateTenant(Long tenantId) {
        log.info("Activating tenant: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
        log.info("Tenant activated: {}", tenantId);
    }
    
    @Override
    public boolean checkEmailQuota(Long tenantId, int additionalEmails) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // TODO: Get actual usage from database
        int currentUsage = 0;
        
        return currentUsage + additionalEmails <= tenant.getMaxEmailsPerMonth();
    }
    
    @Override
    public boolean checkSmsQuota(Long tenantId, int additionalSms) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // TODO: Get actual usage from database
        int currentUsage = 0;
        
        return currentUsage + additionalSms <= tenant.getMaxSmsPerMonth();
    }
    
    @Override
    public boolean checkWhatsappQuota(Long tenantId, int additionalWhatsapp) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // TODO: Get actual usage from database
        int currentUsage = 0;
        
        return currentUsage + additionalWhatsapp <= tenant.getMaxWhatsappPerMonth();
    }
    
    @Override
    public boolean checkStorageQuota(Long tenantId, long additionalBytes) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        long additionalMb = additionalBytes / (1024 * 1024);
        return tenant.getCurrentStorageMb() + additionalMb <= tenant.getStorageLimitMb();
    }
    
    @Override
    public void updateUsage(Long tenantId, String resourceType, int count) {
        // TODO: Implement usage tracking
        log.info("Updating usage for tenant: {}, resource: {}, count: {}", tenantId, resourceType, count);
    }
    
    @Override
    public void updateStorageUsage(Long tenantId, long bytes) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        long additionalMb = bytes / (1024 * 1024);
        tenant.setCurrentStorageMb(tenant.getCurrentStorageMb() + additionalMb);
        tenantRepository.save(tenant);
    }
    
    @Override
    public List<Tenant> getExpiredTrials() {
        return tenantRepository.findByStatusAndTrialEndsAtBefore(
                Tenant.TenantStatus.TRIAL, 
                LocalDateTime.now());
    }
    
    @Override
    public List<Tenant> getTenantsForBilling() {
        return tenantRepository.findByNextBillingDateBefore(LocalDateTime.now());
    }
    
    @Override
    public void processTrialExpirations() {
        List<Tenant> expiredTrials = getExpiredTrials();
        
        for (Tenant tenant : expiredTrials) {
            log.info("Processing expired trial for tenant: {}", tenant.getId());
            tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
            tenantRepository.save(tenant);
        }
    }
    
    @Override
    public void processBilling() {
        List<Tenant> tenantsForBilling = getTenantsForBilling();
        
        for (Tenant tenant : tenantsForBilling) {
            log.info("Processing billing for tenant: {}", tenant.getId());
            // TODO: Implement billing logic
            tenant.setLastBillingDate(LocalDateTime.now());
            tenant.setNextBillingDate(LocalDateTime.now().plusMonths(1));
            tenantRepository.save(tenant);
        }
    }
    
    @Override
    public TenantDto upgradePlan(Long tenantId, Tenant.SubscriptionPlan newPlan) {
        log.info("Upgrading plan for tenant: {} to {}", tenantId, newPlan);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // Validate plan upgrade
        if (newPlan.ordinal() <= tenant.getPlan().ordinal()) {
            throw new IllegalArgumentException("New plan must be higher than current plan");
        }
        
        // Update plan and limits based on the new plan
        tenant.setPlan(newPlan);
        updatePlanLimits(tenant, newPlan);
        
        tenant.setPlanStartDate(LocalDateTime.now());
        tenantRepository.save(tenant);
        
        return convertToDto(tenant);
    }
    
    @Override
    public TenantDto downgradePlan(Long tenantId, Tenant.SubscriptionPlan newPlan) {
        log.info("Downgrading plan for tenant: {} to {}", tenantId, newPlan);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // Validate plan downgrade
        if (newPlan.ordinal() >= tenant.getPlan().ordinal()) {
            throw new IllegalArgumentException("New plan must be lower than current plan");
        }
        
        // Update plan and limits based on the new plan
        tenant.setPlan(newPlan);
        updatePlanLimits(tenant, newPlan);
        
        tenant.setPlanStartDate(LocalDateTime.now());
        tenantRepository.save(tenant);
        
        return convertToDto(tenant);
    }
    
    @Override
    public void setStripeCustomerId(Long tenantId, String stripeCustomerId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setStripeCustomerId(stripeCustomerId);
        tenantRepository.save(tenant);
    }
    
    @Override
    public void setStripeSubscriptionId(Long tenantId, String stripeSubscriptionId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setStripeSubscriptionId(stripeSubscriptionId);
        tenantRepository.save(tenant);
    }
    
    @Override
    public boolean isTenantActive(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        return tenant.isActive();
    }
    
    @Override
    public boolean isTenantTrialExpired(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        return tenant.isTrialExpired();
    }
    
    @Override
    public TenantDto getCurrentTenant() {
        Long currentTenantId = TenantContext.getCurrentTenant();
        
        if (currentTenantId == null) {
            // Try to get tenant ID from authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                currentTenantId = user.getTenantId();
            }
        }
        
        if (currentTenantId == null) {
            throw new IllegalStateException("No current tenant found");
        }
        
        return getTenantById(currentTenantId);
    }
    
    @Override
    public void setCurrentTenant(Long tenantId) {
        TenantContext.setCurrentTenant(tenantId);
    }
    
    private void updatePlanLimits(Tenant tenant, Tenant.SubscriptionPlan plan) {
        switch (plan) {
            case FREE:
                tenant.setMaxUsers(5);
                tenant.setMaxCampaignsPerMonth(100);
                tenant.setMaxEmailsPerMonth(1000);
                tenant.setMaxSmsPerMonth(100);
                tenant.setMaxWhatsappPerMonth(50);
                tenant.setStorageLimitMb(100L);
                break;
            case STARTER:
                tenant.setMaxUsers(10);
                tenant.setMaxCampaignsPerMonth(500);
                tenant.setMaxEmailsPerMonth(10000);
                tenant.setMaxSmsPerMonth(1000);
                tenant.setMaxWhatsappPerMonth(500);
                tenant.setStorageLimitMb(500L);
                break;
            case PROFESSIONAL:
                tenant.setMaxUsers(25);
                tenant.setMaxCampaignsPerMonth(1000);
                tenant.setMaxEmailsPerMonth(50000);
                tenant.setMaxSmsPerMonth(5000);
                tenant.setMaxWhatsappPerMonth(2500);
                tenant.setStorageLimitMb(1024L);
                break;
            case ENTERPRISE:
                tenant.setMaxUsers(100);
                tenant.setMaxCampaignsPerMonth(5000);
                tenant.setMaxEmailsPerMonth(1000000);
                tenant.setMaxSmsPerMonth(50000);
                tenant.setMaxWhatsappPerMonth(25000);
                tenant.setStorageLimitMb(5120L);
                break;
        }
    }
    
    private TenantDto convertToDto(Tenant tenant) {
        return TenantDto.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .subdomain(tenant.getSubdomain())
                .displayName(tenant.getDisplayName())
                .description(tenant.getDescription())
                .contactEmail(tenant.getContactEmail())
                .contactPhone(tenant.getContactPhone())
                .companyName(tenant.getCompanyName())
                .companyAddress(tenant.getCompanyAddress())
                .status(tenant.getStatus())
                .plan(tenant.getPlan())
                .planStartDate(tenant.getPlanStartDate())
                .planEndDate(tenant.getPlanEndDate())
                .maxUsers(tenant.getMaxUsers())
                .maxCampaignsPerMonth(tenant.getMaxCampaignsPerMonth())
                .maxEmailsPerMonth(tenant.getMaxEmailsPerMonth())
                .maxSmsPerMonth(tenant.getMaxSmsPerMonth())
                .maxWhatsappPerMonth(tenant.getMaxWhatsappPerMonth())
                .storageLimitMb(tenant.getStorageLimitMb())
                .currentStorageMb(tenant.getCurrentStorageMb())
                .logoUrl(tenant.getLogoUrl())
                .primaryColor(tenant.getPrimaryColor())
                .secondaryColor(tenant.getSecondaryColor())
                .timezone(tenant.getTimezone())
                .locale(tenant.getLocale())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }
}