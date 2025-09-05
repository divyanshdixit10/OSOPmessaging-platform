package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendDashboardUpdate(Object data) {
        WebSocketMessage message = WebSocketMessage.of("DASHBOARD_UPDATE", "Dashboard data updated", data);
        messagingTemplate.convertAndSend("/topic/dashboard", message);
        log.debug("Sent dashboard update to all subscribers");
    }
    
    public void sendAnalyticsUpdate(Object data) {
        WebSocketMessage message = WebSocketMessage.of("ANALYTICS_UPDATE", "Analytics data updated", data);
        messagingTemplate.convertAndSend("/topic/analytics", message);
        log.debug("Sent analytics update to all subscribers");
    }
    
    public void sendCampaignProgress(Long campaignId, Object data) {
        WebSocketMessage message = WebSocketMessage.of("CAMPAIGN_PROGRESS", "Campaign progress updated", data);
        messagingTemplate.convertAndSend("/topic/campaign/" + campaignId, message);
        log.debug("Sent campaign progress update for campaign: {}", campaignId);
    }
    
    public void sendEmailSentNotification(String email, Object data) {
        WebSocketMessage message = WebSocketMessage.of("EMAIL_SENT", "Email sent successfully", data);
        messagingTemplate.convertAndSend("/topic/notifications", message);
        log.debug("Sent email sent notification for: {}", email);
    }
    
    public void sendSubscriberUpdate(Object data) {
        WebSocketMessage message = WebSocketMessage.of("SUBSCRIBER_UPDATE", "Subscriber data updated", data);
        messagingTemplate.convertAndSend("/topic/subscribers", message);
        log.debug("Sent subscriber update to all subscribers");
    }
    
    public void sendActivityUpdate(Object data) {
        WebSocketMessage message = WebSocketMessage.of("ACTIVITY_UPDATE", "New activity recorded", data);
        messagingTemplate.convertAndSend("/topic/activity", message);
        log.debug("Sent activity update to all subscribers");
    }
    
    public void sendToUser(String username, String destination, Object data) {
        WebSocketMessage message = WebSocketMessage.of("USER_MESSAGE", "Personal message", data);
        messagingTemplate.convertAndSendToUser(username, destination, message);
        log.debug("Sent message to user: {} at destination: {}", username, destination);
    }
    
    public void sendMessage(String topic, Object data) {
        WebSocketMessage message = WebSocketMessage.of("MESSAGE", "Message update", data);
        messagingTemplate.convertAndSend("/topic/" + topic, message);
        log.debug("Sent message to topic: {}", topic);
    }
    
    public void sendMessageToUser(String username, String topic, Object message) {
        try {
            WebSocketMessage wsMessage = WebSocketMessage.of("USER_MESSAGE", "Personal message", message);
            messagingTemplate.convertAndSendToUser(username, "/queue/" + topic, wsMessage);
            log.debug("Sent WebSocket message to user {} on topic {}: {}", username, topic, message);
        } catch (Exception e) {
            log.error("Failed to send WebSocket message to user {} on topic {}: {}", username, topic, e.getMessage());
        }
    }
    
    public void sendEmailUpdate(String email, java.util.Map<String, Object> data) {
        try {
            java.util.Map<String, Object> message = java.util.Map.of(
                "type", "email_update",
                "email", email,
                "data", data,
                "timestamp", System.currentTimeMillis()
            );
            
            WebSocketMessage wsMessage = WebSocketMessage.of("EMAIL_UPDATE", "Email update", message);
            messagingTemplate.convertAndSend("/topic/email_updates", wsMessage);
            log.debug("Sent email update for {}: {}", email, data);
        } catch (Exception e) {
            log.error("Failed to send email update for {}: {}", email, e.getMessage());
        }
    }
}
