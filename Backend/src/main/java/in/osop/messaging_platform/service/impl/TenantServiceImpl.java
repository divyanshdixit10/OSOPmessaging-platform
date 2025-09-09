package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.dto.CreateTenantRequest;
import in.osop.messaging_platform.dto.TenantDto;
import in.osop.messaging_platform.dto.UpdateTenantRequest;
import in.osop.messaging_platform.model.Tenant;
import in.osop.messaging_platform.model.User;
import in.osop.messaging_platform.repository.TenantRepository;
import in.osop.messaging_platform.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            throw new IllegalArgumentException("Subdomain already exists");
        }

        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .subdomain(request.getSubdomain())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .companyName(request.getCompanyName())
                .companyAddress(request.getCompanyAddress())
                .status(Tenant.TenantStatus.TRIAL)
                .plan(Tenant.SubscriptionPlan.FREE)
                .trialEndsAt(LocalDateTime.now().plusDays(14)) // 14-day trial
                .timezone(request.getTimezone() != null ? request.getTimezone() : "UTC")
                .locale(request.getLocale() != null ? request.getLocale() : "en_US")
                .primaryColor(request.getPrimaryColor())
                .secondaryColor(request.getSecondaryColor())
                .build();

        tenant = tenantRepository.save(tenant);
        log.info("Created tenant with ID: {}", tenant.getId());
        
        return convertToDto(tenant);
    }

    @Override
    public TenantDto updateTenant(Long tenantId, UpdateTenantRequest request) {
        log.info("Updating tenant: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

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
        if (request.getTimezone() != null) {
            tenant.setTimezone(request.getTimezone());
        }
        if (request.getLocale() != null) {
            tenant.setLocale(request.getLocale());
        }
        if (request.getPrimaryColor() != null) {
            tenant.setPrimaryColor(request.getPrimaryColor());
        }
        if (request.getSecondaryColor() != null) {
            tenant.setSecondaryColor(request.getSecondaryColor());
        }
        if (request.getLogoUrl() != null) {
            tenant.setLogoUrl(request.getLogoUrl());
        }
        if (request.getSettings() != null) {
            tenant.setSettings(request.getSettings());
        }

        tenant = tenantRepository.save(tenant);
        log.info("Updated tenant: {}", tenantId);
        
        return convertToDto(tenant);
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
        log.info("Suspended tenant: {}", tenantId);
    }

    @Override
    public void activateTenant(Long tenantId) {
        log.info("Activating tenant: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
        log.info("Activated tenant: {}", tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailQuota(Long tenantId, int additionalEmails) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        Long currentMonthEmails = tenantRepository.countEmailsThisMonth(tenantId, LocalDateTime.now());
        return !tenant.hasReachedEmailLimit((int) (currentMonthEmails + additionalEmails));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkSmsQuota(Long tenantId, int additionalSms) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // TODO: Implement SMS counting
        return true; // Placeholder
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkWhatsappQuota(Long tenantId, int additionalWhatsapp) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // TODO: Implement WhatsApp counting
        return true; // Placeholder
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkStorageQuota(Long tenantId, long additionalBytes) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        return !tenant.hasReachedStorageLimit(additionalBytes);
    }

    @Override
    public void updateUsage(Long tenantId, String resourceType, int count) {
        log.info("Updating usage for tenant {}: {} {}", tenantId, count, resourceType);
        
        // TODO: Implement usage tracking
        // This would update the usage_tracking table
    }

    @Override
    public void updateStorageUsage(Long tenantId, long bytes) {
        log.info("Updating storage usage for tenant {}: {} bytes", tenantId, bytes);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setCurrentStorageMb(tenant.getCurrentStorageMb() + (bytes / (1024 * 1024)));
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getExpiredTrials() {
        return tenantRepository.findExpiredTrials(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getTenantsForBilling() {
        return tenantRepository.findTenantsForBilling(LocalDateTime.now());
    }

    @Override
    public void processTrialExpirations() {
        log.info("Processing trial expirations");
        
        List<Tenant> expiredTrials = getExpiredTrials();
        for (Tenant tenant : expiredTrials) {
            if (tenant.getStatus() == Tenant.TenantStatus.TRIAL) {
                tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
                tenantRepository.save(tenant);
                log.info("Suspended expired trial tenant: {}", tenant.getId());
            }
        }
    }

    @Override
    public void processBilling() {
        log.info("Processing billing");
        
        List<Tenant> tenantsForBilling = getTenantsForBilling();
        for (Tenant tenant : tenantsForBilling) {
            // TODO: Implement billing processing
            log.info("Processing billing for tenant: {}", tenant.getId());
        }
    }

    @Override
    public TenantDto upgradePlan(Long tenantId, Tenant.SubscriptionPlan newPlan) {
        log.info("Upgrading tenant {} to plan: {}", tenantId, newPlan);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setPlan(newPlan);
        tenant.setPlanStartDate(LocalDateTime.now());
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        
        // Update quotas based on plan
        updateQuotasForPlan(tenant, newPlan);
        
        tenant = tenantRepository.save(tenant);
        log.info("Upgraded tenant {} to plan: {}", tenantId, newPlan);
        
        return convertToDto(tenant);
    }

    @Override
    public TenantDto downgradePlan(Long tenantId, Tenant.SubscriptionPlan newPlan) {
        log.info("Downgrading tenant {} to plan: {}", tenantId, newPlan);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        tenant.setPlan(newPlan);
        tenant.setPlanStartDate(LocalDateTime.now());
        
        // Update quotas based on plan
        updateQuotasForPlan(tenant, newPlan);
        
        tenant = tenantRepository.save(tenant);
        log.info("Downgraded tenant {} to plan: {}", tenantId, newPlan);
        
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
    @Transactional(readOnly = true)
    public boolean isTenantActive(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        return tenant.isActive();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTenantTrialExpired(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        return tenant.isTrialExpired();
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDto getCurrentTenant() {
        // TODO: Get current tenant from security context
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setCurrentTenant(Long tenantId) {
        // TODO: Set current tenant in security context
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void updateQuotasForPlan(Tenant tenant, Tenant.SubscriptionPlan plan) {
        switch (plan) {
            case FREE:
                tenant.setMaxUsers(5);
                tenant.setMaxCampaignsPerMonth(10);
                tenant.setMaxEmailsPerMonth(1000);
                tenant.setMaxSmsPerMonth(50);
                tenant.setMaxWhatsappPerMonth(25);
                tenant.setStorageLimitMb(100L);
                break;
            case STARTER:
                tenant.setMaxUsers(10);
                tenant.setMaxCampaignsPerMonth(50);
                tenant.setMaxEmailsPerMonth(10000);
                tenant.setMaxSmsPerMonth(500);
                tenant.setMaxWhatsappPerMonth(100);
                tenant.setStorageLimitMb(1000L);
                break;
            case PROFESSIONAL:
                tenant.setMaxUsers(25);
                tenant.setMaxCampaignsPerMonth(200);
                tenant.setMaxEmailsPerMonth(50000);
                tenant.setMaxSmsPerMonth(2000);
                tenant.setMaxWhatsappPerMonth(500);
                tenant.setStorageLimitMb(5000L);
                break;
            case ENTERPRISE:
                tenant.setMaxUsers(100);
                tenant.setMaxCampaignsPerMonth(1000);
                tenant.setMaxEmailsPerMonth(200000);
                tenant.setMaxSmsPerMonth(10000);
                tenant.setMaxWhatsappPerMonth(2000);
                tenant.setStorageLimitMb(50000L);
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
                .stripeCustomerId(tenant.getStripeCustomerId())
                .stripeSubscriptionId(tenant.getStripeSubscriptionId())
                .billingEmail(tenant.getBillingEmail())
                .settings(tenant.getSettings())
                .logoUrl(tenant.getLogoUrl())
                .primaryColor(tenant.getPrimaryColor())
                .secondaryColor(tenant.getSecondaryColor())
                .timezone(tenant.getTimezone())
                .locale(tenant.getLocale())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .trialEndsAt(tenant.getTrialEndsAt())
                .lastBillingDate(tenant.getLastBillingDate())
                .nextBillingDate(tenant.getNextBillingDate())
                .isTrialExpired(tenant.isTrialExpired())
                .isActive(tenant.isActive())
                .build();
    }
}
