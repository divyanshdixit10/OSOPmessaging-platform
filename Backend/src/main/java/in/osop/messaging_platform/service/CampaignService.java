package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.CampaignDto;
import in.osop.messaging_platform.model.Campaign;
import in.osop.messaging_platform.model.CampaignStatus;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignDto createCampaign(CampaignDto campaignDto) {
        log.info("Creating new campaign: {}", campaignDto.getName());
        
        Campaign campaign = Campaign.builder()
                .name(campaignDto.getName())
                .description(campaignDto.getDescription())
                .subject(campaignDto.getSubject())
                .body(campaignDto.getBody())
                .status(CampaignStatus.DRAFT)
                .channel(campaignDto.getChannel())
                .totalRecipients(campaignDto.getTotalRecipients())
                .trackOpens(campaignDto.getTrackOpens())
                .trackClicks(campaignDto.getTrackClicks())
                .addUnsubscribeLink(campaignDto.getAddUnsubscribeLink())
                .isDraft(campaignDto.getIsDraft())
                .isTest(campaignDto.getIsTest())
                .testEmails(campaignDto.getTestEmails() != null ? 
                    String.join(",", campaignDto.getTestEmails()) : null)
                .createdBy(campaignDto.getCreatedBy())
                .build();

        Campaign saved = campaignRepository.save(campaign);
        return convertToDto(saved);
    }

    public CampaignDto getCampaignById(Long id) {
        log.info("Fetching campaign with ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        return convertToDto(campaign);
    }

    public Page<CampaignDto> getCampaigns(String name, String status, String channel, Boolean isDraft, Pageable pageable) {
        log.info("Fetching campaigns with filters: name={}, status={}, channel={}, isDraft={}", name, status, channel, isDraft);
        
        CampaignStatus campaignStatus = status != null ? CampaignStatus.valueOf(status) : null;
        MessageChannel messageChannel = channel != null ? MessageChannel.valueOf(channel) : null;
        
        Page<Campaign> campaigns = campaignRepository.findByFilters(name, campaignStatus, messageChannel, isDraft, pageable);
        return campaigns.map(this::convertToDto);
    }

    public CampaignDto updateCampaign(Long id, CampaignDto campaignDto) {
        log.info("Updating campaign with ID: {}", id);
        Campaign existing = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));

        existing.setName(campaignDto.getName());
        existing.setDescription(campaignDto.getDescription());
        existing.setSubject(campaignDto.getSubject());
        existing.setBody(campaignDto.getBody());
        existing.setChannel(campaignDto.getChannel());
        existing.setTotalRecipients(campaignDto.getTotalRecipients());
        existing.setTrackOpens(campaignDto.getTrackOpens());
        existing.setTrackClicks(campaignDto.getTrackClicks());
        existing.setAddUnsubscribeLink(campaignDto.getAddUnsubscribeLink());
        existing.setIsDraft(campaignDto.getIsDraft());
        existing.setIsTest(campaignDto.getIsTest());
        existing.setTestEmails(campaignDto.getTestEmails() != null ? 
            String.join(",", campaignDto.getTestEmails()) : null);

        Campaign updated = campaignRepository.save(existing);
        return convertToDto(updated);
    }

    public void deleteCampaign(Long id) {
        log.info("Deleting campaign with ID: {}", id);
        if (!campaignRepository.existsById(id)) {
            throw new RuntimeException("Campaign not found with id: " + id);
        }
        campaignRepository.deleteById(id);
    }

    public CampaignDto startCampaign(Long id) {
        log.info("Starting campaign with ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        
        if (campaign.getStatus() != CampaignStatus.DRAFT && campaign.getStatus() != CampaignStatus.SCHEDULED) {
            throw new RuntimeException("Campaign cannot be started from current status: " + campaign.getStatus());
        }
        
        campaign.setStatus(CampaignStatus.RUNNING);
        campaign.setStartedAt(LocalDateTime.now());
        campaign.setIsDraft(false);
        
        Campaign updated = campaignRepository.save(campaign);
        return convertToDto(updated);
    }

    public CampaignDto pauseCampaign(Long id) {
        log.info("Pausing campaign with ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        
        if (campaign.getStatus() != CampaignStatus.RUNNING) {
            throw new RuntimeException("Campaign cannot be paused from current status: " + campaign.getStatus());
        }
        
        campaign.setStatus(CampaignStatus.PAUSED);
        Campaign updated = campaignRepository.save(campaign);
        return convertToDto(updated);
    }

    public CampaignDto resumeCampaign(Long id) {
        log.info("Resuming campaign with ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        
        if (campaign.getStatus() != CampaignStatus.PAUSED) {
            throw new RuntimeException("Campaign cannot be resumed from current status: " + campaign.getStatus());
        }
        
        campaign.setStatus(CampaignStatus.RUNNING);
        Campaign updated = campaignRepository.save(campaign);
        return convertToDto(updated);
    }

    public CampaignDto cancelCampaign(Long id) {
        log.info("Cancelling campaign with ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        
        if (campaign.getStatus() == CampaignStatus.COMPLETED) {
            throw new RuntimeException("Campaign cannot be cancelled from current status: " + campaign.getStatus());
        }
        
        campaign.setStatus(CampaignStatus.CANCELLED);
        Campaign updated = campaignRepository.save(campaign);
        return convertToDto(updated);
    }

    public Map<String, Object> getCampaignStats(Long id) {
        log.info("Fetching statistics for campaign with ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("campaignId", campaign.getId());
        stats.put("name", campaign.getName());
        stats.put("status", campaign.getStatus());
        stats.put("totalRecipients", campaign.getTotalRecipients() != null ? campaign.getTotalRecipients() : 0);
        stats.put("sentCount", campaign.getSentCount() != null ? campaign.getSentCount() : 0);
        stats.put("deliveredCount", campaign.getDeliveredCount() != null ? campaign.getDeliveredCount() : 0);
        stats.put("openedCount", campaign.getOpenedCount() != null ? campaign.getOpenedCount() : 0);
        stats.put("clickedCount", campaign.getClickedCount() != null ? campaign.getClickedCount() : 0);
        stats.put("bouncedCount", campaign.getBouncedCount() != null ? campaign.getBouncedCount() : 0);
        stats.put("unsubscribedCount", campaign.getUnsubscribedCount() != null ? campaign.getUnsubscribedCount() : 0);
        stats.put("openRate", campaign.getOpenRate() != null ? campaign.getOpenRate() : 0.0);
        stats.put("clickRate", campaign.getClickRate() != null ? campaign.getClickRate() : 0.0);
        stats.put("bounceRate", campaign.getBounceRate() != null ? campaign.getBounceRate() : 0.0);
        stats.put("unsubscribeRate", campaign.getUnsubscribeRate() != null ? campaign.getUnsubscribeRate() : 0.0);
        return stats;
    }

    public Map<String, Object> getOverallStats() {
        log.info("Fetching overall campaign statistics");
        
        long totalCampaigns = campaignRepository.count();
        long activeCampaigns = campaignRepository.countByStatus(CampaignStatus.RUNNING);
        long completedCampaigns = campaignRepository.countByStatus(CampaignStatus.COMPLETED);
        long scheduledCampaigns = campaignRepository.countByStatus(CampaignStatus.SCHEDULED);
        
        Double avgOpenRate = campaignRepository.getAverageOpenRate();
        Double avgClickRate = campaignRepository.getAverageClickRate();
        
        return Map.of(
            "totalCampaigns", totalCampaigns,
            "activeCampaigns", activeCampaigns,
            "completedCampaigns", completedCampaigns,
            "scheduledCampaigns", scheduledCampaigns,
            "averageOpenRate", avgOpenRate != null ? avgOpenRate : 0.0,
            "averageClickRate", avgClickRate != null ? avgClickRate : 0.0
        );
    }

    public List<CampaignDto> getCampaignsByStatus(String status) {
        log.info("Fetching campaigns with status: {}", status);
        CampaignStatus campaignStatus = CampaignStatus.valueOf(status);
        List<Campaign> campaigns = campaignRepository.findByStatus(campaignStatus);
        return campaigns.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<CampaignDto> getScheduledCampaigns() {
        log.info("Fetching scheduled campaigns");
        List<Campaign> campaigns = campaignRepository.findScheduledCampaigns(LocalDateTime.now());
        return campaigns.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<CampaignDto> getRunningCampaigns() {
        log.info("Fetching running campaigns");
        List<Campaign> campaigns = campaignRepository.findRunningCampaigns();
        return campaigns.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private CampaignDto convertToDto(Campaign campaign) {
        return CampaignDto.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .description(campaign.getDescription())
                .subject(campaign.getSubject())
                .body(campaign.getBody())
                .templateId(campaign.getTemplateId())
                .status(campaign.getStatus())
                .channel(campaign.getChannel())
                .totalRecipients(campaign.getTotalRecipients())
                .sentCount(campaign.getSentCount())
                .deliveredCount(campaign.getDeliveredCount())
                .openedCount(campaign.getOpenedCount())
                .clickedCount(campaign.getClickedCount())
                .bouncedCount(campaign.getBouncedCount())
                .unsubscribedCount(campaign.getUnsubscribedCount())
                .scheduledAt(campaign.getScheduledAt())
                .startedAt(campaign.getStartedAt())
                .completedAt(campaign.getCompletedAt())
                .createdBy(campaign.getCreatedBy())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .trackOpens(campaign.getTrackOpens())
                .trackClicks(campaign.getTrackClicks())
                .addUnsubscribeLink(campaign.getAddUnsubscribeLink())
                .isDraft(campaign.getIsDraft())
                .isTest(campaign.getIsTest())
                .testEmails(parseTestEmails(campaign.getTestEmails()))
                .openRate(campaign.getOpenRate())
                .clickRate(campaign.getClickRate())
                .bounceRate(campaign.getBounceRate())
                .unsubscribeRate(campaign.getUnsubscribeRate())
                .build();
    }

    private List<String> parseTestEmails(String testEmailsJson) {
        if (testEmailsJson == null || testEmailsJson.trim().isEmpty()) {
            return List.of();
        }
        return List.of(testEmailsJson.split(","));
    }
}
