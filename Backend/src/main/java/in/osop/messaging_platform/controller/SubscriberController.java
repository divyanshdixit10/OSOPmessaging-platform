package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.SubscriberDto;
import in.osop.messaging_platform.service.SubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscriber API", description = "APIs for managing email subscribers")
@CrossOrigin(origins = "http://localhost:3000")
public class SubscriberController {

    private final SubscriberService subscriberService;

    @PostMapping
    @Operation(summary = "Add a new subscriber", description = "Add a new email subscriber")
    @ApiResponse(responseCode = "201", description = "Subscriber added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Subscriber with this email already exists")
    public ResponseEntity<SubscriberDto> addSubscriber(@Valid @RequestBody SubscriberDto subscriberDto) {
        log.info("Adding new subscriber: {}", subscriberDto.getEmail());
        SubscriberDto created = subscriberService.addSubscriber(subscriberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Add multiple subscribers", description = "Add multiple email subscribers in bulk")
    @ApiResponse(responseCode = "201", description = "Subscribers added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<Map<String, Object>> addBulkSubscribers(@RequestBody List<SubscriberDto> subscribers) {
        log.info("Adding {} subscribers in bulk", subscribers.size());
        Map<String, Object> result = subscriberService.addBulkSubscribers(subscribers);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscriber by ID", description = "Retrieve a subscriber by their ID")
    @ApiResponse(responseCode = "200", description = "Subscriber found")
    @ApiResponse(responseCode = "404", description = "Subscriber not found")
    public ResponseEntity<SubscriberDto> getSubscriber(@PathVariable Long id) {
        log.info("Fetching subscriber with ID: {}", id);
        SubscriberDto subscriber = subscriberService.getSubscriberById(id);
        return ResponseEntity.ok(subscriber);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get subscriber by email", description = "Retrieve a subscriber by their email address")
    @ApiResponse(responseCode = "200", description = "Subscriber found")
    @ApiResponse(responseCode = "404", description = "Subscriber not found")
    public ResponseEntity<SubscriberDto> getSubscriberByEmail(@PathVariable String email) {
        log.info("Fetching subscriber with email: {}", email);
        SubscriberDto subscriber = subscriberService.getSubscriberByEmail(email);
        return ResponseEntity.ok(subscriber);
    }

    @GetMapping
    @Operation(summary = "Get all subscribers", description = "Retrieve all subscribers with optional filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Subscribers retrieved successfully")
    public ResponseEntity<Page<SubscriberDto>> getAllSubscribers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isVerified,
            Pageable pageable) {
        log.info("Fetching subscribers with filters: email={}, firstName={}, lastName={}, status={}, isVerified={}", 
                email, firstName, lastName, status, isVerified);
        Page<SubscriberDto> subscribers = subscriberService.getSubscribers(email, firstName, lastName, status, isVerified, pageable);
        return ResponseEntity.ok(subscribers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscriber", description = "Update an existing subscriber")
    @ApiResponse(responseCode = "200", description = "Subscriber updated successfully")
    @ApiResponse(responseCode = "404", description = "Subscriber not found")
    public ResponseEntity<SubscriberDto> updateSubscriber(
            @PathVariable Long id,
            @Valid @RequestBody SubscriberDto subscriberDto) {
        log.info("Updating subscriber with ID: {}", id);
        SubscriberDto updated = subscriberService.updateSubscriber(id, subscriberDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscriber", description = "Delete a subscriber by ID")
    @ApiResponse(responseCode = "204", description = "Subscriber deleted successfully")
    @ApiResponse(responseCode = "404", description = "Subscriber not found")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) {
        log.info("Deleting subscriber with ID: {}", id);
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify subscriber", description = "Verify a subscriber's email address")
    @ApiResponse(responseCode = "200", description = "Subscriber verified successfully")
    @ApiResponse(responseCode = "404", description = "Subscriber not found")
    @ApiResponse(responseCode = "400", description = "Invalid verification token")
    public ResponseEntity<SubscriberDto> verifySubscriber(
            @PathVariable Long id,
            @RequestParam String token) {
        log.info("Verifying subscriber with ID: {} using token", id);
        SubscriberDto verified = subscriberService.verifySubscriber(id, token);
        return ResponseEntity.ok(verified);
    }

    @PostMapping("/{id}/unsubscribe")
    @Operation(summary = "Unsubscribe subscriber", description = "Unsubscribe a subscriber from emails")
    @ApiResponse(responseCode = "200", description = "Subscriber unsubscribed successfully")
    @ApiResponse(responseCode = "404", description = "Subscriber not found")
    public ResponseEntity<SubscriberDto> unsubscribeSubscriber(@PathVariable Long id) {
        log.info("Unsubscribing subscriber with ID: {}", id);
        SubscriberDto unsubscribed = subscriberService.unsubscribeSubscriber(id);
        return ResponseEntity.ok(unsubscribed);
    }

    @PostMapping("/{id}/resubscribe")
    @Operation(summary = "Resubscribe subscriber", description = "Resubscribe a previously unsubscribed subscriber")
    @ApiResponse(responseCode = "200", description = "Subscriber resubscribed successfully")
    @ApiResponse(responseCode = "404", description = "Subscriber not found")
    public ResponseEntity<SubscriberDto> resubscribeSubscriber(@PathVariable Long id) {
        log.info("Resubscribing subscriber with ID: {}", id);
        SubscriberDto resubscribed = subscriberService.resubscribeSubscriber(id);
        return ResponseEntity.ok(resubscribed);
    }

    @GetMapping("/stats/overview")
    @Operation(summary = "Get subscriber statistics", description = "Get overview statistics for subscribers")
    @ApiResponse(responseCode = "200", description = "Subscriber statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getSubscriberStats() {
        log.info("Fetching subscriber statistics");
        Map<String, Object> stats = subscriberService.getSubscriberStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/status/{status}")
    @Operation(summary = "Get subscribers by status", description = "Get count of subscribers with a specific status")
    @ApiResponse(responseCode = "200", description = "Status count retrieved successfully")
    public ResponseEntity<Map<String, Long>> getSubscriberCountByStatus(@PathVariable String status) {
        log.info("Fetching subscriber count for status: {}", status);
        long count = subscriberService.getSubscriberCountByStatus(status);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/engaged")
    @Operation(summary = "Get engaged subscribers", description = "Get subscribers who have engaged with emails recently")
    @ApiResponse(responseCode = "200", description = "Engaged subscribers retrieved successfully")
    public ResponseEntity<List<SubscriberDto>> getEngagedSubscribers(
            @RequestParam(defaultValue = "30") int days) {
        log.info("Fetching engaged subscribers from last {} days", days);
        List<SubscriberDto> subscribers = subscriberService.getEngagedSubscribers(days);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive subscribers", description = "Get subscribers who haven't engaged recently")
    @ApiResponse(responseCode = "200", description = "Inactive subscribers retrieved successfully")
    public ResponseEntity<List<SubscriberDto>> getInactiveSubscribers(
            @RequestParam(defaultValue = "90") int days) {
        log.info("Fetching inactive subscribers from last {} days", days);
        List<SubscriberDto> subscribers = subscriberService.getInactiveSubscribers(days);
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/sources")
    @Operation(summary = "Get subscriber sources", description = "Get all available subscriber sources")
    @ApiResponse(responseCode = "200", description = "Subscriber sources retrieved successfully")
    public ResponseEntity<List<String>> getAllSubscriberSources() {
        log.info("Fetching all subscriber sources");
        List<String> sources = subscriberService.getAllSubscriberSources();
        return ResponseEntity.ok(sources);
    }

    @PostMapping("/import")
    @Operation(summary = "Import subscribers", description = "Import subscribers from CSV or other formats")
    @ApiResponse(responseCode = "201", description = "Subscribers imported successfully")
    @ApiResponse(responseCode = "400", description = "Invalid import data")
    public ResponseEntity<Map<String, Object>> importSubscribers(
            @RequestParam("file") String fileContent,
            @RequestParam(defaultValue = "csv") String format) {
        log.info("Importing subscribers from {} format", format);
        Map<String, Object> result = subscriberService.importSubscribers(fileContent, format);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/export")
    @Operation(summary = "Export subscribers", description = "Export subscribers to various formats")
    @ApiResponse(responseCode = "200", description = "Subscribers exported successfully")
    public ResponseEntity<Map<String, Object>> exportSubscribers(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String status) {
        log.info("Exporting subscribers in {} format with status filter: {}", format, status);
        Map<String, Object> result = subscriberService.exportSubscribers(format, status);
        return ResponseEntity.ok(result);
    }
}
