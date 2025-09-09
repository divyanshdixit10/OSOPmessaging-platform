package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.User;
import org.springframework.security.core.Authentication;

/**
 * Service for security-related operations and authorization checks
 */
public interface SecurityService {
    
    /**
     * Check if the authenticated user is a tenant admin
     * @param authentication The authentication object
     * @return true if the user is a tenant admin, false otherwise
     */
    boolean isTenantAdmin(Authentication authentication);
    
    /**
     * Check if the authenticated user belongs to the specified tenant
     * @param authentication The authentication object
     * @param tenantId The tenant ID to check
     * @return true if the user belongs to the tenant, false otherwise
     */
    boolean belongsToTenant(Authentication authentication, Long tenantId);
    
    /**
     * Check if the authenticated user has access to the specified resource
     * @param authentication The authentication object
     * @param resourceType The type of resource
     * @param resourceId The ID of the resource
     * @return true if the user has access to the resource, false otherwise
     */
    boolean hasResourceAccess(Authentication authentication, String resourceType, Long resourceId);
    
    /**
     * Get the current authenticated user
     * @return The authenticated user or null if not authenticated
     */
    User getCurrentUser();
}
