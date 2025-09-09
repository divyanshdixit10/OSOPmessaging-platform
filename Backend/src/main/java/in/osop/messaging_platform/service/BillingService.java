package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.Tenant;

import java.util.List;
import java.util.Map;

public interface BillingService {
    
    /**
     * Create a Stripe customer for a tenant
     */
    String createCustomer(Tenant tenant);
    
    /**
     * Create a checkout session for plan upgrade
     */
    String createCheckoutSession(Long tenantId, String planId);
    
    /**
     * Create a customer portal session
     */
    String createCustomerPortalSession(Long tenantId);
    
    /**
     * Handle successful payment
     */
    void handleSuccessfulPayment(String sessionId);
    
    /**
     * Handle failed payment
     */
    void handleFailedPayment(String sessionId);
    
    /**
     * Handle subscription created
     */
    void handleSubscriptionCreated(String subscriptionId);
    
    /**
     * Handle subscription updated
     */
    void handleSubscriptionUpdated(String subscriptionId);
    
    /**
     * Handle subscription deleted
     */
    void handleSubscriptionDeleted(String subscriptionId);
    
    /**
     * Handle invoice payment succeeded
     */
    void handleInvoicePaymentSucceeded(String invoiceId);
    
    /**
     * Handle invoice payment failed
     */
    void handleInvoicePaymentFailed(String invoiceId);
    
    /**
     * Get billing history for a tenant
     */
    List<Map<String, Object>> getBillingHistory(Long tenantId);
    
    /**
     * Cancel subscription
     */
    void cancelSubscription(Long tenantId);
    
    /**
     * Reactivate subscription
     */
    void reactivateSubscription(Long tenantId);
    
    /**
     * Get subscription status
     */
    String getSubscriptionStatus(Long tenantId);
    
    /**
     * Get upcoming invoice
     */
    Map<String, Object> getUpcomingInvoice(Long tenantId);
    
    /**
     * Update payment method
     */
    void updatePaymentMethod(Long tenantId, String paymentMethodId);
    
    /**
     * Get available plans
     */
    List<Map<String, Object>> getAvailablePlans();
}
