package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.model.UsageTracking;
import in.osop.messaging_platform.service.TenantService;
import in.osop.messaging_platform.service.UsageTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usage Tracking API", description = "APIs for tracking and retrieving tenant resource usage")
public class UsageTrackingController {

    private final UsageTrackingService usageTrackingService;
    private final TenantService tenantService;

    @GetMapping("/current-month")
    @Operation(summary = "Get current month usage", description = "Get usage for all resource types for the current month")
    @ApiResponse(responseCode = "200", description = "Usage data retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<Map<UsageTracking.ResourceType, Long>> getCurrentMonthUsage() {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting current month usage for tenant ID: {}", tenantId);
        Map<UsageTracking.ResourceType, Long> usage = usageTrackingService.getAllResourceUsageForCurrentMonth(tenantId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/resource/{resourceType}/current-month")
    @Operation(summary = "Get current month usage for specific resource", description = "Get usage for a specific resource type for the current month")
    @ApiResponse(responseCode = "200", description = "Usage data retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<Long> getCurrentMonthUsageForResource(@PathVariable UsageTracking.ResourceType resourceType) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting current month usage for tenant ID: {} and resource type: {}", tenantId, resourceType);
        Long usage = usageTrackingService.getCurrentMonthUsageForTenant(tenantId, resourceType);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/resource/{resourceType}/previous-month")
    @Operation(summary = "Get previous month usage for specific resource", description = "Get usage for a specific resource type for the previous month")
    @ApiResponse(responseCode = "200", description = "Usage data retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<Long> getPreviousMonthUsageForResource(@PathVariable UsageTracking.ResourceType resourceType) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting previous month usage for tenant ID: {} and resource type: {}", tenantId, resourceType);
        Long usage = usageTrackingService.getPreviousMonthUsageForTenant(tenantId, resourceType);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/resource/{resourceType}/range")
    @Operation(summary = "Get usage for date range", description = "Get usage for a specific resource type for a date range")
    @ApiResponse(responseCode = "200", description = "Usage data retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<Long> getUsageForDateRange(
            @PathVariable UsageTracking.ResourceType resourceType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting usage for tenant ID: {}, resource type: {}, date range: {} to {}", 
                tenantId, resourceType, startDate, endDate);
        Long usage = usageTrackingService.getUsageForTenant(tenantId, resourceType, startDate, endDate);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/resource/{resourceType}/history")
    @Operation(summary = "Get usage history", description = "Get usage history for a specific resource type")
    @ApiResponse(responseCode = "200", description = "Usage history retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<List<UsageTracking>> getUsageHistory(
            @PathVariable UsageTracking.ResourceType resourceType,
            @RequestParam(defaultValue = "6") int months) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting usage history for tenant ID: {}, resource type: {}, months: {}", 
                tenantId, resourceType, months);
        List<UsageTracking> history = usageTrackingService.getUsageHistory(tenantId, resourceType, months);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/resource/{resourceType}/daily/{year}/{month}")
    @Operation(summary = "Get daily usage for month", description = "Get daily usage for a specific resource type for a month")
    @ApiResponse(responseCode = "200", description = "Daily usage retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantAdmin(authentication)")
    public ResponseEntity<Map<Integer, Long>> getDailyUsageForMonth(
            @PathVariable UsageTracking.ResourceType resourceType,
            @PathVariable int year,
            @PathVariable int month) {
        Long tenantId = tenantService.getCurrentTenant().getId();
        log.debug("Getting daily usage for tenant ID: {}, resource type: {}, year: {}, month: {}", 
                tenantId, resourceType, year, month);
        Map<Integer, Long> dailyUsage = usageTrackingService.getDailyUsageForMonth(tenantId, resourceType, year, month);
        return ResponseEntity.ok(dailyUsage);
    }

    @GetMapping("/admin/tenant/{tenantId}")
    @Operation(summary = "Get usage for specific tenant (Admin only)", description = "Get usage for all resource types for a specific tenant")
    @ApiResponse(responseCode = "200", description = "Usage data retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<UsageTracking.ResourceType, Long>> getTenantUsage(@PathVariable Long tenantId) {
        log.debug("Admin getting current month usage for tenant ID: {}", tenantId);
        Map<UsageTracking.ResourceType, Long> usage = usageTrackingService.getAllResourceUsageForCurrentMonth(tenantId);
        return ResponseEntity.ok(usage);
    }
}
