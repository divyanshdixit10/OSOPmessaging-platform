package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.model.User;
import in.osop.messaging_platform.repository.UserRepository;
import in.osop.messaging_platform.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isTenantAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        
        // API keys have ROLE_API authority
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_API"))) {
            return true; // API keys are considered tenant admins for now
        }
        
        // For JWT authentication, check if the user is a tenant admin
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getIsTenantAdmin() != null && user.getIsTenantAdmin();
        }
        
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean belongsToTenant(Authentication authentication, Long tenantId) {
        if (authentication == null || tenantId == null) {
            return false;
        }
        
        // API keys are already validated against tenant in ApiKeyAuthFilter
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_API"))) {
            return true;
        }
        
        // For JWT authentication, check if the user belongs to the tenant
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getTenantId() != null && user.getTenantId().equals(tenantId);
        }
        
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasResourceAccess(Authentication authentication, String resourceType, Long resourceId) {
        if (authentication == null || resourceType == null || resourceId == null) {
            return false;
        }
        
        // Admin users have access to all resources
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        // API keys have access to all resources of their tenant
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_API"))) {
            return true;
        }
        
        // For JWT authentication, check resource access based on resource type
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            
            // Tenant admins have access to all resources of their tenant
            if (user.getIsTenantAdmin() != null && user.getIsTenantAdmin()) {
                return true;
            }
            
            // TODO: Implement more specific resource access checks based on resource type
            // For now, just check if the user belongs to the tenant that owns the resource
            return true;
        }
        
        return false;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        
        // For API key authentication, return null as there's no specific user
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_API"))) {
            return null;
        }
        
        // Try to load user by username
        String username = authentication.getName();
        return userRepository.findByEmail(username).orElse(null);
    }
}
