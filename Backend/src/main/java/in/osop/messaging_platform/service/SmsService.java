package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;

public interface SmsService {
    
    /**
     * Sends an SMS message
     * 
     * @param request The message request containing SMS details
     * @return The message response with status details
     */
    MessageResponse sendSms(MessageRequest request);
} 