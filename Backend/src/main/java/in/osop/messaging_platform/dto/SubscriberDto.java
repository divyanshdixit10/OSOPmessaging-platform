package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberDto {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private SubscriptionStatus status;
    private Boolean isVerified;
    private LocalDateTime optedInAt;
    private LocalDateTime optedOutAt;
    private LocalDateTime lastEmailSentAt;
    private LocalDateTime lastEmailOpenedAt;
    private LocalDateTime lastEmailClickedAt;
    private Integer totalEmailsSent;
    private Integer totalEmailsOpened;
    private Integer totalEmailsClicked;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> preferences;
    private List<String> tags;
    private String source;
    private String notes;
}
