package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.model.*;
import in.osop.messaging_platform.repository.CampaignProgressRepository;
import in.osop.messaging_platform.repository.CampaignRepository;
import in.osop.messaging_platform.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service for executing campaigns
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignExecutionService {

    private final CampaignRepository campaignRepository;
    private final SubscriberRepository subscriberRepository;
    private final CampaignProgressRepository campaignProgressRepository;
    private final AsyncEmailService asyncEmailService;
    private final EmailService emailService;
    private final WebSocketService webSocketService;
    private final ActivityLogService activityLogService;

    /**
     * Execute a campaign
     */
    @Transactional
    public void executeCampaign(Long campaignId) {
        log.info("Executing campaign: {}", campaignId);
        
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
        
        // Validate campaign status
        if (campaign.getStatus() != CampaignStatus.DRAFT && 
            campaign.getStatus() != CampaignStatus.SCHEDULED) {
            throw new RuntimeException("Campaign cannot be executed from current status: " + campaign.getStatus());
        }
        
        // Update campaign status
        campaign.setStatus(CampaignStatus.RUNNING);
        campaign.setStartedAt(LocalDateTime.now());
        campaign.setIsDraft(false);
        campaignRepository.save(campaign);
        
        // Create campaign progress
        CampaignProgress progress = campaignProgressRepository.findByCampaignId(campaignId)
            .orElse(CampaignProgress.builder()
                .campaignId(campaignId)
                .status(CampaignProgress.CampaignProgressStatus.RUNNING)
                .totalRecipients(campaign.getTotalRecipients())
                .emailsSent(0)
                .emailsSuccess(0)
                .emailsFailed(0)
                .emailsInProgress(0)
                .currentBatchNumber(0)
                .totalBatches(0)
                .batchSize(50) // Default batch size
                .startedAt(LocalDateTime.now())
                .build());
        
        progress.setStatus(CampaignProgress.CampaignProgressStatus.RUNNING);
        progress.setStartedAt(LocalDateTime.now());
        campaignProgressRepository.save(progress);
        
        // Log activity
        activityLogService.logActivity(
            ActivityLog.ActivityType.CAMPAIGN_STARTED,
            "Campaign Started",
            "Campaign '" + campaign.getName() + "' has been started",
            campaign.getCreatedBy(),
            "campaign",
            campaignId
        );
        
        // Start async execution
        asyncEmailService.sendCampaignAsync(campaignId);
        
        // Send real-time update via WebSocket
        webSocketService.sendCampaignProgress(campaignId, Map.of(
            "status", "RUNNING",
            "message", "Campaign started",
            "startedAt", LocalDateTime.now().toString()
        ));
    }
    
    /**
     * Schedule a campaign for future execution
     */
    @Transactional
    public void scheduleCampaign(Long campaignId, LocalDateTime scheduledTime) {
        log.info("Scheduling campaign {} for execution at {}", campaignId, scheduledTime);
        
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
        
        // Validate campaign status
        if (campaign.getStatus() != CampaignStatus.DRAFT) {
            throw new RuntimeException("Campaign cannot be scheduled from current status: " + campaign.getStatus());
        }
        
        // Validate scheduled time
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Scheduled time must be in the future");
        }
        
        // Update campaign status
        campaign.setStatus(CampaignStatus.SCHEDULED);
        campaign.setScheduledAt(scheduledTime);
        campaign.setIsDraft(false);
        campaignRepository.save(campaign);
        
        // Create campaign progress
        CampaignProgress progress = campaignProgressRepository.findByCampaignId(campaignId)
            .orElse(CampaignProgress.builder()
                .campaignId(campaignId)
                .status(CampaignProgress.CampaignProgressStatus.SCHEDULED)
                .totalRecipients(campaign.getTotalRecipients())
                .batchSize(50) // Default batch size
                .scheduledTime(scheduledTime)
                .build());
        
        progress.setStatus(CampaignProgress.CampaignProgressStatus.SCHEDULED);
        progress.setScheduledTime(scheduledTime);
        campaignProgressRepository.save(progress);
        
        // Log activity
        activityLogService.logActivity(
            ActivityLog.ActivityType.CAMPAIGN_SCHEDULED,
            "Campaign Scheduled",
            "Campaign '" + campaign.getName() + "' has been scheduled for " + scheduledTime,
            campaign.getCreatedBy(),
            "campaign",
            campaignId
        );
        
        // Send real-time update via WebSocket
        webSocketService.sendCampaignProgress(campaignId, Map.of(
            "status", "SCHEDULED",
            "message", "Campaign scheduled",
            "scheduledTime", scheduledTime.toString()
        ));
    }
    
    /**
     * Pause a running campaign
     */
    @Transactional
    public void pauseCampaign(Long campaignId) {
        log.info("Pausing campaign: {}", campaignId);
        
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
        
        // Validate campaign status
        if (campaign.getStatus() != CampaignStatus.RUNNING) {
            throw new RuntimeException("Campaign cannot be paused from current status: " + campaign.getStatus());
        }
        
        // Update campaign status
        campaign.setStatus(CampaignStatus.PAUSED);
        campaignRepository.save(campaign);
        
        // Update campaign progress
        CampaignProgress progress = campaignProgressRepository.findByCampaignId(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign progress not found for campaign: " + campaignId));
        
        progress.setStatus(CampaignProgress.CampaignProgressStatus.PAUSED);
        campaignProgressRepository.save(progress);
        
        // Log activity
        activityLogService.logActivity(
            ActivityLog.ActivityType.CAMPAIGN_PAUSED,
            "Campaign Paused",
            "Campaign '" + campaign.getName() + "' has been paused",
            campaign.getCreatedBy(),
            "campaign",
            campaignId
        );
        
        // Send real-time update via WebSocket
        webSocketService.sendCampaignProgress(campaignId, Map.of(
            "status", "PAUSED",
            "message", "Campaign paused",
            "pausedAt", LocalDateTime.now().toString()
        ));
    }
    
    /**
     * Resume a paused campaign
     */
    @Transactional
    public void resumeCampaign(Long campaignId) {
        log.info("Resuming campaign: {}", campaignId);
        
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
        
        // Validate campaign status
        if (campaign.getStatus() != CampaignStatus.PAUSED) {
            throw new RuntimeException("Campaign cannot be resumed from current status: " + campaign.getStatus());
        }
        
        // Update campaign status
        campaign.setStatus(CampaignStatus.RUNNING);
        campaignRepository.save(campaign);
        
        // Update campaign progress
        CampaignProgress progress = campaignProgressRepository.findByCampaignId(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign progress not found for campaign: " + campaignId));
        
        progress.setStatus(CampaignProgress.CampaignProgressStatus.RUNNING);
        campaignProgressRepository.save(progress);
        
        // Log activity
        activityLogService.logActivity(
            ActivityLog.ActivityType.CAMPAIGN_RESUMED,
            "Campaign Resumed",
            "Campaign '" + campaign.getName() + "' has been resumed",
            campaign.getCreatedBy(),
            "campaign",
            campaignId
        );
        
        // Send real-time update via WebSocket
        webSocketService.sendCampaignProgress(campaignId, Map.of(
            "status", "RUNNING",
            "message", "Campaign resumed",
            "resumedAt", LocalDateTime.now().toString()
        ));
        
        // Continue async execution
        asyncEmailService.sendCampaignAsync(campaignId);
    }
    
    /**
     * Cancel a campaign
     */
    @Transactional
    public void cancelCampaign(Long campaignId) {
        log.info("Cancelling campaign: {}", campaignId);
        
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
        
        // Validate campaign status
        if (campaign.getStatus() == CampaignStatus.COMPLETED) {
            throw new RuntimeException("Campaign cannot be cancelled from current status: " + campaign.getStatus());
        }
        
        // Update campaign status
        campaign.setStatus(CampaignStatus.CANCELLED);
        campaignRepository.save(campaign);
        
        // Update campaign progress
        CampaignProgress progress = campaignProgressRepository.findByCampaignId(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign progress not found for campaign: " + campaignId));
        
        progress.setStatus(CampaignProgress.CampaignProgressStatus.CANCELLED);
        progress.setCompletedAt(LocalDateTime.now());
        campaignProgressRepository.save(progress);
        
        // Log activity
        activityLogService.logActivity(
            ActivityLog.ActivityType.CAMPAIGN_CANCELLED,
            "Campaign Cancelled",
            "Campaign '" + campaign.getName() + "' has been cancelled",
            campaign.getCreatedBy(),
            "campaign",
            campaignId
        );
        
        // Send real-time update via WebSocket
        webSocketService.sendCampaignProgress(campaignId, Map.of(
            "status", "CANCELLED",
            "message", "Campaign cancelled",
            "cancelledAt", LocalDateTime.now().toString()
        ));
    }
    
    /**
     * Send test emails for a campaign
     */
    @Transactional
    public void sendTestEmails(Long campaignId, List<String> testEmails) {
        log.info("Sending test emails for campaign {} to {} recipients", campaignId, testEmails.size());
        
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
        
        // Create message request
        MessageRequest request = MessageRequest.builder()
            .channel(MessageChannel.EMAIL)
            .recipients(testEmails)
            .subject(campaign.getSubject())
            .message(campaign.getBody())
            .trackOpens(campaign.getTrackOpens())
            .trackClicks(campaign.getTrackClicks())
            .addUnsubscribeLink(campaign.getAddUnsubscribeLink())
            .build();
        
        // Send test emails
        emailService.sendEmail(request);
        
        // Update campaign with test emails
        campaign.setTestEmails(String.join(",", testEmails));
        campaign.setIsTest(true);
        campaignRepository.save(campaign);
        
        // Log activity
        activityLogService.logActivity(
            ActivityLog.ActivityType.CAMPAIGN_TEST_SENT,
            "Campaign Test",
            "Test emails sent for campaign '" + campaign.getName() + "' to " + testEmails.size() + " recipients",
            campaign.getCreatedBy(),
            "campaign",
            campaignId
        );
    }
    
    /**
     * Get campaign progress
     */
    public CampaignProgress getCampaignProgress(Long campaignId) {
        return campaignProgressRepository.findByCampaignId(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign progress not found for campaign: " + campaignId));
    }
    
    /**
     * Get all active campaigns
     */
    public List<Campaign> getActiveCampaigns() {
        return campaignRepository.findAll().stream()
            .filter(campaign -> campaign.getStatus() == CampaignStatus.RUNNING || 
                              campaign.getStatus() == CampaignStatus.PAUSED || 
                              campaign.getStatus() == CampaignStatus.SCHEDULED)
            .collect(Collectors.toList());
    }
    
    /**
     * Scheduled task to check for campaigns that need to be started
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void checkScheduledCampaigns() {
        log.debug("Checking for scheduled campaigns to start");
        
        List<Campaign> scheduledCampaigns = campaignRepository.findAll().stream()
            .filter(campaign -> campaign.getStatus() == CampaignStatus.SCHEDULED && 
                              campaign.getScheduledAt() != null && 
                              campaign.getScheduledAt().isBefore(LocalDateTime.now()))
            .collect(Collectors.toList());
        
        for (Campaign campaign : scheduledCampaigns) {
            log.info("Starting scheduled campaign: {}", campaign.getId());
            executeCampaign(campaign.getId());
        }
    }
}
