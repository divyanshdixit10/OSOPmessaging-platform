package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.CampaignDto;
import in.osop.messaging_platform.service.CampaignService;
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
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Campaign API", description = "APIs for managing email campaigns")
@CrossOrigin(origins = "http://localhost:3000")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @Operation(summary = "Create a new campaign", description = "Create a new email campaign with the provided details")
    @ApiResponse(responseCode = "201", description = "Campaign created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<CampaignDto> createCampaign(@Valid @RequestBody CampaignDto campaignDto) {
        log.info("Creating new campaign: {}", campaignDto.getName());
        CampaignDto created = campaignService.createCampaign(campaignDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get campaign by ID", description = "Retrieve a campaign by its ID")
    @ApiResponse(responseCode = "200", description = "Campaign found")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    public ResponseEntity<CampaignDto> getCampaign(@PathVariable Long id) {
        log.info("Fetching campaign with ID: {}", id);
        CampaignDto campaign = campaignService.getCampaignById(id);
        return ResponseEntity.ok(campaign);
    }

    @GetMapping
    @Operation(summary = "Get all campaigns", description = "Retrieve all campaigns with optional filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Campaigns retrieved successfully")
    public ResponseEntity<Page<CampaignDto>> getAllCampaigns(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) Boolean isDraft,
            Pageable pageable) {
        log.info("Fetching campaigns with filters: name={}, status={}, channel={}, isDraft={}", name, status, channel, isDraft);
        Page<CampaignDto> campaigns = campaignService.getCampaigns(name, status, channel, isDraft, pageable);
        return ResponseEntity.ok(campaigns);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update campaign", description = "Update an existing campaign")
    @ApiResponse(responseCode = "200", description = "Campaign updated successfully")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    public ResponseEntity<CampaignDto> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody CampaignDto campaignDto) {
        log.info("Updating campaign with ID: {}", id);
        CampaignDto updated = campaignService.updateCampaign(id, campaignDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete campaign", description = "Delete a campaign by ID")
    @ApiResponse(responseCode = "204", description = "Campaign deleted successfully")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        log.info("Deleting campaign with ID: {}", id);
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start campaign", description = "Start a scheduled or draft campaign")
    @ApiResponse(responseCode = "200", description = "Campaign started successfully")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    @ApiResponse(responseCode = "400", description = "Campaign cannot be started")
    public ResponseEntity<CampaignDto> startCampaign(@PathVariable Long id) {
        log.info("Starting campaign with ID: {}", id);
        CampaignDto started = campaignService.startCampaign(id);
        return ResponseEntity.ok(started);
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "Pause campaign", description = "Pause a running campaign")
    @ApiResponse(responseCode = "200", description = "Campaign paused successfully")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    @ApiResponse(responseCode = "400", description = "Campaign cannot be paused")
    public ResponseEntity<CampaignDto> pauseCampaign(@PathVariable Long id) {
        log.info("Pausing campaign with ID: {}", id);
        CampaignDto paused = campaignService.pauseCampaign(id);
        return ResponseEntity.ok(paused);
    }

    @PostMapping("/{id}/resume")
    @Operation(summary = "Resume campaign", description = "Resume a paused campaign")
    @ApiResponse(responseCode = "200", description = "Campaign resumed successfully")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    @ApiResponse(responseCode = "400", description = "Campaign cannot be resumed")
    public ResponseEntity<CampaignDto> resumeCampaign(@PathVariable Long id) {
        log.info("Resuming campaign with ID: {}", id);
        CampaignDto resumed = campaignService.resumeCampaign(id);
        return ResponseEntity.ok(resumed);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel campaign", description = "Cancel a scheduled or running campaign")
    @ApiResponse(responseCode = "200", description = "Campaign cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    @ApiResponse(responseCode = "400", description = "Campaign cannot be cancelled")
    public ResponseEntity<CampaignDto> cancelCampaign(@PathVariable Long id) {
        log.info("Cancelling campaign with ID: {}", id);
        CampaignDto cancelled = campaignService.cancelCampaign(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get campaign statistics", description = "Get detailed statistics for a campaign")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Campaign not found")
    public ResponseEntity<Map<String, Object>> getCampaignStats(@PathVariable Long id) {
        log.info("Fetching statistics for campaign with ID: {}", id);
        Map<String, Object> stats = campaignService.getCampaignStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/overview")
    @Operation(summary = "Get overall campaign statistics", description = "Get overview statistics for all campaigns")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getOverallStats() {
        log.info("Fetching overall campaign statistics");
        Map<String, Object> stats = campaignService.getOverallStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get campaigns by status", description = "Get all campaigns with a specific status")
    @ApiResponse(responseCode = "200", description = "Campaigns retrieved successfully")
    public ResponseEntity<List<CampaignDto>> getCampaignsByStatus(@PathVariable String status) {
        log.info("Fetching campaigns with status: {}", status);
        List<CampaignDto> campaigns = campaignService.getCampaignsByStatus(status);
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/scheduled")
    @Operation(summary = "Get scheduled campaigns", description = "Get all campaigns that are scheduled to run")
    @ApiResponse(responseCode = "200", description = "Scheduled campaigns retrieved successfully")
    public ResponseEntity<List<CampaignDto>> getScheduledCampaigns() {
        log.info("Fetching scheduled campaigns");
        List<CampaignDto> campaigns = campaignService.getScheduledCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/running")
    @Operation(summary = "Get running campaigns", description = "Get all campaigns that are currently running")
    @ApiResponse(responseCode = "200", description = "Running campaigns retrieved successfully")
    public ResponseEntity<List<CampaignDto>> getRunningCampaigns() {
        log.info("Fetching running campaigns");
        List<CampaignDto> campaigns = campaignService.getRunningCampaigns();
        return ResponseEntity.ok(campaigns);
    }
}
