package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.*;
import in.osop.messaging_platform.model.*;
import in.osop.messaging_platform.repository.*;
import in.osop.messaging_platform.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campaigns/enhanced")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enhanced Campaign API", description = "APIs for campaign management with background processing")
@CrossOrigin(origins = "http://localhost:3000")
public class EnhancedCampaignController {
    
    private final AsyncEmailService asyncEmailService;
    private final CampaignProgressRepository campaignProgressRepository;
    private final CampaignRepository campaignRepository;
    private final MessageLogRepository messageLogRepository;
    private final ActivityLogService activityLogService;
    
    @PostMapping("/send")
    @Operation(summary = "Send campaign immediately", description = "Queue campaign for immediate background sending")
    @ApiResponse(responseCode = "200", description = "Campaign queued successfully")
    public ResponseEntity<Map<String, Object>> sendCampaign(@RequestBody SendCampaignRequest request) {
        log.info("Sending campaign immediately: {}", request.getCampaignId());
        
        try {
            // Start async sending
            asyncEmailService.sendCampaignAsync(request.getCampaignId());
            
            // Log activity
            activityLogService.logActivity(
                ActivityLog.ActivityType.CAMPAIGN_STARTED,
                "Campaign Started",
                "Campaign has been queued for immediate sending",
                request.getUserId(),
                "campaign",
                request.getCampaignId()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Campaign queued for sending",
                "campaignId", request.getCampaignId()
            ));
            
        } catch (Exception e) {
            log.error("Error queuing campaign: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to queue campaign: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/schedule")
    @Operation(summary = "Schedule campaign for later", description = "Schedule campaign to be sent at a specific time")
    @ApiResponse(responseCode = "200", description = "Campaign scheduled successfully")
    public ResponseEntity<Map<String, Object>> scheduleCampaign(@RequestBody ScheduleCampaignRequest request) {
        log.info("Scheduling campaign: {} for {}", request.getCampaignId(), request.getScheduledTime());
        
        try {
            // Create campaign progress with scheduled status
            CampaignProgress progress = CampaignProgress.builder()
                .campaignId(request.getCampaignId())
                .status(CampaignProgress.CampaignProgressStatus.SCHEDULED)
                .scheduledTime(request.getScheduledTime())
                .batchSize(request.getBatchSize() != null ? request.getBatchSize() : 50)
                .rateLimitPerMinute(request.getRateLimitPerMinute() != null ? request.getRateLimitPerMinute() : 100)
                .build();
            
            campaignProgressRepository.save(progress);
            
            // Log activity
            activityLogService.logActivity(
                ActivityLog.ActivityType.CAMPAIGN_CREATED,
                "Campaign Scheduled",
                "Campaign scheduled for " + request.getScheduledTime(),
                request.getUserId(),
                "campaign",
                request.getCampaignId()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Campaign scheduled successfully",
                "campaignId", request.getCampaignId(),
                "scheduledTime", request.getScheduledTime()
            ));
            
        } catch (Exception e) {
            log.error("Error scheduling campaign: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to schedule campaign: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{id}/progress")
    @Operation(summary = "Get campaign progress", description = "Get real-time progress of a campaign")
    @ApiResponse(responseCode = "200", description = "Campaign progress retrieved successfully")
    public ResponseEntity<CampaignProgressDto> getCampaignProgress(@PathVariable Long id) {
        log.info("Getting progress for campaign: {}", id);
        
        CampaignProgress progress = campaignProgressRepository.findByCampaignId(id)
            .orElseThrow(() -> new RuntimeException("Campaign progress not found: " + id));
        
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
        
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/{id}/pause")
    @Operation(summary = "Pause campaign", description = "Pause a running campaign")
    @ApiResponse(responseCode = "200", description = "Campaign paused successfully")
    public ResponseEntity<Map<String, Object>> pauseCampaign(@PathVariable Long id) {
        log.info("Pausing campaign: {}", id);
        
        try {
            CampaignProgress progress = campaignProgressRepository.findByCampaignId(id)
                .orElseThrow(() -> new RuntimeException("Campaign progress not found: " + id));
            
            if (progress.getStatus() == CampaignProgress.CampaignProgressStatus.RUNNING) {
                progress.setStatus(CampaignProgress.CampaignProgressStatus.PAUSED);
                campaignProgressRepository.save(progress);
                
                // Log activity
                activityLogService.logActivity(
                    ActivityLog.ActivityType.CAMPAIGN_PAUSED,
                    "Campaign Paused",
                    "Campaign has been paused",
                    "system",
                    "campaign",
                    id
                );
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Campaign paused successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Campaign is not running and cannot be paused"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error pausing campaign: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to pause campaign: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{id}/resume")
    @Operation(summary = "Resume campaign", description = "Resume a paused campaign")
    @ApiResponse(responseCode = "200", description = "Campaign resumed successfully")
    public ResponseEntity<Map<String, Object>> resumeCampaign(@PathVariable Long id) {
        log.info("Resuming campaign: {}", id);
        
        try {
            CampaignProgress progress = campaignProgressRepository.findByCampaignId(id)
                .orElseThrow(() -> new RuntimeException("Campaign progress not found: " + id));
            
            if (progress.getStatus() == CampaignProgress.CampaignProgressStatus.PAUSED) {
                progress.setStatus(CampaignProgress.CampaignProgressStatus.RUNNING);
                campaignProgressRepository.save(progress);
                
                // Continue async sending
                asyncEmailService.sendCampaignAsync(id);
                
                // Log activity
                activityLogService.logActivity(
                    ActivityLog.ActivityType.CAMPAIGN_STARTED,
                    "Campaign Resumed",
                    "Campaign has been resumed",
                    "system",
                    "campaign",
                    id
                );
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Campaign resumed successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Campaign is not paused and cannot be resumed"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error resuming campaign: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to resume campaign: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel campaign", description = "Cancel a running or scheduled campaign")
    @ApiResponse(responseCode = "200", description = "Campaign cancelled successfully")
    public ResponseEntity<Map<String, Object>> cancelCampaign(@PathVariable Long id) {
        log.info("Cancelling campaign: {}", id);
        
        try {
            CampaignProgress progress = campaignProgressRepository.findByCampaignId(id)
                .orElseThrow(() -> new RuntimeException("Campaign progress not found: " + id));
            
            if (progress.getStatus() == CampaignProgress.CampaignProgressStatus.RUNNING ||
                progress.getStatus() == CampaignProgress.CampaignProgressStatus.SCHEDULED) {
                
                progress.setStatus(CampaignProgress.CampaignProgressStatus.CANCELLED);
                progress.setCompletedAt(LocalDateTime.now());
                campaignProgressRepository.save(progress);
                
                // Log activity
                activityLogService.logActivity(
                    ActivityLog.ActivityType.CAMPAIGN_PAUSED,
                    "Campaign Cancelled",
                    "Campaign has been cancelled",
                    "system",
                    "campaign",
                    id
                );
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Campaign cancelled successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Campaign cannot be cancelled in its current state"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error cancelling campaign: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to cancel campaign: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{id}/retry-failed")
    @Operation(summary = "Retry failed emails", description = "Retry sending failed emails for a campaign")
    @ApiResponse(responseCode = "200", description = "Failed emails retry started successfully")
    public ResponseEntity<Map<String, Object>> retryFailedEmails(@PathVariable Long id) {
        log.info("Retrying failed emails for campaign: {}", id);
        
        try {
            // Start async retry
            asyncEmailService.retryFailedEmails(id);
            
            // Log activity
            activityLogService.logActivity(
                ActivityLog.ActivityType.EMAIL_SENT,
                "Retry Failed Emails",
                "Retrying failed emails for campaign",
                "system",
                "campaign",
                id
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Failed emails retry started"
            ));
            
        } catch (Exception e) {
            log.error("Error retrying failed emails: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retry emails: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{id}/analytics")
    @Operation(summary = "Get campaign analytics", description = "Get detailed analytics for a campaign")
    @ApiResponse(responseCode = "200", description = "Campaign analytics retrieved successfully")
    public ResponseEntity<CampaignAnalyticsDto> getCampaignAnalytics(@PathVariable Long id) {
        log.info("Getting analytics for campaign: {}", id);
        
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + id));
        
        CampaignProgress progress = campaignProgressRepository.findByCampaignId(id)
            .orElse(null);
        
        // Get email logs for this campaign
        List<MessageLog> emailLogs = messageLogRepository.findByCampaignIdOrderByTimestampDesc(id);
        
        CampaignAnalyticsDto dto = CampaignAnalyticsDto.builder()
            .id(campaign.getId())
            .name(campaign.getName())
            .description(campaign.getDescription())
            .status(campaign.getStatus())
            .totalRecipients(campaign.getTotalRecipients())
            .sentCount(progress != null ? progress.getEmailsSent() : 0)
            .deliveredCount(campaign.getDeliveredCount())
            .openedCount(campaign.getOpenedCount())
            .clickedCount(campaign.getClickedCount())
            .bouncedCount(campaign.getBouncedCount())
            .unsubscribedCount(campaign.getUnsubscribedCount())
            .openRate(campaign.getOpenRate())
            .clickRate(campaign.getClickRate())
            .bounceRate(campaign.getBounceRate())
            .unsubscribeRate(campaign.getUnsubscribeRate())
            .progressPercentage(progress != null ? progress.getProgressPercentage() : 0.0)
            .createdAt(campaign.getCreatedAt())
            .startedAt(progress != null ? progress.getStartedAt() : null)
            .completedAt(progress != null ? progress.getCompletedAt() : campaign.getCompletedAt())
            .build();
        
        return ResponseEntity.ok(dto);
    }
}
