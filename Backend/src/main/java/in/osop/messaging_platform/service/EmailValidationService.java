package in.osop.messaging_platform.service;

import java.util.List;
import java.util.Map;

/**
 * Service for advanced email validation including MX record checking and deliverability validation
 */
public interface EmailValidationService {
    
    /**
     * Validate a single email address
     * @param email Email address to validate
     * @return ValidationResult with status and details
     */
    ValidationResult validateEmail(String email);
    
    /**
     * Validate multiple email addresses
     * @param emails List of email addresses to validate
     * @return Map of email to ValidationResult
     */
    Map<String, ValidationResult> validateEmails(List<String> emails);
    
    /**
     * Check if email domain has valid MX records
     * @param domain Email domain to check
     * @return true if domain has valid MX records
     */
    boolean hasValidMxRecords(String domain);
    
    /**
     * Check if email is disposable/temporary
     * @param email Email address to check
     * @return true if email is disposable
     */
    boolean isDisposableEmail(String email);
    
    /**
     * Check if email is in a known spam trap
     * @param email Email address to check
     * @return true if email is in spam trap
     */
    boolean isSpamTrap(String email);
    
    /**
     * Get email reputation score (0-100)
     * @param email Email address to check
     * @return Reputation score
     */
    int getEmailReputation(String email);
    
    /**
     * Validation result class
     */
    class ValidationResult {
        private final boolean valid;
        private final String reason;
        private final ValidationType type;
        private final int reputationScore;
        
        public ValidationResult(boolean valid, String reason, ValidationType type, int reputationScore) {
            this.valid = valid;
            this.reason = reason;
            this.type = type;
            this.reputationScore = reputationScore;
        }
        
        public boolean isValid() { return valid; }
        public String getReason() { return reason; }
        public ValidationType getType() { return type; }
        public int getReputationScore() { return reputationScore; }
    }
    
    /**
     * Validation types
     */
    enum ValidationType {
        VALID,
        INVALID_FORMAT,
        DOMAIN_NOT_FOUND,
        NO_MX_RECORDS,
        DISPOSABLE_EMAIL,
        SPAM_TRAP,
        BOUNCED_EMAIL,
        UNKNOWN
    }
}
