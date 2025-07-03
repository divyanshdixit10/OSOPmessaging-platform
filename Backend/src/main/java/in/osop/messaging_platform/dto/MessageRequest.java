package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.MessageChannel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    
    @NotNull(message = "Channel is required")
    private MessageChannel channel;
    
    @NotEmpty(message = "At least one recipient is required")
    private List<String> recipients;
    
    private String subject;
    
    @NotNull(message = "Message content is required")
    private String message;
    
    private List<String> mediaUrls;
    
    private List<MultipartFile> attachments;
    
    // Template fields
    private String templateId;
    private String templateName;
    private Map<String, String> placeholders;
    
    // Tracking fields
    @Builder.Default
    private boolean trackOpens = false;
    
    @Builder.Default
    private boolean trackClicks = false;
    
    @Builder.Default
    private boolean addUnsubscribeLink = true;
    
    // Optional fields for email
    private List<String> cc;
    private List<String> bcc;
} 