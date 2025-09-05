package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.DashboardStatsDto;
import in.osop.messaging_platform.dto.OverviewMetricsDto;
import in.osop.messaging_platform.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Analytics API", description = "APIs for analytics and dashboard data")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Get real-time dashboard statistics from database")
    @ApiResponse(responseCode = "200", description = "Dashboard stats retrieved successfully")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching dashboard stats from database");
        DashboardStatsDto stats = analyticsService.getDashboardStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/overview")
    @Operation(summary = "Get overview metrics", description = "Get email performance overview metrics")
    @ApiResponse(responseCode = "200", description = "Overview metrics retrieved successfully")
    public ResponseEntity<OverviewMetricsDto> getOverviewMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching overview metrics from database");
        OverviewMetricsDto metrics = analyticsService.getOverviewMetrics(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/engagement")
    @Operation(summary = "Get engagement metrics", description = "Get email engagement metrics")
    @ApiResponse(responseCode = "200", description = "Engagement metrics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getEngagementMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching engagement metrics from database");
        Map<String, Object> metrics = analyticsService.getEngagementMetrics(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/campaigns")
    @Operation(summary = "Get campaign performance", description = "Get campaign performance analytics")
    @ApiResponse(responseCode = "200", description = "Campaign performance retrieved successfully")
    public ResponseEntity<Map<String, Object>> getCampaignPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching campaign performance from database");
        Map<String, Object> performance = analyticsService.getCampaignPerformance(startDate, endDate);
        return ResponseEntity.ok(performance);
    }
    
    @GetMapping("/subscribers")
    @Operation(summary = "Get subscriber analytics", description = "Get subscriber analytics and statistics")
    @ApiResponse(responseCode = "200", description = "Subscriber analytics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getSubscriberAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching subscriber analytics from database");
        Map<String, Object> analytics = analyticsService.getSubscriberAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/realtime/dashboard")
    @Operation(summary = "Get real-time dashboard stats", description = "Get real-time dashboard statistics")
    @ApiResponse(responseCode = "200", description = "Real-time dashboard stats retrieved successfully")
    public ResponseEntity<DashboardStatsDto> getRealTimeDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching real-time dashboard stats from database");
        DashboardStatsDto stats = analyticsService.getDashboardStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}