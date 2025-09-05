package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.service.EmailTrackingService;
import in.osop.messaging_platform.service.EmailDeliveryTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EmailTrackingController {

    private final EmailTrackingService emailTrackingService;
    private final EmailDeliveryTrackingService emailDeliveryTrackingService;

    @GetMapping(value = "/open/{encodedData}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> trackEmailOpen(@PathVariable String encodedData, HttpServletRequest request) {
        try {
            // Decode the tracking data
            String decodedData = new String(Base64.getDecoder().decode(encodedData));
            String[] parts = decodedData.split("\\|");
            
            if (parts.length >= 2) {
                Long emailEventId = Long.parseLong(parts[0]);
                String email = parts[1];
                
                // Get client IP and User Agent
                String ipAddress = getClientIpAddress(request);
                String userAgent = request.getHeader("User-Agent");
                
                            // Track the email open
            emailTrackingService.trackEmailOpenedByEventId(emailEventId, email, ipAddress, userAgent);
            
            // Also track with delivery tracking service
            emailDeliveryTrackingService.trackEmailOpen(emailEventId, ipAddress, userAgent);
                
                log.info("Email opened tracked for: {} from IP: {}", email, ipAddress);
            }
            
            // Return a 1x1 transparent PNG pixel
            byte[] pixel = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(pixel);
                    
        } catch (Exception e) {
            log.error("Error tracking email open: {}", e.getMessage());
            // Still return the pixel even if tracking fails
            byte[] pixel = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(pixel);
        }
    }

    @GetMapping("/click/{encodedData}")
    public ResponseEntity<String> trackEmailClick(@PathVariable String encodedData, HttpServletRequest request) {
        try {
            // Decode the tracking data
            String decodedData = new String(Base64.getDecoder().decode(encodedData));
            String[] parts = decodedData.split("\\|");
            
            if (parts.length >= 3) {
                Long emailEventId = Long.parseLong(parts[0]);
                String email = parts[1];
                String originalUrl = parts[2];
                
                // Get client IP and User Agent
                String ipAddress = getClientIpAddress(request);
                String userAgent = request.getHeader("User-Agent");
                
                            // Track the email click
            emailTrackingService.trackEmailClickedByEventId(emailEventId, email, originalUrl, ipAddress, userAgent);
            
            // Also track with delivery tracking service
            emailDeliveryTrackingService.trackEmailClick(emailEventId, originalUrl, ipAddress, userAgent);
                
                log.info("Email click tracked for: {} to URL: {} from IP: {}", email, originalUrl, ipAddress);
                
                // Redirect to the original URL
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", originalUrl)
                        .build();
            }
            
            return ResponseEntity.badRequest().body("Invalid tracking data");
            
        } catch (Exception e) {
            log.error("Error tracking email click: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Tracking error");
        }
    }

    @PostMapping("/unsubscribe/{encodedData}")
    public ResponseEntity<Map<String, String>> unsubscribe(@PathVariable String encodedData, HttpServletRequest request) {
        try {
            // Decode the tracking data
            String decodedData = new String(Base64.getDecoder().decode(encodedData));
            String[] parts = decodedData.split("\\|");
            
            if (parts.length >= 2) {
                Long emailEventId = Long.parseLong(parts[0]);
                String email = parts[1];
                
                // Get client IP and User Agent
                String ipAddress = getClientIpAddress(request);
                String userAgent = request.getHeader("User-Agent");
                
                // Track the unsubscribe
                emailTrackingService.trackEmailUnsubscribedByEventId(emailEventId, email, ipAddress, userAgent);
                
                log.info("Unsubscribe tracked for: {} from IP: {}", email, ipAddress);
                
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "You have been successfully unsubscribed"
                ));
            }
            
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Invalid unsubscribe data"
            ));
            
        } catch (Exception e) {
            log.error("Error processing unsubscribe: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Unsubscribe error"
            ));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
