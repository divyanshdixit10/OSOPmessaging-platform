package in.osop.messaging_platform.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@Slf4j
public class WebSocketController {
    
    @MessageMapping("/email.send")
    @SendTo("/topic/email_updates")
    public Map<String, Object> handleEmailSend(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received email send message: {}", message);
        
        // Echo back the message with timestamp
        return Map.of(
            "type", "email_send_response",
            "originalMessage", message,
            "timestamp", System.currentTimeMillis(),
            "sessionId", headerAccessor.getSessionId()
        );
    }
    
    @MessageMapping("/analytics.request")
    @SendTo("/topic/analytics")
    public Map<String, Object> handleAnalyticsRequest(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received analytics request: {}", message);
        
        // Return mock analytics data
        return Map.of(
            "type", "analytics_response",
            "data", Map.of(
                "totalEmails", 1250,
                "delivered", 1180,
                "opened", 890,
                "clicked", 234,
                "bounced", 70,
                "deliveryRate", 94.4,
                "openRate", 75.4,
                "clickRate", 26.3
            ),
            "timestamp", System.currentTimeMillis(),
            "sessionId", headerAccessor.getSessionId()
        );
    }
    
    @MessageMapping("/dashboard.subscribe")
    @SendTo("/topic/dashboard")
    public Map<String, Object> handleDashboardSubscribe(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("User subscribed to dashboard updates: {}", headerAccessor.getSessionId());
        
        return Map.of(
            "type", "dashboard_subscribed",
            "message", "Successfully subscribed to dashboard updates",
            "timestamp", System.currentTimeMillis(),
            "sessionId", headerAccessor.getSessionId()
        );
    }
}
