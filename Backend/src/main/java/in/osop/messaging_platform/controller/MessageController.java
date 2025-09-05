package in.osop.messaging_platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.model.EmailEventType;
import in.osop.messaging_platform.service.MessageService;
import in.osop.messaging_platform.service.EmailTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message API", description = "APIs for sending messages via different channels")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from React app
public class MessageController {

    private final MessageService messageService;
    private final EmailTrackingService emailTrackingService;
    private final ObjectMapper objectMapper;
    
    @PostMapping("/send")
    @Operation(summary = "Send a message", description = "Send a message via Email, WhatsApp, or SMS")
    @ApiResponse(responseCode = "200", description = "Message sent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        log.info("Received message request for channel: {}", request.getChannel());
        MessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/send-with-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send a message with attachments", description = "Send a message with multiple file attachments")
    @ApiResponse(responseCode = "200", description = "Message sent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<MessageResponse> sendMessageWithAttachment(
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestPart("channel") String channel,
            @RequestPart("recipients") String recipients,
            @RequestPart(value = "subject", required = false) String subject,
            @RequestPart("message") String message,
            @RequestPart(value = "mediaUrls", required = false) String mediaUrls) {
        
        log.info("Received message request with channel: {}, recipients: {}, subject: {}, mediaUrls: {}, attachments: {}",
                channel, recipients, subject, mediaUrls, attachments != null ? attachments.size() : 0);
        
        // Create a request object from parts
        MessageRequest request = MessageRequest.builder()
                .channel(MessageChannel.valueOf(channel))
                .recipients(Arrays.asList(recipients.split(",")))
                .subject(subject)
                .message(message)
                .attachments(attachments)
                .mediaUrls(mediaUrls != null ? Arrays.asList(mediaUrls.split(",")) : null)
                .build();
        
        // Send message
        MessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/email/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send bulk email", description = "Send bulk email with template, tracking, and attachments")
    @ApiResponse(responseCode = "200", description = "Email sent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<MessageResponse> sendBulkEmail(
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestPart("templateId") String templateId,
            @RequestPart("subject") String subject,
            @RequestPart("body") String body,
            @RequestPart("recipients") String recipientsJson,
            @RequestPart(value = "mediaUrls", required = false) String mediaUrlsJson,
            @RequestPart("trackOpens") String trackOpens,
            @RequestPart("trackClicks") String trackClicks,
            @RequestPart("addUnsubscribeLink") String addUnsubscribeLink) {
        
        try {
            // Parse JSON strings
            List<String> recipients = objectMapper.readValue(recipientsJson, new TypeReference<List<String>>() {});
            List<String> mediaUrls = mediaUrlsJson != null ? 
                objectMapper.readValue(mediaUrlsJson, new TypeReference<List<String>>() {}) : null;
            
            log.info("Received bulk email request with templateId: {}, recipients: {}, mediaUrls: {}, attachments: {}, tracking: opens={}, clicks={}, unsubscribe={}",
                    templateId, recipients.size(), mediaUrls, attachments != null ? attachments.size() : 0,
                    trackOpens, trackClicks, addUnsubscribeLink);
            
            // Create a request object from parts
            MessageRequest request = MessageRequest.builder()
                    .channel(MessageChannel.EMAIL)
                    .recipients(recipients)
                    .subject(subject)
                    .message(body)
                    .attachments(attachments)
                    .mediaUrls(mediaUrls)
                    .templateId(templateId)
                    .trackOpens(Boolean.parseBoolean(trackOpens))
                    .trackClicks(Boolean.parseBoolean(trackClicks))
                    .addUnsubscribeLink(Boolean.parseBoolean(addUnsubscribeLink))
                    .build();
            
            // Send message
            MessageResponse response = messageService.sendMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to parse request data: {}", e.getMessage());
            throw new RuntimeException("Failed to process bulk email request: " + e.getMessage());
        }
    }

    // Email Tracking Endpoints
    @GetMapping("/track/open/{campaignId}")
    @Operation(summary = "Track email open", description = "Track when an email is opened")
    public ResponseEntity<String> trackEmailOpen(
            @PathVariable Long campaignId,
            @RequestParam String email,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String userAgent) {
        try {
            log.info("Tracking email open for campaign {} and email {}", campaignId, email);
            emailTrackingService.trackEmailOpened(campaignId, email, ip, userAgent);
            return ResponseEntity.ok("Email open tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking email open: {}", e.getMessage());
            return ResponseEntity.ok("Tracking failed but continuing"); // Return 200 to avoid breaking email
        }
    }

    @GetMapping("/track/click/{campaignId}")
    @Operation(summary = "Track email click", description = "Track when a link in an email is clicked")
    public ResponseEntity<String> trackEmailClick(
            @PathVariable Long campaignId,
            @RequestParam String email,
            @RequestParam(required = false) String link,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String userAgent) {
        try {
            log.info("Tracking email click for campaign {} and email {}", campaignId, email);
            emailTrackingService.trackEmailClicked(campaignId, email, link, ip, userAgent);
            return ResponseEntity.ok("Email click tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking email click: {}", e.getMessage());
            return ResponseEntity.ok("Tracking failed but continuing");
        }
    }

    @PostMapping("/track/delivered/{campaignId}")
    @Operation(summary = "Track email delivered", description = "Track when an email is delivered")
    public ResponseEntity<String> trackEmailDelivered(
            @PathVariable Long campaignId,
            @RequestParam String email) {
        try {
            log.info("Tracking email delivered for campaign {} and email {}", campaignId, email);
            emailTrackingService.trackEmailDelivered(campaignId, email);
            return ResponseEntity.ok("Email delivered tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking email delivered: {}", e.getMessage());
            return ResponseEntity.ok("Tracking failed but continuing");
        }
    }

    @PostMapping("/track/bounced/{campaignId}")
    @Operation(summary = "Track email bounced", description = "Track when an email bounces")
    public ResponseEntity<String> trackEmailBounced(
            @PathVariable Long campaignId,
            @RequestParam String email,
            @RequestParam(required = false) String reason) {
        try {
            log.info("Tracking email bounced for campaign {} and email {}", campaignId, email);
            emailTrackingService.trackEmailBounced(campaignId, email, reason);
            return ResponseEntity.ok("Email bounced tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking email bounced: {}", e.getMessage());
            return ResponseEntity.ok("Tracking failed but continuing");
        }
    }

    @PostMapping("/track/unsubscribed/{campaignId}")
    @Operation(summary = "Track email unsubscribed", description = "Track when a user unsubscribes")
    public ResponseEntity<String> trackEmailUnsubscribed(
            @PathVariable Long campaignId,
            @RequestParam String email,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String userAgent) {
        try {
            log.info("Tracking email unsubscribed for campaign {} and email {}", campaignId, email);
            emailTrackingService.trackEmailUnsubscribed(campaignId, email, ip, userAgent);
            return ResponseEntity.ok("Email unsubscribed tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking email unsubscribed: {}", e.getMessage());
            return ResponseEntity.ok("Tracking failed but continuing");
        }
    }
} 