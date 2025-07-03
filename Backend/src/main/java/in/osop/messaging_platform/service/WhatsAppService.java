package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;

public interface WhatsAppService {
    
    /**
     * Sends a WhatsApp message
     * 
     * @param request The message request containing WhatsApp details
     * @return The message response with status details
     */
    MessageResponse sendWhatsAppMessage(MessageRequest request);
} 