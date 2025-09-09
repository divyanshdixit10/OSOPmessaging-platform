package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.UsageTracking;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface UsageTrackingService {
    
    /**
     * Track usage of a resource for a tenant
     * @param tenantId The tenant ID
     * @param userId The user ID (can be null for system operations)
     * @param resourceType The type of resource
     * @param count The usage count to add
     * @return The updated UsageTracking entity
     */
    UsageTracking trackUsage(Long tenantId, Long userId, UsageTracking.ResourceType resourceType, Long count);
    
    /**
     * Get usage for a tenant by resource type and date range
     * @param tenantId The tenant ID
     * @param resourceType The type of resource
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return The total usage count
     */
    Long getUsageForTenant(Long tenantId, UsageTracking.ResourceType resourceType, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get usage for a tenant by resource type for the current month
     * @param tenantId The tenant ID
     * @param resourceType The type of resource
     * @return The total usage count for the current month
     */
    Long getCurrentMonthUsageForTenant(Long tenantId, UsageTracking.ResourceType resourceType);
    
    /**
     * Get usage for a tenant by resource type for the previous month
     * @param tenantId The tenant ID
     * @param resourceType The type of resource
     * @return The total usage count for the previous month
     */
    Long getPreviousMonthUsageForTenant(Long tenantId, UsageTracking.ResourceType resourceType);
    
    /**
     * Get usage for all resource types for a tenant in the current month
     * @param tenantId The tenant ID
     * @return Map of resource type to usage count
     */
    Map<UsageTracking.ResourceType, Long> getAllResourceUsageForCurrentMonth(Long tenantId);
    
    /**
     * Get usage history for a tenant by resource type
     * @param tenantId The tenant ID
     * @param resourceType The type of resource
     * @param months Number of months to look back
     * @return List of usage tracking entries
     */
    List<UsageTracking> getUsageHistory(Long tenantId, UsageTracking.ResourceType resourceType, int months);
    
    /**
     * Get daily usage for a tenant by resource type for a specific month
     * @param tenantId The tenant ID
     * @param resourceType The type of resource
     * @param year The year
     * @param month The month (1-12)
     * @return Map of day of month to usage count
     */
    Map<Integer, Long> getDailyUsageForMonth(Long tenantId, UsageTracking.ResourceType resourceType, int year, int month);
}
