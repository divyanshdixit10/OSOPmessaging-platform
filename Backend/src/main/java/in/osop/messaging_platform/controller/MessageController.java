package in.osop.messaging_platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.osop.messaging_platform.dto.MessageRequest;
import in.osop.messaging_platform.dto.MessageResponse;
import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.service.MessageService;
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
} 