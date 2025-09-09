package in.osop.messaging_platform.config;

/**
 * ThreadLocal storage for the current tenant ID
 */
public class TenantContext {
    
    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();
    
    public static void setCurrentTenant(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    public static Long getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
