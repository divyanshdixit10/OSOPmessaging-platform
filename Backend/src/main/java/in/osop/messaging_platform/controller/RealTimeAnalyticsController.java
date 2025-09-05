package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.service.EmailDeliveryTrackingService;
import in.osop.messaging_platform.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics/realtime")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RealTimeAnalyticsController {
    
    private final EmailDeliveryTrackingService emailDeliveryTrackingService;
    private final AnalyticsService analyticsService;
    
    @GetMapping("/delivery-status/{emailEventId}")
    public ResponseEntity<Map<String, Object>> getDeliveryStatus(@PathVariable Long emailEventId) {
        try {
            EmailDeliveryTrackingService.DeliveryStatus status = 
                emailDeliveryTrackingService.getDeliveryStatus(emailEventId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("emailEventId", emailEventId);
            response.put("status", status.name());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get delivery status for emailEventId {}: {}", emailEventId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/campaign-stats/{campaignId}")
    public ResponseEntity<EmailDeliveryTrackingService.DeliveryStatistics> getCampaignStats(@PathVariable Long campaignId) {
        try {
            EmailDeliveryTrackingService.DeliveryStatistics stats = 
                emailDeliveryTrackingService.getDeliveryStatistics(campaignId);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Failed to get campaign stats for campaignId {}: {}", campaignId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/bounced-emails")
    public ResponseEntity<List<String>> getBouncedEmails(@RequestParam(defaultValue = "24") int hours) {
        try {
            List<String> bouncedEmails = emailDeliveryTrackingService.getBouncedEmails(hours);
            return ResponseEntity.ok(bouncedEmails);
            
        } catch (Exception e) {
            log.error("Failed to get bounced emails: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/live-stats")
    public ResponseEntity<Map<String, Object>> getLiveStats() {
        try {
            // Get real-time dashboard stats (last 30 days)
            var dashboardStats = analyticsService.getDashboardStats(
                java.time.LocalDateTime.now().minusDays(30), 
                java.time.LocalDateTime.now()
            );
            
            // Get recent bounced emails
            List<String> recentBounces = emailDeliveryTrackingService.getBouncedEmails(1);
            
            Map<String, Object> liveStats = new HashMap<>();
            liveStats.put("dashboardStats", dashboardStats);
            liveStats.put("recentBounces", recentBounces);
            liveStats.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(liveStats);
            
        } catch (Exception e) {
            log.error("Failed to get live stats: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/email-reputation/{email}")
    public ResponseEntity<Map<String, Object>> getEmailReputation(@PathVariable String email) {
        try {
            // This would typically come from a reputation service
            // For now, we'll return a mock response
            Map<String, Object> reputation = new HashMap<>();
            reputation.put("email", email);
            reputation.put("score", 85); // Mock score
            reputation.put("status", "GOOD");
            reputation.put("lastChecked", System.currentTimeMillis());
            
            return ResponseEntity.ok(reputation);
            
        } catch (Exception e) {
            log.error("Failed to get email reputation for {}: {}", email, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
