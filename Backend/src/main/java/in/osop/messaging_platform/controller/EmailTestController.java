package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.service.EmailValidationService;
import in.osop.messaging_platform.service.EmailDeliveryTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EmailTestController {
    
    private final EmailValidationService emailValidationService;
    private final EmailDeliveryTrackingService emailDeliveryTrackingService;
    
    @PostMapping("/validate-email")
    public ResponseEntity<Map<String, Object>> testEmailValidation(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        
        try {
            EmailValidationService.ValidationResult result = emailValidationService.validateEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("valid", result.isValid());
            response.put("reason", result.getReason());
            response.put("type", result.getType().name());
            response.put("reputationScore", result.getReputationScore());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to validate email {}: {}", email, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/validate-emails")
    public ResponseEntity<Map<String, Object>> testBulkEmailValidation(@RequestBody Map<String, List<String>> request) {
        List<String> emails = request.get("emails");
        
        if (emails == null || emails.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Emails list is required"));
        }
        
        try {
            Map<String, EmailValidationService.ValidationResult> results = 
                emailValidationService.validateEmails(emails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("results", results);
            response.put("totalEmails", emails.size());
            response.put("validEmails", results.values().stream().mapToInt(r -> r.isValid() ? 1 : 0).sum());
            response.put("invalidEmails", results.values().stream().mapToInt(r -> r.isValid() ? 0 : 1).sum());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to validate emails: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/delivery-stats/{campaignId}")
    public ResponseEntity<EmailDeliveryTrackingService.DeliveryStatistics> getDeliveryStats(@PathVariable Long campaignId) {
        try {
            EmailDeliveryTrackingService.DeliveryStatistics stats = 
                emailDeliveryTrackingService.getDeliveryStatistics(campaignId);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Failed to get delivery stats for campaign {}: {}", campaignId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/bounced-emails")
    public ResponseEntity<List<String>> getBouncedEmails(@RequestParam(defaultValue = "24") int hours) {
        try {
            List<String> bouncedEmails = emailDeliveryTrackingService.getBouncedEmails(hours);
            return ResponseEntity.ok(bouncedEmails);
            
        } catch (Exception e) {
            log.error("Failed to get bounced emails: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/simulate-email-send")
    public ResponseEntity<Map<String, Object>> simulateEmailSend(@RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            String subject = (String) request.get("subject");
            String message = (String) request.get("message");
            
            if (email == null || subject == null || message == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email, subject, and message are required"));
            }
            
            // Simulate email validation
            EmailValidationService.ValidationResult validation = emailValidationService.validateEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("subject", subject);
            response.put("validation", Map.of(
                "valid", validation.isValid(),
                "reason", validation.getReason(),
                "reputationScore", validation.getReputationScore()
            ));
            response.put("simulated", true);
            response.put("timestamp", System.currentTimeMillis());
            
            if (!validation.isValid()) {
                response.put("status", "FAILED");
                response.put("error", "Email validation failed: " + validation.getReason());
            } else {
                response.put("status", "SUCCESS");
                response.put("message", "Email would be sent successfully");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to simulate email send: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
