package in.osop.messaging_platform.filter;

import in.osop.messaging_platform.config.TenantContext;
import in.osop.messaging_platform.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Order(2) // Run after TenantFilter but before Spring Security filters
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String apiKey = request.getHeader(API_KEY_HEADER);
        
        if (apiKey != null && !apiKey.isEmpty()) {
            log.debug("Found API key in request header");
            
            Optional<Long> tenantIdOpt = apiKeyService.validateApiKey(apiKey);
            
            if (tenantIdOpt.isPresent()) {
                Long tenantId = tenantIdOpt.get();
                log.debug("API key is valid for tenant ID: {}", tenantId);
                
                // Set tenant context
                TenantContext.setCurrentTenant(tenantId);
                
                // Set X-Tenant-ID header for downstream services
                response.setHeader("X-Tenant-ID", tenantId.toString());
                
                // Set authentication in Spring Security context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        "api-key-user", null, List.of(new SimpleGrantedAuthority("ROLE_API")));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Update last used timestamp asynchronously
                apiKeyService.updateApiKeyLastUsed(apiKey);
            } else {
                log.warn("Invalid API key provided: {}", apiKey);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
