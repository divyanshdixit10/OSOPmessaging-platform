package in.osop.messaging_platform.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import in.osop.messaging_platform.model.Tenant;
import in.osop.messaging_platform.repository.TenantRepository;
import in.osop.messaging_platform.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeBillingServiceImpl implements BillingService {

    private final TenantRepository tenantRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public String createCustomer(Tenant tenant) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("email", tenant.getContactEmail());
            params.put("name", tenant.getDisplayName());
            params.put("description", "Customer for " + tenant.getDisplayName());
            
            Map<String, String> metadata = new HashMap<>();
            metadata.put("tenant_id", tenant.getId().toString());
            metadata.put("tenant_name", tenant.getName());
            params.put("metadata", metadata);

            Customer customer = Customer.create(params);
            
            // Update tenant with Stripe customer ID
            tenant.setStripeCustomerId(customer.getId());
            tenantRepository.save(tenant);
            
            log.info("Created Stripe customer {} for tenant {}", customer.getId(), tenant.getId());
            return customer.getId();
            
        } catch (StripeException e) {
            log.error("Failed to create Stripe customer for tenant {}", tenant.getId(), e);
            throw new RuntimeException("Failed to create customer", e);
        }
    }

    @Override
    public String createCheckoutSession(Long tenantId, String planId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            // Create customer if not exists
            if (tenant.getStripeCustomerId() == null) {
                createCustomer(tenant);
                tenant = tenantRepository.findById(tenantId).orElseThrow();
            }

            // Get plan details
            Map<String, Object> planDetails = getPlanDetails(planId);
            String priceId = (String) planDetails.get("priceId");

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomer(tenant.getStripeCustomerId())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .setSuccessUrl("https://yourdomain.com/billing/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("https://yourdomain.com/billing/cancel")
                    .putMetadata("tenant_id", tenantId.toString())
                    .build();

            Session session = Session.create(params);
            log.info("Created checkout session {} for tenant {}", session.getId(), tenantId);
            return session.getId();

        } catch (StripeException e) {
            log.error("Failed to create checkout session for tenant {}", tenantId, e);
            throw new RuntimeException("Failed to create checkout session", e);
        }
    }

    @Override
    public String createCustomerPortalSession(Long tenantId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            if (tenant.getStripeCustomerId() == null) {
                throw new IllegalArgumentException("No Stripe customer found for tenant");
            }

            com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams.builder()
                    .setCustomer(tenant.getStripeCustomerId())
                    .setReturnUrl("https://yourdomain.com/billing")
                    .build();

            com.stripe.model.billingportal.Session session = 
                    com.stripe.model.billingportal.Session.create(params);
            
            log.info("Created customer portal session for tenant {}", tenantId);
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Failed to create customer portal session for tenant {}", tenantId, e);
            throw new RuntimeException("Failed to create customer portal session", e);
        }
    }

    @Override
    public void handleSuccessfulPayment(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            String tenantIdStr = session.getMetadata().get("tenant_id");
            
            if (tenantIdStr == null) {
                log.error("No tenant_id in session metadata for session {}", sessionId);
                return;
            }

            Long tenantId = Long.parseLong(tenantIdStr);
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            // Update tenant status
            tenant.setStatus(Tenant.TenantStatus.ACTIVE);
            tenant.setStripeSubscriptionId(session.getSubscription());
            tenantRepository.save(tenant);

            log.info("Handled successful payment for tenant {}", tenantId);

        } catch (StripeException e) {
            log.error("Failed to handle successful payment for session {}", sessionId, e);
        }
    }

    @Override
    public void handleFailedPayment(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            String tenantIdStr = session.getMetadata().get("tenant_id");
            
            if (tenantIdStr == null) {
                log.error("No tenant_id in session metadata for session {}", sessionId);
                return;
            }

            Long tenantId = Long.parseLong(tenantIdStr);
            log.info("Handled failed payment for tenant {}", tenantId);

        } catch (StripeException e) {
            log.error("Failed to handle failed payment for session {}", sessionId, e);
        }
    }

    @Override
    public void handleSubscriptionCreated(String subscriptionId) {
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            String customerId = subscription.getCustomer();
            
            Tenant tenant = tenantRepository.findByStripeCustomerId(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            tenant.setStripeSubscriptionId(subscriptionId);
            tenant.setStatus(Tenant.TenantStatus.ACTIVE);
            tenant.setPlanStartDate(LocalDateTime.now());
            tenantRepository.save(tenant);

            log.info("Handled subscription created for tenant {}", tenant.getId());

        } catch (StripeException e) {
            log.error("Failed to handle subscription created for subscription {}", subscriptionId, e);
        }
    }

    @Override
    public void handleSubscriptionUpdated(String subscriptionId) {
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            String customerId = subscription.getCustomer();
            
            Tenant tenant = tenantRepository.findByStripeCustomerId(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            // Update plan based on subscription
            String priceId = subscription.getItems().getData().get(0).getPrice().getId();
            Tenant.SubscriptionPlan newPlan = getPlanFromPriceId(priceId);
            
            tenant.setPlan(newPlan);
            tenantRepository.save(tenant);

            log.info("Handled subscription updated for tenant {}", tenant.getId());

        } catch (StripeException e) {
            log.error("Failed to handle subscription updated for subscription {}", subscriptionId, e);
        }
    }

    @Override
    public void handleSubscriptionDeleted(String subscriptionId) {
        try {
            Tenant tenant = tenantRepository.findByStripeSubscriptionId(subscriptionId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            tenant.setStripeSubscriptionId(null);
            tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
            tenant.setPlan(Tenant.SubscriptionPlan.FREE);
            tenantRepository.save(tenant);

            log.info("Handled subscription deleted for tenant {}", tenant.getId());

        } catch (Exception e) {
            log.error("Failed to handle subscription deleted for subscription {}", subscriptionId, e);
        }
    }

    @Override
    public void handleInvoicePaymentSucceeded(String invoiceId) {
        try {
            Invoice invoice = Invoice.retrieve(invoiceId);
            String customerId = invoice.getCustomer();
            
            Tenant tenant = tenantRepository.findByStripeCustomerId(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            tenant.setLastBillingDate(LocalDateTime.now());
            tenant.setNextBillingDate(LocalDateTime.now().plusMonths(1));
            tenantRepository.save(tenant);

            log.info("Handled invoice payment succeeded for tenant {}", tenant.getId());

        } catch (StripeException e) {
            log.error("Failed to handle invoice payment succeeded for invoice {}", invoiceId, e);
        }
    }

    @Override
    public void handleInvoicePaymentFailed(String invoiceId) {
        try {
            Invoice invoice = Invoice.retrieve(invoiceId);
            String customerId = invoice.getCustomer();
            
            Tenant tenant = tenantRepository.findByStripeCustomerId(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            log.info("Handled invoice payment failed for tenant {}", tenant.getId());

        } catch (StripeException e) {
            log.error("Failed to handle invoice payment failed for invoice {}", invoiceId, e);
        }
    }

    @Override
    public List<Map<String, Object>> getBillingHistory(Long tenantId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            if (tenant.getStripeCustomerId() == null) {
                return new ArrayList<>();
            }

            Map<String, Object> params = new HashMap<>();
            params.put("customer", tenant.getStripeCustomerId());
            params.put("limit", 100);

            InvoiceCollection invoices = Invoice.list(params);
            List<Map<String, Object>> billingHistory = new ArrayList<>();

            for (Invoice invoice : invoices.getData()) {
                Map<String, Object> invoiceData = new HashMap<>();
                invoiceData.put("id", invoice.getId());
                invoiceData.put("amount", invoice.getAmountPaid());
                invoiceData.put("currency", invoice.getCurrency());
                invoiceData.put("status", invoice.getStatus());
                invoiceData.put("created", invoice.getCreated());
                invoiceData.put("periodStart", invoice.getPeriodStart());
                invoiceData.put("periodEnd", invoice.getPeriodEnd());
                billingHistory.add(invoiceData);
            }

            return billingHistory;

        } catch (StripeException e) {
            log.error("Failed to get billing history for tenant {}", tenantId, e);
            throw new RuntimeException("Failed to get billing history", e);
        }
    }

    @Override
    public void cancelSubscription(Long tenantId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            if (tenant.getStripeSubscriptionId() == null) {
                throw new IllegalArgumentException("No active subscription found");
            }

            Subscription subscription = Subscription.retrieve(tenant.getStripeSubscriptionId());
            subscription.cancel();

            log.info("Cancelled subscription for tenant {}", tenantId);

        } catch (StripeException e) {
            log.error("Failed to cancel subscription for tenant {}", tenantId, e);
            throw new RuntimeException("Failed to cancel subscription", e);
        }
    }

    @Override
    public void reactivateSubscription(Long tenantId) {
        // Implementation for reactivating subscription
        log.info("Reactivating subscription for tenant {}", tenantId);
    }

    @Override
    public String getSubscriptionStatus(Long tenantId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            if (tenant.getStripeSubscriptionId() == null) {
                return "no_subscription";
            }

            Subscription subscription = Subscription.retrieve(tenant.getStripeSubscriptionId());
            return subscription.getStatus();

        } catch (StripeException e) {
            log.error("Failed to get subscription status for tenant {}", tenantId, e);
            return "unknown";
        }
    }

    @Override
    public Map<String, Object> getUpcomingInvoice(Long tenantId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            if (tenant.getStripeCustomerId() == null) {
                return new HashMap<>();
            }

            Map<String, Object> params = new HashMap<>();
            params.put("customer", tenant.getStripeCustomerId());

            Invoice upcomingInvoice = Invoice.upcoming(params);
            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("amount", upcomingInvoice.getAmountDue());
            invoiceData.put("currency", upcomingInvoice.getCurrency());
            invoiceData.put("periodStart", upcomingInvoice.getPeriodStart());
            invoiceData.put("periodEnd", upcomingInvoice.getPeriodEnd());

            return invoiceData;

        } catch (StripeException e) {
            log.error("Failed to get upcoming invoice for tenant {}", tenantId, e);
            return new HashMap<>();
        }
    }

    @Override
    public void updatePaymentMethod(Long tenantId, String paymentMethodId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

            if (tenant.getStripeCustomerId() == null) {
                throw new IllegalArgumentException("No Stripe customer found");
            }

            Customer customer = Customer.retrieve(tenant.getStripeCustomerId());
            Map<String, Object> params = new HashMap<>();
            params.put("invoice_settings", Map.of("default_payment_method", paymentMethodId));
            customer.update(params);

            log.info("Updated payment method for tenant {}", tenantId);

        } catch (StripeException e) {
            log.error("Failed to update payment method for tenant {}", tenantId, e);
            throw new RuntimeException("Failed to update payment method", e);
        }
    }

    @Override
    public List<Map<String, Object>> getAvailablePlans() {
        List<Map<String, Object>> plans = new ArrayList<>();
        
        // Free Plan
        Map<String, Object> freePlan = new HashMap<>();
        freePlan.put("id", "free");
        freePlan.put("name", "Free");
        freePlan.put("price", 0);
        freePlan.put("currency", "usd");
        freePlan.put("interval", "month");
        freePlan.put("features", Arrays.asList(
            "5 users",
            "10 campaigns/month",
            "1,000 emails/month",
            "50 SMS/month",
            "25 WhatsApp/month",
            "100 MB storage"
        ));
        plans.add(freePlan);

        // Starter Plan
        Map<String, Object> starterPlan = new HashMap<>();
        starterPlan.put("id", "starter");
        starterPlan.put("name", "Starter");
        starterPlan.put("price", 29);
        starterPlan.put("currency", "usd");
        starterPlan.put("interval", "month");
        starterPlan.put("priceId", "price_starter_monthly");
        starterPlan.put("features", Arrays.asList(
            "10 users",
            "50 campaigns/month",
            "10,000 emails/month",
            "500 SMS/month",
            "100 WhatsApp/month",
            "1 GB storage",
            "Email support"
        ));
        plans.add(starterPlan);

        // Professional Plan
        Map<String, Object> professionalPlan = new HashMap<>();
        professionalPlan.put("id", "professional");
        professionalPlan.put("name", "Professional");
        professionalPlan.put("price", 99);
        professionalPlan.put("currency", "usd");
        professionalPlan.put("interval", "month");
        professionalPlan.put("priceId", "price_professional_monthly");
        professionalPlan.put("features", Arrays.asList(
            "25 users",
            "200 campaigns/month",
            "50,000 emails/month",
            "2,000 SMS/month",
            "500 WhatsApp/month",
            "5 GB storage",
            "Priority support",
            "Advanced analytics"
        ));
        plans.add(professionalPlan);

        // Enterprise Plan
        Map<String, Object> enterprisePlan = new HashMap<>();
        enterprisePlan.put("id", "enterprise");
        enterprisePlan.put("name", "Enterprise");
        enterprisePlan.put("price", 299);
        enterprisePlan.put("currency", "usd");
        enterprisePlan.put("interval", "month");
        enterprisePlan.put("priceId", "price_enterprise_monthly");
        enterprisePlan.put("features", Arrays.asList(
            "100 users",
            "1,000 campaigns/month",
            "200,000 emails/month",
            "10,000 SMS/month",
            "2,000 WhatsApp/month",
            "50 GB storage",
            "24/7 support",
            "Custom integrations",
            "Dedicated account manager"
        ));
        plans.add(enterprisePlan);

        return plans;
    }

    private Map<String, Object> getPlanDetails(String planId) {
        List<Map<String, Object>> plans = getAvailablePlans();
        return plans.stream()
                .filter(plan -> planId.equals(plan.get("id")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
    }

    private Tenant.SubscriptionPlan getPlanFromPriceId(String priceId) {
        switch (priceId) {
            case "price_starter_monthly":
                return Tenant.SubscriptionPlan.STARTER;
            case "price_professional_monthly":
                return Tenant.SubscriptionPlan.PROFESSIONAL;
            case "price_enterprise_monthly":
                return Tenant.SubscriptionPlan.ENTERPRISE;
            default:
                return Tenant.SubscriptionPlan.FREE;
        }
    }
}
