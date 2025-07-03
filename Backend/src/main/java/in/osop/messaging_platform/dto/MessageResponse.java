package in.osop.messaging_platform.dto;

import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private String status;
    private MessageChannel channel;
    private List<String> recipients;
    private Map<String, MessageStatus> details;
    private String errorMessage;
} 