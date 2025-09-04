package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.CampaignStatus;
import in.osop.messaging_platform.model.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDto {
    
    private Long id;
    private String name;
    private String description;
    private String subject;
    private String body;
    private Long templateId;
    private String templateName;
    private CampaignStatus status;
    private MessageChannel channel;
    private Integer totalRecipients;
    private Integer sentCount;
    private Integer deliveredCount;
    private Integer openedCount;
    private Integer clickedCount;
    private Integer bouncedCount;
    private Integer unsubscribedCount;
    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean trackOpens;
    private Boolean trackClicks;
    private Boolean addUnsubscribeLink;
    private Boolean isDraft;
    private Boolean isTest;
    private List<String> testEmails;
    private Double openRate;
    private Double clickRate;
    private Double bounceRate;
    private Double unsubscribeRate;
    private List<String> recipients;
}
