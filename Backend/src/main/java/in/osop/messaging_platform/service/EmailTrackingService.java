package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.EmailEvent;
import in.osop.messaging_platform.model.EmailEventType;
import in.osop.messaging_platform.model.Campaign;
import in.osop.messaging_platform.model.Subscriber;
import in.osop.messaging_platform.repository.EmailEventRepository;
import in.osop.messaging_platform.repository.CampaignRepository;
import in.osop.messaging_platform.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTrackingService {

    private final EmailEventRepository emailEventRepository;
    private final CampaignRepository campaignRepository;
    private final SubscriberRepository subscriberRepository;

    @Transactional
    public void trackEmailEvent(Long campaignId, String email, EmailEventType eventType, 
                               Map<String, Object> eventData, String ipAddress, String userAgent) {
        try {
            log.info("Tracking email event: {} for campaign {} and email {}", eventType, campaignId, email);
            
            // Find campaign and subscriber
            Optional<Campaign> campaignOpt = campaignRepository.findById(campaignId);
            Optional<Subscriber> subscriberOpt = subscriberRepository.findByEmail(email);
            
            if (campaignOpt.isEmpty()) {
                log.warn("Campaign not found for ID: {}", campaignId);
                return;
            }
            
            Campaign campaign = campaignOpt.get();
            Subscriber subscriber = subscriberOpt.orElse(null);
            
            // Create email event
            EmailEvent emailEvent = EmailEvent.builder()
                    .campaign(campaign)
                    .subscriber(subscriber)
                    .email(email)
                    .eventType(eventType)
                    .eventData(eventData != null ? eventData.toString() : null)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();
            
            // Save the event
            emailEventRepository.save(emailEvent);
            
            // Update campaign statistics based on event type
            updateCampaignStats(campaign, eventType);
            
            log.info("Email event tracked successfully: {}", eventType);
            
        } catch (Exception e) {
            log.error("Error tracking email event: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void trackEmailSent(Long campaignId, String email) {
        trackEmailEvent(campaignId, email, EmailEventType.SENT, null, null, null);
    }

    @Transactional
    public void trackEmailDelivered(Long campaignId, String email) {
        trackEmailEvent(campaignId, email, EmailEventType.DELIVERED, null, null, null);
    }

    @Transactional
    public void trackEmailOpened(Long campaignId, String email, String ipAddress, String userAgent) {
        Map<String, Object> eventData = Map.of(
            "openedAt", LocalDateTime.now().toString(),
            "ipAddress", ipAddress != null ? ipAddress : "unknown",
            "userAgent", userAgent != null ? userAgent : "unknown"
        );
        trackEmailEvent(campaignId, email, EmailEventType.OPENED, eventData, ipAddress, userAgent);
    }

    @Transactional
    public void trackEmailClicked(Long campaignId, String email, String linkUrl, String ipAddress, String userAgent) {
        Map<String, Object> eventData = Map.of(
            "clickedAt", LocalDateTime.now().toString(),
            "linkUrl", linkUrl != null ? linkUrl : "unknown",
            "ipAddress", ipAddress != null ? ipAddress : "unknown",
            "userAgent", userAgent != null ? userAgent : "unknown"
        );
        trackEmailEvent(campaignId, email, EmailEventType.CLICKED, eventData, ipAddress, userAgent);
    }

    @Transactional
    public void trackEmailBounced(Long campaignId, String email, String bounceReason) {
        Map<String, Object> eventData = Map.of(
            "bouncedAt", LocalDateTime.now().toString(),
            "bounceReason", bounceReason != null ? bounceReason : "unknown"
        );
        trackEmailEvent(campaignId, email, EmailEventType.BOUNCED, eventData, null, null);
    }

    @Transactional
    public void trackEmailUnsubscribed(Long campaignId, String email, String ipAddress, String userAgent) {
        Map<String, Object> eventData = Map.of(
            "unsubscribedAt", LocalDateTime.now().toString(),
            "ipAddress", ipAddress != null ? ipAddress : "unknown",
            "userAgent", userAgent != null ? userAgent : "unknown"
        );
        trackEmailEvent(campaignId, email, EmailEventType.UNSUBSCRIBED, eventData, ipAddress, userAgent);
    }

    // Methods for tracking by EmailEvent ID
    @Transactional
    public void trackEmailOpenedByEventId(Long emailEventId, String email, String ipAddress, String userAgent) {
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isPresent()) {
                EmailEvent emailEvent = emailEventOpt.get();
                trackEmailOpened(emailEvent.getCampaign() != null ? emailEvent.getCampaign().getId() : null, 
                               email, ipAddress, userAgent);
            }
        } catch (Exception e) {
            log.error("Error tracking email open by event ID: {}", e.getMessage());
        }
    }

    @Transactional
    public void trackEmailClickedByEventId(Long emailEventId, String email, String linkUrl, String ipAddress, String userAgent) {
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isPresent()) {
                EmailEvent emailEvent = emailEventOpt.get();
                trackEmailClicked(emailEvent.getCampaign() != null ? emailEvent.getCampaign().getId() : null, 
                                email, linkUrl, ipAddress, userAgent);
            }
        } catch (Exception e) {
            log.error("Error tracking email click by event ID: {}", e.getMessage());
        }
    }

    @Transactional
    public void trackEmailUnsubscribedByEventId(Long emailEventId, String email, String ipAddress, String userAgent) {
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isPresent()) {
                EmailEvent emailEvent = emailEventOpt.get();
                trackEmailUnsubscribed(emailEvent.getCampaign() != null ? emailEvent.getCampaign().getId() : null, 
                                     email, ipAddress, userAgent);
            }
        } catch (Exception e) {
            log.error("Error tracking unsubscribe by event ID: {}", e.getMessage());
        }
    }

    private void updateCampaignStats(Campaign campaign, EmailEventType eventType) {
        switch (eventType) {
            case SENT:
                campaign.setSentCount(campaign.getSentCount() + 1);
                break;
            case DELIVERED:
                campaign.setDeliveredCount(campaign.getDeliveredCount() + 1);
                break;
            case OPENED:
                campaign.setOpenedCount(campaign.getOpenedCount() + 1);
                break;
            case CLICKED:
                campaign.setClickedCount(campaign.getClickedCount() + 1);
                break;
            case BOUNCED:
                campaign.setBouncedCount(campaign.getBouncedCount() + 1);
                break;
            case UNSUBSCRIBED:
                campaign.setUnsubscribedCount(campaign.getUnsubscribedCount() + 1);
                break;
        }
        
        campaign.setUpdatedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
        
        log.debug("Updated campaign {} stats for event type: {}", campaign.getId(), eventType);
    }

    public long getEventCount(Long campaignId, EmailEventType eventType) {
        return emailEventRepository.countByCampaignIdAndEventType(campaignId, eventType);
    }

    public long getEventCountForEmail(String email, EmailEventType eventType) {
        return emailEventRepository.countByEmailAndEventType(email, eventType);
    }
}
