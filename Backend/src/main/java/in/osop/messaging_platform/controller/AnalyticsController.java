package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.*;
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
import java.util.List;
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
    
    // New endpoints as per requirements
    
    @GetMapping("/stats")
    @Operation(summary = "Get live stats", description = "Get live statistics for dashboard cards")
    @ApiResponse(responseCode = "200", description = "Live stats retrieved successfully")
    public ResponseEntity<LiveStatsDto> getLiveStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching live stats from database");
        LiveStatsDto stats = analyticsService.getLiveStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/campaigns")
    @Operation(summary = "Get campaign analytics", description = "Get campaign performance analytics with progress bars")
    @ApiResponse(responseCode = "200", description = "Campaign analytics retrieved successfully")
    public ResponseEntity<List<CampaignAnalyticsDto>> getCampaignAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching campaign analytics from database");
        List<CampaignAnalyticsDto> analytics = analyticsService.getCampaignAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/templates")
    @Operation(summary = "Get template analytics", description = "Get template usage and performance analytics")
    @ApiResponse(responseCode = "200", description = "Template analytics retrieved successfully")
    public ResponseEntity<List<TemplateAnalyticsDto>> getTemplateAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching template analytics from database");
        List<TemplateAnalyticsDto> analytics = analyticsService.getTemplateAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recent activities", description = "Get recent activity feed")
    @ApiResponse(responseCode = "200", description = "Recent activities retrieved successfully")
    public ResponseEntity<List<RecentActivityDto>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Fetching recent activities from database");
        List<RecentActivityDto> activities = analyticsService.getRecentActivities(limit);
        return ResponseEntity.ok(activities);
    }
}