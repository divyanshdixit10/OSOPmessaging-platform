package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.model.EmailEvent;
import in.osop.messaging_platform.model.EmailEventType;
import in.osop.messaging_platform.repository.EmailEventRepository;
import in.osop.messaging_platform.service.EmailDeliveryTrackingService;
import in.osop.messaging_platform.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDeliveryTrackingServiceImpl implements EmailDeliveryTrackingService {
    
    private final EmailEventRepository emailEventRepository;
    private final WebSocketService webSocketService;
    
    // Cache for delivery status to avoid database queries
    private final Map<Long, DeliveryStatus> deliveryStatusCache = new HashMap<>();
    
    @Override
    @Transactional
    public void trackDeliveryStatus(Long emailEventId, DeliveryStatus status, Map<String, Object> details) {
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isEmpty()) {
                log.warn("EmailEvent not found for ID: {}", emailEventId);
                return;
            }
            
            EmailEvent emailEvent = emailEventOpt.get();
            
            // Update the event with new status
            Map<String, Object> eventData = new HashMap<>();
            if (emailEvent.getEventData() != null) {
                try {
                    // Parse existing event data
                    String[] pairs = emailEvent.getEventData().split(",");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=");
                        if (keyValue.length == 2) {
                            eventData.put(keyValue[0], keyValue[1]);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse existing event data: {}", e.getMessage());
                }
            }
            
            // Add new status and details
            eventData.put("deliveryStatus", status.name());
            eventData.put("lastUpdated", LocalDateTime.now().toString());
            if (details != null) {
                eventData.putAll(details);
            }
            
            // Convert back to string format
            String eventDataString = eventData.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
            
            emailEvent.setEventData(eventDataString);
            emailEvent.setProcessed(true);
            emailEventRepository.save(emailEvent);
            
            // Update cache
            deliveryStatusCache.put(emailEventId, status);
            
            // Send real-time update via WebSocket
            Map<String, Object> wsData = new HashMap<>();
            wsData.put("emailEventId", emailEventId);
            wsData.put("email", emailEvent.getEmail());
            wsData.put("status", status.name());
            wsData.put("timestamp", LocalDateTime.now().toString());
            wsData.putAll(details != null ? details : new HashMap<>());
            
            webSocketService.sendMessage("email_delivery_update", wsData);
            
            log.info("Tracked delivery status for email {}: {}", emailEvent.getEmail(), status);
            
        } catch (Exception e) {
            log.error("Failed to track delivery status for emailEventId {}: {}", emailEventId, e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void handleBounce(Long emailEventId, BounceType bounceType, String reason) {
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isEmpty()) {
                log.warn("EmailEvent not found for ID: {}", emailEventId);
                return;
            }
            
            EmailEvent emailEvent = emailEventOpt.get();
            
            // Create bounce event
            EmailEvent bounceEvent = EmailEvent.builder()
                .email(emailEvent.getEmail())
                .eventType(EmailEventType.BOUNCED)
                .eventData(String.format("bounceType=%s,reason=%s,originalEventId=%d", 
                    bounceType.name(), reason, emailEventId))
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();
            
            emailEventRepository.save(bounceEvent);
            
            // Update original event status
            trackDeliveryStatus(emailEventId, DeliveryStatus.BOUNCED, Map.of(
                "bounceType", bounceType.name(),
                "bounceReason", reason
            ));
            
            // Update email reputation
            updateEmailReputation(emailEvent.getEmail(), EmailEventType.BOUNCED);
            
            log.warn("Handled bounce for email {}: {} - {}", emailEvent.getEmail(), bounceType, reason);
            
        } catch (Exception e) {
            log.error("Failed to handle bounce for emailEventId {}: {}", emailEventId, e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void trackEmailOpen(Long emailEventId, String ipAddress, String userAgent) {
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isEmpty()) {
                log.warn("EmailEvent not found for ID: {}", emailEventId);
                return;
            }
            
            EmailEvent emailEvent = emailEventOpt.get();
            
            // Create open event
            EmailEvent openEvent = EmailEvent.builder()
                .email(emailEvent.getEmail())
                .eventType(EmailEventType.OPENED)
                .eventData(String.format("ipAddress=%s,userAgent=%s,originalEventId=%d", 
                    ipAddress, userAgent, emailEventId))
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();
            
            emailEventRepository.save(openEvent);
            
            // Update delivery status
            trackDeliveryStatus(emailEventId, DeliveryStatus.OPENED, Map.of(
                "ipAddress", ipAddress,
                "userAgent", userAgent,
                "openedAt", LocalDateTime.now().toString()
            ));
            
            log.info("Tracked email open for {} from IP: {}", emailEvent.getEmail(), ipAddress);
            
        } catch (Exception e) {
            log.error("Failed to track email open for emailEventId {}: {}", emailEventId, e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void trackEmailClick(Long emailEventId, String url, String ipAddress, String userAgent) {
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isEmpty()) {
                log.warn("EmailEvent not found for ID: {}", emailEventId);
                return;
            }
            
            EmailEvent emailEvent = emailEventOpt.get();
            
            // Create click event
            EmailEvent clickEvent = EmailEvent.builder()
                .email(emailEvent.getEmail())
                .eventType(EmailEventType.CLICKED)
                .eventData(String.format("url=%s,ipAddress=%s,userAgent=%s,originalEventId=%d", 
                    url, ipAddress, userAgent, emailEventId))
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();
            
            emailEventRepository.save(clickEvent);
            
            // Update delivery status
            trackDeliveryStatus(emailEventId, DeliveryStatus.CLICKED, Map.of(
                "clickedUrl", url,
                "ipAddress", ipAddress,
                "userAgent", userAgent,
                "clickedAt", LocalDateTime.now().toString()
            ));
            
            log.info("Tracked email click for {} on URL: {}", emailEvent.getEmail(), url);
            
        } catch (Exception e) {
            log.error("Failed to track email click for emailEventId {}: {}", emailEventId, e.getMessage());
        }
    }
    
    @Override
    public DeliveryStatistics getDeliveryStatistics(Long campaignId) {
        try {
            // Get all email events for the campaign
            List<EmailEvent> events = emailEventRepository.findByEventDataContaining("campaignId=" + campaignId);
            
            long totalSent = events.stream()
                .filter(e -> e.getEventType() == EmailEventType.SENT)
                .count();
            
            long delivered = events.stream()
                .filter(e -> e.getEventType() == EmailEventType.DELIVERED)
                .count();
            
            long opened = events.stream()
                .filter(e -> e.getEventType() == EmailEventType.OPENED)
                .count();
            
            long clicked = events.stream()
                .filter(e -> e.getEventType() == EmailEventType.CLICKED)
                .count();
            
            long bounced = events.stream()
                .filter(e -> e.getEventType() == EmailEventType.BOUNCED)
                .count();
            
            long complained = events.stream()
                .filter(e -> e.getEventType() == EmailEventType.COMPLAINED)
                .count();
            
            long unsubscribed = events.stream()
                .filter(e -> e.getEventType() == EmailEventType.UNSUBSCRIBED)
                .count();
            
            return new DeliveryStatistics(totalSent, delivered, opened, clicked, bounced, complained, unsubscribed);
            
        } catch (Exception e) {
            log.error("Failed to get delivery statistics for campaign {}: {}", campaignId, e.getMessage());
            return new DeliveryStatistics(0, 0, 0, 0, 0, 0, 0);
        }
    }
    
    @Override
    public DeliveryStatus getDeliveryStatus(Long emailEventId) {
        // Check cache first
        if (deliveryStatusCache.containsKey(emailEventId)) {
            return deliveryStatusCache.get(emailEventId);
        }
        
        try {
            Optional<EmailEvent> emailEventOpt = emailEventRepository.findById(emailEventId);
            if (emailEventOpt.isEmpty()) {
                return DeliveryStatus.FAILED;
            }
            
            EmailEvent emailEvent = emailEventOpt.get();
            
            // Determine status based on event type
            DeliveryStatus status = switch (emailEvent.getEventType()) {
                case SENT -> DeliveryStatus.SENT;
                case DELIVERED -> DeliveryStatus.DELIVERED;
                case OPENED -> DeliveryStatus.OPENED;
                case CLICKED -> DeliveryStatus.CLICKED;
                case BOUNCED -> DeliveryStatus.BOUNCED;
                case COMPLAINED -> DeliveryStatus.COMPLAINED;
                case UNSUBSCRIBED -> DeliveryStatus.UNSUBSCRIBED;
                default -> DeliveryStatus.PENDING;
            };
            
            // Cache the status
            deliveryStatusCache.put(emailEventId, status);
            return status;
            
        } catch (Exception e) {
            log.error("Failed to get delivery status for emailEventId {}: {}", emailEventId, e.getMessage());
            return DeliveryStatus.FAILED;
        }
    }
    
    @Override
    public List<String> getBouncedEmails(int hours) {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
            
            return emailEventRepository.findByEventTypeAndCreatedAtAfter(EmailEventType.BOUNCED, cutoff)
                .stream()
                .map(EmailEvent::getEmail)
                .distinct()
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Failed to get bounced emails: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public void updateEmailReputation(String email, EmailEventType eventType) {
        // This would typically update a reputation database
        // For now, we'll just log the reputation change
        log.info("Updated reputation for {} based on event: {}", email, eventType);
        
        // In a real implementation, you would:
        // 1. Update email reputation score
        // 2. Add to suppression list if reputation is too low
        // 3. Send alerts for reputation changes
    }
}
