package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
    
    /**
     * Sends a message through the specified channel
     * 
     * @param request The message request containing all details
     * @return The message response with status details
     */
    MessageResponse sendMessage(MessageRequest request);
    
    /**
     * Handles file attachment for email
     * 
     * @param attachment The file to be attached
     * @param request The message request to update with attachment
     * @return The updated message request
     */
    MessageRequest handleAttachment(MultipartFile attachment, MessageRequest request);
} 