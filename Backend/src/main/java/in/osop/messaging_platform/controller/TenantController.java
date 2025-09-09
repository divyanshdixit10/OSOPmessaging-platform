package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.CreateTenantRequest;
import in.osop.messaging_platform.dto.TenantDto;
import in.osop.messaging_platform.dto.UpdateTenantRequest;
import in.osop.messaging_platform.model.Tenant;
import in.osop.messaging_platform.model.User;
import in.osop.messaging_platform.service.TenantService;
import in.osop.messaging_platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for tenant management
 */
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenant API", description = "APIs for tenant management")
public class TenantController {

    private final TenantService tenantService;
    private final UserService userService;

    @GetMapping("/current")
    @Operation(summary = "Get current tenant", description = "Get the current tenant for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Current tenant retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Current tenant not found")
    public ResponseEntity<TenantDto> getCurrentTenant() {
        TenantDto tenant = tenantService.getCurrentTenant();
        return ResponseEntity.ok(tenant);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID", description = "Get a tenant by its ID")
    @ApiResponse(responseCode = "200", description = "Tenant retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantDto> getTenantById(@PathVariable Long id) {
        TenantDto tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }

    @GetMapping("/subdomain/{subdomain}")
    @Operation(summary = "Get tenant by subdomain", description = "Get a tenant by its subdomain")
    @ApiResponse(responseCode = "200", description = "Tenant retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    public ResponseEntity<TenantDto> getTenantBySubdomain(@PathVariable String subdomain) {
        TenantDto tenant = tenantService.getTenantBySubdomain(subdomain);
        return ResponseEntity.ok(tenant);
    }

    @GetMapping
    @Operation(summary = "Get all tenants", description = "Get all tenants (admin only)")
    @ApiResponse(responseCode = "200", description = "Tenants retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TenantDto>> getAllTenants(Pageable pageable) {
        List<TenantDto> tenants = tenantService.getAllTenants();
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), tenants.size());
        
