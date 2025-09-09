package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.service.EmailValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EmailValidationServiceImpl implements EmailValidationService {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Common disposable email domains
    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
        "10minutemail.com", "tempmail.org", "guerrillamail.com", "mailinator.com",
        "temp-mail.org", "throwaway.email", "getnada.com", "maildrop.cc",
        "yopmail.com", "sharklasers.com", "guerrillamailblock.com"
    );
    
    // Known spam trap domains
    private static final Set<String> SPAM_TRAP_DOMAINS = Set.of(
        "spam.com", "spamtrap.com", "honeypot.com", "trap.com"
    );
    
    // Cache for MX records to avoid repeated DNS lookups
    private final Map<String, Boolean> mxRecordCache = new ConcurrentHashMap<>();
    private final Map<String, Integer> reputationCache = new ConcurrentHashMap<>();
    
    @Override
    public ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email is null or empty", ValidationType.INVALID_FORMAT, 0);
        }
        
        email = email.trim().toLowerCase();
        
        // Basic format validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ValidationResult(false, "Invalid email format", ValidationType.INVALID_FORMAT, 0);
        }
        
        String domain = email.substring(email.indexOf('@') + 1);
        
        // Check if it's a disposable email
        if (isDisposableEmail(email)) {
            return new ValidationResult(false, "Disposable email detected", ValidationType.DISPOSABLE_EMAIL, 10);
        }
        
        // Check if it's a spam trap
        if (isSpamTrap(email)) {
            return new ValidationResult(false, "Spam trap detected", ValidationType.SPAM_TRAP, 0);
        }
        
        // Check MX records (skip for now to avoid blocking valid emails)
        // if (!hasValidMxRecords(domain)) {
        //     return new ValidationResult(false, "No valid MX records found", ValidationType.NO_MX_RECORDS, 20);
        // }
        
        // Get reputation score
        int reputation = getEmailReputation(email);
        
        // Determine if email is valid based on reputation (lowered threshold for testing)
        boolean valid = reputation >= 30;
        String reason = valid ? "Valid email" : "Low reputation score: " + reputation;
        ValidationType type = valid ? ValidationType.VALID : ValidationType.UNKNOWN;
        
        return new ValidationResult(valid, reason, type, reputation);
    }
    
    @Override
    public Map<String, ValidationResult> validateEmails(List<String> emails) {
        Map<String, ValidationResult> results = new ConcurrentHashMap<>();
        
        // Validate emails in parallel for better performance
        List<CompletableFuture<Void>> futures = emails.stream()
            .map(email -> CompletableFuture.runAsync(() -> {
                ValidationResult result = validateEmail(email);
                results.put(email, result);
            }))
            .toList();
        
        // Wait for all validations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        return results;
    }
    
    @Override
    public boolean hasValidMxRecords(String domain) {
        // Check cache first
        if (mxRecordCache.containsKey(domain)) {
            return mxRecordCache.get(domain);
        }
        
        try {
            DirContext context = new InitialDirContext();
            Attributes attributes = context.getAttributes(domain, new String[]{"MX"});
            Attribute mxAttribute = attributes.get("MX");
            
            boolean hasMx = mxAttribute != null && mxAttribute.size() > 0;
            mxRecordCache.put(domain, hasMx);
            
            log.debug("MX records check for {}: {}", domain, hasMx);
            return hasMx;
            
        } catch (NamingException e) {
            log.warn("Failed to check MX records for domain {}: {}", domain, e.getMessage());
            mxRecordCache.put(domain, false);
            return false;
        }
    }
    
    @Override
    public boolean isDisposableEmail(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        return DISPOSABLE_DOMAINS.contains(domain);
    }
    
    @Override
    public boolean isSpamTrap(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        return SPAM_TRAP_DOMAINS.contains(domain);
    }
    
    @Override
    public int getEmailReputation(String email) {
        // Check cache first
        if (reputationCache.containsKey(email)) {
            return reputationCache.get(email);
        }
        
        int score = 100; // Start with perfect score
        
        String domain = email.substring(email.indexOf('@') + 1);
        
        // Deduct points for various factors
        if (isDisposableEmail(email)) {
            score -= 90;
        }
        
        if (isSpamTrap(email)) {
            score -= 100;
        }
        
        // Skip MX record check for now
        // if (!hasValidMxRecords(domain)) {
        //     score -= 80;
        // }
        
        // Check for suspicious patterns
        if (email.contains("+") && email.contains("@")) {
            score -= 10; // Email aliases might be less reliable
        }
        
        if (domain.length() > 20) {
            score -= 5; // Very long domains might be suspicious
        }
        
        // Check for common spam patterns
        if (email.matches(".*[0-9]{4,}.*")) {
            score -= 15; // Many numbers in email
        }
        
        if (email.contains("..") || email.contains("--")) {
            score -= 20; // Double dots or dashes
        }
        
        // Ensure score is between 0 and 100
        score = Math.max(0, Math.min(100, score));
        
        reputationCache.put(email, score);
        return score;
    }
}
