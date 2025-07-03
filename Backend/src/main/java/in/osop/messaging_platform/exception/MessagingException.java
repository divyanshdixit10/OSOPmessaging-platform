package in.osop.messaging_platform.exception;

/**
 * Custom exception for messaging related errors.
 */
public class MessagingException extends RuntimeException {
    
    public MessagingException(String message) {
        super(message);
    }
    
    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
} 