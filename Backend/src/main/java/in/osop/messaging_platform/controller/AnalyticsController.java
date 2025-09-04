package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics API", description = "APIs for email analytics and reporting")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Get key metrics for the dashboard")
    @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching dashboard statistics from {} to {}", startDate, endDate);
        Map<String, Object> stats = analyticsService.getDashboardStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/overview")
    @Operation(summary = "Get overview metrics", description = "Get overview email performance metrics")
    @ApiResponse(responseCode = "200", description = "Overview metrics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getOverviewMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching overview metrics from {} to {}", startDate, endDate);
        Map<String, Object> metrics = analyticsService.getOverviewMetrics(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/engagement")
    @Operation(summary = "Get engagement metrics", description = "Get email engagement performance metrics")
    @ApiResponse(responseCode = "200", description = "Engagement metrics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getEngagementMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching engagement metrics from {} to {}", startDate, endDate);
        Map<String, Object> metrics = analyticsService.getEngagementMetrics(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/campaigns")
    @Operation(summary = "Get campaign performance", description = "Get performance metrics for all campaigns")
    @ApiResponse(responseCode = "200", description = "Campaign performance retrieved successfully")
    public ResponseEntity<Map<String, Object>> getCampaignPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching campaign performance from {} to {}", startDate, endDate);
        Map<String, Object> performance = analyticsService.getCampaignPerformance(startDate, endDate);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/subscribers")
    @Operation(summary = "Get subscriber analytics", description = "Get subscriber growth and engagement analytics")
    @ApiResponse(responseCode = "200", description = "Subscriber analytics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getSubscriberAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching subscriber analytics from {} to {}", startDate, endDate);
        Map<String, Object> analytics = analyticsService.getSubscriberAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/trends")
    @Operation(summary = "Get performance trends", description = "Get email performance trends over time")
    @ApiResponse(responseCode = "200", description = "Performance trends retrieved successfully")
    public ResponseEntity<Map<String, Object>> getPerformanceTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "daily") String granularity) {
        log.info("Fetching performance trends from {} to {} with granularity {}", startDate, endDate, granularity);
        Map<String, Object> trends = analyticsService.getPerformanceTrends(startDate, endDate, granularity);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/geographic")
    @Operation(summary = "Get geographic analytics", description = "Get email performance by geographic location")
    @ApiResponse(responseCode = "200", description = "Geographic analytics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getGeographicAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching geographic analytics from {} to {}", startDate, endDate);
        Map<String, Object> analytics = analyticsService.getGeographicAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/device")
    @Operation(summary = "Get device analytics", description = "Get email performance by device type")
    @ApiResponse(responseCode = "200", description = "Device analytics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getDeviceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching device analytics from {} to {}", startDate, endDate);
        Map<String, Object> analytics = analyticsService.getDeviceAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/browser")
    @Operation(summary = "Get browser analytics", description = "Get email performance by browser type")
    @ApiResponse(responseCode = "200", description = "Browser analytics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getBrowserAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching browser analytics from {} to {}", startDate, endDate);
        Map<String, Object> analytics = analyticsService.getBrowserAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/time")
    @Operation(summary = "Get time-based analytics", description = "Get email performance by time of day and day of week")
    @ApiResponse(responseCode = "200", description = "Time-based analytics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getTimeBasedAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching time-based analytics from {} to {}", startDate, endDate);
        Map<String, Object> analytics = analyticsService.getTimeBasedAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/export")
    @Operation(summary = "Export analytics data", description = "Export analytics data in various formats")
    @ApiResponse(responseCode = "200", description = "Analytics data exported successfully")
    public ResponseEntity<Map<String, Object>> exportAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "json") String format) {
        log.info("Exporting analytics data from {} to {} in {} format", startDate, endDate, format);
        Map<String, Object> export = analyticsService.exportAnalytics(startDate, endDate, format);
        return ResponseEntity.ok(export);
    }

    @GetMapping("/realtime")
    @Operation(summary = "Get real-time analytics", description = "Get real-time email performance metrics")
    @ApiResponse(responseCode = "200", description = "Real-time analytics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getRealTimeAnalytics() {
        log.info("Fetching real-time analytics");
        Map<String, Object> analytics = analyticsService.getRealTimeAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