        Page<TenantDto> page = new PageImpl<>(
            tenants.subList(start, end), 
            pageable, 
            tenants.size()
        );
        
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @Operation(summary = "Create tenant", description = "Create a new tenant")
    @ApiResponse(responseCode = "201", description = "Tenant created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantDto> createTenant(
            @Valid @RequestBody CreateTenantRequest request,
            Authentication authentication) {
        
        User user = userService.getCurrentUser(authentication.getName());
        TenantDto tenant = tenantService.createTenant(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant", description = "Update an existing tenant")
    @ApiResponse(responseCode = "200", description = "Tenant updated successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantDto> updateTenant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest request) {
        
        TenantDto tenant = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(tenant);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant", description = "Delete a tenant")
    @ApiResponse(responseCode = "204", description = "Tenant deleted successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend tenant", description = "Suspend a tenant")
    @ApiResponse(responseCode = "200", description = "Tenant suspended successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> suspendTenant(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String reason = request.getOrDefault("reason", "Administrative action");
        tenantService.suspendTenant(id, reason);
        
        return ResponseEntity.ok(Map.of("message", "Tenant suspended successfully"));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate tenant", description = "Activate a suspended tenant")
    @ApiResponse(responseCode = "200", description = "Tenant activated successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> activateTenant(@PathVariable Long id) {
        tenantService.activateTenant(id);
        return ResponseEntity.ok(Map.of("message", "Tenant activated successfully"));
    }

    @PostMapping("/quota/check")
    @Operation(summary = "Check quota", description = "Check if a tenant has enough quota for a resource")
    @ApiResponse(responseCode = "200", description = "Quota check completed")
    public ResponseEntity<Map<String, Object>> checkQuota(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.parseLong(request.get("tenantId").toString());
        String resourceType = (String) request.get("resourceType");
        Integer count = Integer.parseInt(request.get("count").toString());
        
        boolean allowed = false;
        String reason = "Unknown resource type";
        
        switch (resourceType) {
            case "email":
                allowed = tenantService.checkEmailQuota(tenantId, count);
                reason = allowed ? null : "Email quota exceeded";
                break;
            case "sms":
                allowed = tenantService.checkSmsQuota(tenantId, count);
                reason = allowed ? null : "SMS quota exceeded";
                break;
            case "whatsapp":
                allowed = tenantService.checkWhatsappQuota(tenantId, count);
                reason = allowed ? null : "WhatsApp quota exceeded";
                break;
            case "storage":
                allowed = tenantService.checkStorageQuota(tenantId, Long.parseLong(count.toString()));
                reason = allowed ? null : "Storage quota exceeded";
                break;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("allowed", allowed);
        if (!allowed) {
            response.put("reason", reason);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usage-stats")
    @Operation(summary = "Get usage statistics", description = "Get usage statistics for the current tenant")
    @ApiResponse(responseCode = "200", description = "Usage statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getUsageStats() {
        TenantDto tenant = tenantService.getCurrentTenant();
        
        // Calculate usage percentages
        int emailsUsed = 1000; // TODO: Get actual usage from database
        int smsUsed = 100; // TODO: Get actual usage from database
        int whatsappUsed = 50; // TODO: Get actual usage from database
        int campaignsUsed = 10; // TODO: Get actual usage from database
        long storageUsed = tenant.getCurrentStorageMb();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("emailsUsed", emailsUsed);
        stats.put("emailsRemaining", tenant.getMaxEmailsPerMonth() - emailsUsed);
        stats.put("smsUsed", smsUsed);
        stats.put("smsRemaining", tenant.getMaxSmsPerMonth() - smsUsed);
        stats.put("whatsappUsed", whatsappUsed);
        stats.put("whatsappRemaining", tenant.getMaxWhatsappPerMonth() - whatsappUsed);
        stats.put("campaignsUsed", campaignsUsed);
        stats.put("campaignsRemaining", tenant.getMaxCampaignsPerMonth() - campaignsUsed);
        stats.put("storageUsed", storageUsed);
        stats.put("storageRemaining", tenant.getStorageLimitMb() - storageUsed);
        stats.put("emailUsagePercentage", (double) emailsUsed / tenant.getMaxEmailsPerMonth() * 100);
        stats.put("smsUsagePercentage", (double) smsUsed / tenant.getMaxSmsPerMonth() * 100);
        stats.put("whatsappUsagePercentage", (double) whatsappUsed / tenant.getMaxWhatsappPerMonth() * 100);
        stats.put("campaignUsagePercentage", (double) campaignsUsed / tenant.getMaxCampaignsPerMonth() * 100);
        stats.put("storageUsagePercentage", (double) storageUsed / tenant.getStorageLimitMb() * 100);
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/upgrade-plan")
    @Operation(summary = "Upgrade plan", description = "Upgrade a tenant's subscription plan")
    @ApiResponse(responseCode = "200", description = "Plan upgraded successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    public ResponseEntity<TenantDto> upgradePlan(@RequestBody Map<String, String> request) {
        TenantDto currentTenant = tenantService.getCurrentTenant();
        Tenant.SubscriptionPlan newPlan = Tenant.SubscriptionPlan.valueOf(request.get("plan"));
        
        TenantDto tenant = tenantService.upgradePlan(currentTenant.getId(), newPlan);
        return ResponseEntity.ok(tenant);
    }

    @PostMapping("/downgrade-plan")
    @Operation(summary = "Downgrade plan", description = "Downgrade a tenant's subscription plan")
    @ApiResponse(responseCode = "200", description = "Plan downgraded successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    public ResponseEntity<TenantDto> downgradePlan(@RequestBody Map<String, String> request) {
        TenantDto currentTenant = tenantService.getCurrentTenant();
        Tenant.SubscriptionPlan newPlan = Tenant.SubscriptionPlan.valueOf(request.get("plan"));
        
        TenantDto tenant = tenantService.downgradePlan(currentTenant.getId(), newPlan);
        return ResponseEntity.ok(tenant);
    }

    @PostMapping("/set-current")
    @Operation(summary = "Set current tenant", description = "Set the current tenant for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Current tenant set successfully")
    @ApiResponse(responseCode = "404", description = "Tenant not found")
    public ResponseEntity<Map<String, String>> setCurrentTenant(@RequestBody Map<String, Long> request) {
        Long tenantId = request.get("tenantId");
        tenantService.setCurrentTenant(tenantId);
        return ResponseEntity.ok(Map.of("message", "Current tenant set successfully"));
    }
}
