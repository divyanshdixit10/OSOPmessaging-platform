package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.EmailEvent;
import in.osop.messaging_platform.model.EmailEventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for tracking email delivery status and handling bounces
 */
public interface EmailDeliveryTrackingService {
    
    /**
     * Track email delivery status
     * @param emailEventId Email event ID
     * @param status Delivery status
     * @param details Additional details
     */
    void trackDeliveryStatus(Long emailEventId, DeliveryStatus status, Map<String, Object> details);
    
    /**
     * Handle email bounce
     * @param emailEventId Email event ID
     * @param bounceType Type of bounce
     * @param reason Bounce reason
     */
    void handleBounce(Long emailEventId, BounceType bounceType, String reason);
    
    /**
     * Track email open
     * @param emailEventId Email event ID
     * @param ipAddress IP address of opener
     * @param userAgent User agent string
     */
    void trackEmailOpen(Long emailEventId, String ipAddress, String userAgent);
    
    /**
     * Track email click
     * @param emailEventId Email event ID
     * @param url Clicked URL
     * @param ipAddress IP address of clicker
     * @param userAgent User agent string
     */
    void trackEmailClick(Long emailEventId, String url, String ipAddress, String userAgent);
    
    /**
     * Get delivery statistics for a campaign
     * @param campaignId Campaign ID
     * @return Delivery statistics
     */
    DeliveryStatistics getDeliveryStatistics(Long campaignId);
    
    /**
     * Get real-time delivery status
     * @param emailEventId Email event ID
     * @return Current delivery status
     */
    DeliveryStatus getDeliveryStatus(Long emailEventId);
    
    /**
     * Get bounced emails for cleanup
     * @param hours Number of hours to look back
     * @return List of bounced emails
     */
    List<String> getBouncedEmails(int hours);
    
    /**
     * Update email reputation based on delivery events
     * @param email Email address
     * @param eventType Event type
     */
    void updateEmailReputation(String email, EmailEventType eventType);
    
    /**
     * Delivery status enum
     */
    enum DeliveryStatus {
        PENDING,
        SENT,
        DELIVERED,
        OPENED,
        CLICKED,
        BOUNCED,
        COMPLAINED,
        UNSUBSCRIBED,
        FAILED
    }
    
    /**
     * Bounce type enum
     */
    enum BounceType {
        HARD_BOUNCE,
        SOFT_BOUNCE,
        BLOCKED,
        SPAM,
        INVALID_EMAIL,
        MAILBOX_FULL,
        UNKNOWN
    }
    
    /**
     * Delivery statistics class
     */
    class DeliveryStatistics {
        private final long totalSent;
        private final long delivered;
        private final long opened;
        private final long clicked;
        private final long bounced;
        private final long complained;
        private final long unsubscribed;
        private final double deliveryRate;
        private final double openRate;
        private final double clickRate;
        private final double bounceRate;
        
        public DeliveryStatistics(long totalSent, long delivered, long opened, long clicked, 
                                long bounced, long complained, long unsubscribed) {
            this.totalSent = totalSent;
            this.delivered = delivered;
            this.opened = opened;
            this.clicked = clicked;
            this.bounced = bounced;
            this.complained = complained;
            this.unsubscribed = unsubscribed;
            
            this.deliveryRate = totalSent > 0 ? (double) delivered / totalSent * 100 : 0;
            this.openRate = delivered > 0 ? (double) opened / delivered * 100 : 0;
            this.clickRate = opened > 0 ? (double) clicked / opened * 100 : 0;
            this.bounceRate = totalSent > 0 ? (double) bounced / totalSent * 100 : 0;
        }
        
        // Getters
        public long getTotalSent() { return totalSent; }
        public long getDelivered() { return delivered; }
        public long getOpened() { return opened; }
        public long getClicked() { return clicked; }
        public long getBounced() { return bounced; }
        public long getComplained() { return complained; }
        public long getUnsubscribed() { return unsubscribed; }
        public double getDeliveryRate() { return deliveryRate; }
        public double getOpenRate() { return openRate; }
        public double getClickRate() { return clickRate; }
        public double getBounceRate() { return bounceRate; }
    }
}
