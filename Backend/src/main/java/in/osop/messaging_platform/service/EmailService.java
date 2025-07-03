package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;

public interface EmailService {
    
    /**
     * Sends an email message
     * 
     * @param request The message request containing email details
     * @return The message response with status details
     */
    MessageResponse sendEmail(MessageRequest request);
} 