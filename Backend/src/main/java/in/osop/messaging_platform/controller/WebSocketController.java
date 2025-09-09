package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.LiveStatsDto;
import in.osop.messaging_platform.dto.CampaignProgressDto;
import in.osop.messaging_platform.model.CampaignProgress;
import in.osop.messaging_platform.service.AnalyticsService;
import in.osop.messaging_platform.service.AsyncEmailService;
import in.osop.messaging_platform.repository.CampaignProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final AnalyticsService analyticsService;
    private final AsyncEmailService asyncEmailService;
    private final CampaignProgressRepository campaignProgressRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
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
    
    @MessageMapping("/analytics.live")
    @SendTo("/topic/analytics_live")
    public Map<String, Object> handleLiveAnalyticsRequest(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received live analytics request: {}", message);
        
        try {
            // Get live stats from the service
            LiveStatsDto liveStats = analyticsService.getLiveStats(null, null);
            
            return Map.of(
                "type", "live_analytics_update",
                "data", liveStats,
                "timestamp", System.currentTimeMillis(),
                "sessionId", headerAccessor.getSessionId()
            );
        } catch (Exception e) {
            log.error("Error getting live analytics: ", e);
            return Map.of(
                "type", "error",
                "message", "Failed to get live analytics",
                "timestamp", System.currentTimeMillis()
            );
        }
    }
    
    // Scheduled method to broadcast live analytics updates every 10 seconds
    @Scheduled(fixedRate = 10000) // 10 seconds
    public void broadcastLiveAnalytics() {
        try {
            LiveStatsDto liveStats = analyticsService.getLiveStats(null, null);
            
            Map<String, Object> message = Map.of(
                "type", "live_analytics_broadcast",
                "data", liveStats,
                "timestamp", System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSend("/topic/analytics_live", message);
            log.debug("Broadcasted live analytics update");
        } catch (Exception e) {
            log.error("Error broadcasting live analytics: ", e);
        }
    }
    
    // Scheduled method to broadcast campaign progress updates every 5 seconds
    @Scheduled(fixedRate = 5000) // 5 seconds
    public void broadcastCampaignProgress() {
        try {
            // Get all running campaigns
            List<CampaignProgress> runningCampaigns = campaignProgressRepository
                .findByStatus(CampaignProgress.CampaignProgressStatus.RUNNING);
            
            for (CampaignProgress progress : runningCampaigns) {
                CampaignProgressDto dto = CampaignProgressDto.builder()
                    .campaignId(progress.getCampaignId())
                    .status(progress.getStatus().toString())
                    .totalRecipients(progress.getTotalRecipients())
                    .emailsSent(progress.getEmailsSent())
                    .emailsSuccess(progress.getEmailsSuccess())
                    .emailsFailed(progress.getEmailsFailed())
                    .emailsInProgress(progress.getEmailsInProgress())
                    .progressPercentage(progress.getProgressPercentage())
                    .successRate(progress.getSuccessRate())
                    .failureRate(progress.getFailureRate())
                    .currentBatchNumber(progress.getCurrentBatchNumber())
                    .totalBatches(progress.getTotalBatches())
                    .scheduledTime(progress.getScheduledTime())
                    .startedAt(progress.getStartedAt())
                    .completedAt(progress.getCompletedAt())
                    .lastBatchSentAt(progress.getLastBatchSentAt())
                    .errorMessage(progress.getErrorMessage())
                    .build();
                
                Map<String, Object> message = Map.of(
                    "type", "campaign_progress_update",
                    "campaignId", progress.getCampaignId(),
                    "data", dto,
                    "timestamp", System.currentTimeMillis()
                );
                
                messagingTemplate.convertAndSend("/topic/campaign_progress", message);
            }
            
            log.debug("Broadcasted campaign progress updates for {} campaigns", runningCampaigns.size());
        } catch (Exception e) {
            log.error("Error broadcasting campaign progress: ", e);
        }
    }
    
    @MessageMapping("/campaign.subscribe")
    @SendTo("/topic/campaign_progress")
    public Map<String, Object> handleCampaignSubscribe(Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("User subscribed to campaign progress updates: {}", headerAccessor.getSessionId());
        
        return Map.of(
            "type", "campaign_subscribed",
            "message", "Successfully subscribed to campaign progress updates",
            "timestamp", System.currentTimeMillis(),
            "sessionId", headerAccessor.getSessionId()
        );
    }
}
