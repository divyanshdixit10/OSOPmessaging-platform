package in.osop.messaging_platform.filter;

import in.osop.messaging_platform.config.TenantContext;
import in.osop.messaging_platform.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to set the current tenant for each request
 */
@Component
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Clear any existing tenant context
            TenantContext.clear();
            
            // Check for X-Tenant-ID header first
            String tenantIdHeader = request.getHeader("X-Tenant-ID");
            if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
                try {
                    Long tenantId = Long.parseLong(tenantIdHeader);
                    TenantContext.setCurrentTenant(tenantId);
                    log.debug("Set tenant ID from header: {}", tenantId);
                } catch (NumberFormatException e) {
                    log.warn("Invalid tenant ID in header: {}", tenantIdHeader);
                }
            } else {
                // Try to get tenant ID from authenticated user
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
                    User user = (User) authentication.getPrincipal();
                    if (user.getTenantId() != null) {
                        TenantContext.setCurrentTenant(user.getTenantId());
                        log.debug("Set tenant ID from user: {}", user.getTenantId());
                    }
                }
            }
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Always clear the tenant context after the request is processed
            TenantContext.clear();
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip tenant filter for public endpoints
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/public/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/actuator/");
    }
}
