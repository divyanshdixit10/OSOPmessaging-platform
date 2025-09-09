package in.osop.messaging_platform.service;

import in.osop.messaging_platform.model.*;
import in.osop.messaging_platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailService {
    
    private final JavaMailSender mailSender;
    private final CampaignProgressRepository campaignProgressRepository;
    private final MessageLogRepository messageLogRepository;
    private final CampaignRepository campaignRepository;
    private final SubscriberRepository subscriberRepository;
    private final ActivityLogService activityLogService;
    
    /**
     * Start sending a campaign asynchronously
     */
    @Async("emailTaskExecutor")
    @Transactional
    public CompletableFuture<Void> sendCampaignAsync(Long campaignId) {
        log.info("Starting async campaign sending for campaign ID: {}", campaignId);
        
        try {
            Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
            
            // Create or update campaign progress
            CampaignProgress progress = campaignProgressRepository.findByCampaignId(campaignId)
                .orElse(CampaignProgress.builder()
                    .campaignId(campaignId)
                    .status(CampaignProgress.CampaignProgressStatus.RUNNING)
                    .totalRecipients(campaign.getTotalRecipients())
                    .startedAt(LocalDateTime.now())
                    .build());
            
            progress.setStatus(CampaignProgress.CampaignProgressStatus.RUNNING);
            progress.setStartedAt(LocalDateTime.now());
            campaignProgressRepository.save(progress);
            
            // Get subscribers for this campaign
            List<Subscriber> subscribers = subscriberRepository.findByStatus(SubscriptionStatus.ACTIVE);
            
            // Calculate batches
            int batchSize = progress.getBatchSize();
            int totalBatches = (int) Math.ceil((double) subscribers.size() / batchSize);
            progress.setTotalBatches(totalBatches);
            campaignProgressRepository.save(progress);
            
            // Send emails in batches
            for (int batchNum = 0; batchNum < totalBatches; batchNum++) {
                if (progress.getStatus() == CampaignProgress.CampaignProgressStatus.CANCELLED ||
                    progress.getStatus() == CampaignProgress.CampaignProgressStatus.PAUSED) {
                    log.info("Campaign {} paused or cancelled, stopping batch processing", campaignId);
                    break;
                }
                
                int startIndex = batchNum * batchSize;
                int endIndex = Math.min(startIndex + batchSize, subscribers.size());
                List<Subscriber> batchSubscribers = subscribers.subList(startIndex, endIndex);
                
                sendBatch(campaign, batchSubscribers, batchNum + 1, progress);
                
                // Update progress
                progress.setCurrentBatchNumber(batchNum + 1);
                progress.setLastBatchSentAt(LocalDateTime.now());
                campaignProgressRepository.save(progress);
                
                // Rate limiting - wait between batches
                if (batchNum < totalBatches - 1) {
                    long waitTime = calculateWaitTime(progress.getRateLimitPerMinute(), batchSize);
                    Thread.sleep(waitTime);
                }
            }
            
            // Mark campaign as completed
            if (progress.getStatus() == CampaignProgress.CampaignProgressStatus.RUNNING) {
                progress.setStatus(CampaignProgress.CampaignProgressStatus.COMPLETED);
                progress.setCompletedAt(LocalDateTime.now());
                campaignProgressRepository.save(progress);
                
                // Update campaign status
                campaign.setStatus(CampaignStatus.COMPLETED);
                campaign.setCompletedAt(LocalDateTime.now());
                campaignRepository.save(campaign);
                
                log.info("Campaign {} completed successfully", campaignId);
                activityLogService.logActivity(
                    ActivityLog.ActivityType.CAMPAIGN_COMPLETED,
                    "Campaign Completed",
                    "Campaign '" + campaign.getName() + "' has been completed successfully",
                    "system",
                    "campaign",
                    campaignId
                );
            }
            
        } catch (Exception e) {
            log.error("Error sending campaign {}: {}", campaignId, e.getMessage(), e);
            
            // Mark campaign as failed
            CampaignProgress progress = campaignProgressRepository.findByCampaignId(campaignId).orElse(null);
            if (progress != null) {
                progress.setStatus(CampaignProgress.CampaignProgressStatus.FAILED);
                progress.setErrorMessage(e.getMessage());
                progress.setCompletedAt(LocalDateTime.now());
                campaignProgressRepository.save(progress);
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Send a batch of emails
     */
    private void sendBatch(Campaign campaign, List<Subscriber> subscribers, int batchNumber, CampaignProgress progress) {
        log.info("Sending batch {} for campaign {} with {} subscribers", batchNumber, campaign.getId(), subscribers.size());
        
        for (Subscriber subscriber : subscribers) {
            try {
                sendSingleEmail(campaign, subscriber, batchNumber);
                
                // Update progress
                progress.setEmailsSent(progress.getEmailsSent() + 1);
                progress.setEmailsSuccess(progress.getEmailsSuccess() + 1);
                
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", subscriber.getEmail(), e.getMessage());
                
                // Log the failure
                MessageLog messageLog = MessageLog.builder()
                    .campaignId(campaign.getId())
                    .batchNumber(batchNumber)
                    .channel(MessageChannel.EMAIL)
                    .recipient(subscriber.getEmail())
                    .status(MessageStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .retryCount(0)
                    .maxRetries(3)
                    .timestamp(LocalDateTime.now())
                    .build();
                messageLogRepository.save(messageLog);
                
                // Update progress
                progress.setEmailsSent(progress.getEmailsSent() + 1);
                progress.setEmailsFailed(progress.getEmailsFailed() + 1);
            }
        }
        
        campaignProgressRepository.save(progress);
    }
    
    /**
     * Send a single email
     */
    private void sendSingleEmail(Campaign campaign, Subscriber subscriber, int batchNumber) {
        long startTime = System.currentTimeMillis();
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(subscriber.getEmail());
            message.setSubject(campaign.getSubject());
            message.setText(campaign.getBody());
            
            mailSender.send(message);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Log successful send
            MessageLog messageLog = MessageLog.builder()
                .campaignId(campaign.getId())
                .batchNumber(batchNumber)
                .channel(MessageChannel.EMAIL)
                .recipient(subscriber.getEmail())
                .status(MessageStatus.SENT)
                .sentAt(LocalDateTime.now())
                .processingTimeMs(processingTime)
                .timestamp(LocalDateTime.now())
                .build();
            messageLogRepository.save(messageLog);
            
            // Log activity
            activityLogService.logEmailSent(subscriber.getEmail(), campaign.getId(), "system");
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Log failed send
            MessageLog messageLog = MessageLog.builder()
                .campaignId(campaign.getId())
                .batchNumber(batchNumber)
                .channel(MessageChannel.EMAIL)
                .recipient(subscriber.getEmail())
                .status(MessageStatus.FAILED)
                .errorMessage(e.getMessage())
                .processingTimeMs(processingTime)
                .timestamp(LocalDateTime.now())
                .build();
            messageLogRepository.save(messageLog);
            
            throw e;
        }
    }
    
    /**
     * Calculate wait time between batches for rate limiting
     */
    private long calculateWaitTime(int rateLimitPerMinute, int batchSize) {
        if (rateLimitPerMinute <= 0) return 0;
        
        // Calculate milliseconds to wait to respect rate limit
        double emailsPerSecond = rateLimitPerMinute / 60.0;
        double secondsPerEmail = 1.0 / emailsPerSecond;
        double waitTimeSeconds = batchSize * secondsPerEmail;
        
        return (long) (waitTimeSeconds * 1000); // Convert to milliseconds
    }
    
    /**
     * Scheduled task to process scheduled campaigns
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processScheduledCampaigns() {
        log.debug("Checking for scheduled campaigns to start");
        
        List<CampaignProgress> scheduledCampaigns = campaignProgressRepository
            .findScheduledCampaignsReadyToStart(LocalDateTime.now());
        
        for (CampaignProgress progress : scheduledCampaigns) {
            log.info("Starting scheduled campaign: {}", progress.getCampaignId());
            
            // Update status to running
            progress.setStatus(CampaignProgress.CampaignProgressStatus.RUNNING);
            progress.setStartedAt(LocalDateTime.now());
            campaignProgressRepository.save(progress);
            
            // Start async sending
            sendCampaignAsync(progress.getCampaignId());
        }
    }
    
    /**
     * Retry failed emails for a campaign
     */
    @Async("emailTaskExecutor")
    @Transactional
    public CompletableFuture<Void> retryFailedEmails(Long campaignId) {
        log.info("Retrying failed emails for campaign: {}", campaignId);
        
        List<MessageLog> failedEmails = messageLogRepository.findByCampaignIdAndStatusAndRetryCountLessThan(
            campaignId, MessageStatus.FAILED, 3);
        
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        for (MessageLog messageLog : failedEmails) {
            try {
                // Find subscriber
                Subscriber subscriber = subscriberRepository.findByEmail(messageLog.getRecipient())
                    .orElse(null);
                
                if (subscriber != null) {
                    sendSingleEmail(campaign, subscriber, messageLog.getBatchNumber());
                    
                    // Update message log
                    messageLog.setStatus(MessageStatus.SENT);
                    messageLog.setSentAt(LocalDateTime.now());
                    messageLog.incrementRetry();
                    messageLogRepository.save(messageLog);
                }
                
            } catch (Exception e) {
                log.error("Retry failed for email {}: {}", messageLog.getRecipient(), e.getMessage());
                messageLog.incrementRetry();
                messageLog.setErrorMessage(e.getMessage());
                messageLogRepository.save(messageLog);
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
}
