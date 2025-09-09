package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.ApiResponse;
import in.osop.messaging_platform.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Billing", description = "Billing and subscription management")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/plans")
    @Operation(summary = "Get available subscription plans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAvailablePlans() {
        try {
            List<Map<String, Object>> plans = billingService.getAvailablePlans();
            return ResponseEntity.ok(ApiResponse.success(plans));
        } catch (Exception e) {
            log.error("Failed to get available plans", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get available plans"));
        }
    }

    @PostMapping("/checkout")
    @Operation(summary = "Create checkout session for plan upgrade")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> createCheckoutSession(
            @RequestParam String planId) {
        try {
            // TODO: Get current tenant from security context
            Long tenantId = 1L; // Placeholder
            
            String sessionId = billingService.createCheckoutSession(tenantId, planId);
            Map<String, String> response = Map.of(
                "sessionId", sessionId,
                "url", "https://checkout.stripe.com/pay/" + sessionId
            );
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Failed to create checkout session", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create checkout session"));
        }
    }

    @PostMapping("/portal")
    @Operation(summary = "Create customer portal session")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> createCustomerPortalSession() {
        try {
            // TODO: Get current tenant from security context
            Long tenantId = 1L; // Placeholder
            
            String portalUrl = billingService.createCustomerPortalSession(tenantId);
            Map<String, String> response = Map.of("url", portalUrl);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Failed to create customer portal session", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create customer portal session"));
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Get billing history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBillingHistory() {
        try {
            // TODO: Get current tenant from security context
            Long tenantId = 1L; // Placeholder
            
            List<Map<String, Object>> history = billingService.getBillingHistory(tenantId);
            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (Exception e) {
            log.error("Failed to get billing history", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get billing history"));
        }
    }

    @GetMapping("/subscription/status")
    @Operation(summary = "Get subscription status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSubscriptionStatus() {
        try {
            // TODO: Get current tenant from security context
            Long tenantId = 1L; // Placeholder
            
            String status = billingService.getSubscriptionStatus(tenantId);
            Map<String, String> response = Map.of("status", status);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Failed to get subscription status", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get subscription status"));
        }
    }

    @GetMapping("/upcoming-invoice")
    @Operation(summary = "Get upcoming invoice")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUpcomingInvoice() {
        try {
            // TODO: Get current tenant from security context
            Long tenantId = 1L; // Placeholder
            
            Map<String, Object> invoice = billingService.getUpcomingInvoice(tenantId);
            return ResponseEntity.ok(ApiResponse.success(invoice));
        } catch (Exception e) {
            log.error("Failed to get upcoming invoice", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get upcoming invoice"));
        }
    }

    @PostMapping("/cancel-subscription")
    @Operation(summary = "Cancel subscription")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> cancelSubscription() {
        try {
            // TODO: Get current tenant from security context
            Long tenantId = 1L; // Placeholder
            
            billingService.cancelSubscription(tenantId);
            return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully"));
        } catch (Exception e) {
            log.error("Failed to cancel subscription", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to cancel subscription"));
        }
    }

    @PostMapping("/reactivate-subscription")
    @Operation(summary = "Reactivate subscription")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> reactivateSubscription() {
        try {
            // TODO: Get current tenant from security context
            Long tenantId = 1L; // Placeholder
            
            billingService.reactivateSubscription(tenantId);
            return ResponseEntity.ok(ApiResponse.success("Subscription reactivated successfully"));
        } catch (Exception e) {
            log.error("Failed to reactivate subscription", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to reactivate subscription"));
        }
    }

    @PostMapping("/webhook")
    @Operation(summary = "Handle Stripe webhooks")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        try {
            // TODO: Implement webhook signature verification
            // TODO: Parse webhook event and call appropriate billing service methods
            
            log.info("Received Stripe webhook: {}", payload);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Failed to process webhook", e);
            return ResponseEntity.badRequest().body("Webhook processing failed");
        }
    }
}
