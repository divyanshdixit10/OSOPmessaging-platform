package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.*;
import in.osop.messaging_platform.model.EmailTemplate;
import in.osop.messaging_platform.service.EnhancedTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates/enhanced")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enhanced Template API", description = "APIs for advanced template management with rich editor support")
@CrossOrigin(origins = "http://localhost:3000")
public class EnhancedTemplateController {
    
    private final EnhancedTemplateService templateService;
    
    @PostMapping
    @Operation(summary = "Create a new template", description = "Create a new email template with rich content")
    @ApiResponse(responseCode = "201", description = "Template created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<TemplateDto> createTemplate(
            @Valid @RequestBody CreateTemplateRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Creating new template: {} by user: {}", request.getName(), userId);
        TemplateDto created = templateService.createTemplate(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping
    @Operation(summary = "Get all templates", description = "Retrieve templates with filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    public ResponseEntity<Page<TemplateDto>> getTemplates(
            @Parameter(description = "Search term for name or description") 
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by category") 
            @RequestParam(required = false) EmailTemplate.TemplateCategory category,
            
            @Parameter(description = "Filter by type") 
            @RequestParam(required = false) EmailTemplate.TemplateType type,
            
            @Parameter(description = "Filter by active status") 
            @RequestParam(required = false) Boolean isActive,
            
            @Parameter(description = "Filter by creator") 
            @RequestParam(required = false) String createdBy,
            
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Fetching templates with filters: search={}, category={}, type={}, active={}, createdBy={}", 
                search, category, type, isActive, createdBy);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TemplateDto> templates = templateService.getTemplates(search, category, type, isActive, createdBy, pageable);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID", description = "Retrieve a specific template by its ID")
    @ApiResponse(responseCode = "200", description = "Template found")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<TemplateDto> getTemplate(@PathVariable Long id) {
        log.info("Fetching template with ID: {}", id);
        TemplateDto template = templateService.getTemplateById(id);
        return ResponseEntity.ok(template);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update template", description = "Update an existing template")
    @ApiResponse(responseCode = "200", description = "Template updated successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<TemplateDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTemplateRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Updating template with ID: {} by user: {}", id, userId);
        TemplateDto updated = templateService.updateTemplate(id, request, userId);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template", description = "Soft delete a template")
    @ApiResponse(responseCode = "204", description = "Template deleted successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Deleting template with ID: {} by user: {}", id, userId);
        templateService.deleteTemplate(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/clone")
    @Operation(summary = "Clone template", description = "Create a copy of an existing template")
    @ApiResponse(responseCode = "201", description = "Template cloned successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<TemplateDto> cloneTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Id") String userId) {
        
        String newName = request.get("name");
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New template name is required");
        }
        
        log.info("Cloning template with ID: {} to new name: {} by user: {}", id, newName, userId);
        TemplateDto cloned = templateService.cloneTemplate(id, newName, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cloned);
    }
    
    @GetMapping("/{id}/versions")
    @Operation(summary = "Get template versions", description = "Get version history for a template")
    @ApiResponse(responseCode = "200", description = "Versions retrieved successfully")
    public ResponseEntity<List<TemplateVersionDto>> getTemplateVersions(@PathVariable Long id) {
        log.info("Fetching versions for template ID: {}", id);
        List<TemplateVersionDto> versions = templateService.getTemplateVersions(id);
        return ResponseEntity.ok(versions);
    }
    
    @PostMapping("/{id}/revert/{versionNumber}")
    @Operation(summary = "Revert template to version", description = "Revert template to a specific version")
    @ApiResponse(responseCode = "200", description = "Template reverted successfully")
    @ApiResponse(responseCode = "404", description = "Template or version not found")
    public ResponseEntity<TemplateDto> revertToVersion(
            @PathVariable Long id,
            @PathVariable Integer versionNumber,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Reverting template ID: {} to version: {} by user: {}", id, versionNumber, userId);
        TemplateDto reverted = templateService.revertToVersion(id, versionNumber, userId);
        return ResponseEntity.ok(reverted);
    }
    
    @PostMapping("/import")
    @Operation(summary = "Import template", description = "Import a template from JSON/HTML")
    @ApiResponse(responseCode = "201", description = "Template imported successfully")
    @ApiResponse(responseCode = "400", description = "Invalid import data")
    public ResponseEntity<TemplateDto> importTemplate(
            @Valid @RequestBody TemplateImportRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Importing template: {} by user: {}", request.getName(), userId);
        TemplateDto imported = templateService.importTemplate(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(imported);
    }
    
    @GetMapping("/{id}/export")
    @Operation(summary = "Export template", description = "Export template as JSON")
    @ApiResponse(responseCode = "200", description = "Template exported successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<String> exportTemplate(@PathVariable Long id) {
        log.info("Exporting template with ID: {}", id);
        String exported = templateService.exportTemplate(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", "template_" + id + ".json");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(exported);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get template statistics", description = "Get overall template statistics")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<TemplateStatisticsDto> getTemplateStatistics() {
        log.info("Fetching template statistics");
        TemplateStatisticsDto stats = templateService.getTemplateStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get template categories", description = "Get all available template categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<String>> getTemplateCategories() {
        log.info("Fetching template categories");
        TemplateStatisticsDto stats = templateService.getTemplateStatistics();
        return ResponseEntity.ok(stats.getCategories());
    }
    
    @GetMapping("/types")
    @Operation(summary = "Get template types", description = "Get all available template types")
    @ApiResponse(responseCode = "200", description = "Types retrieved successfully")
    public ResponseEntity<List<String>> getTemplateTypes() {
        log.info("Fetching template types");
        TemplateStatisticsDto stats = templateService.getTemplateStatistics();
        return ResponseEntity.ok(stats.getTypes());
    }
    
    @PostMapping("/{id}/use")
    @Operation(summary = "Increment template usage", description = "Increment usage count for a template")
    @ApiResponse(responseCode = "200", description = "Usage incremented successfully")
    public ResponseEntity<Map<String, String>> incrementTemplateUsage(@PathVariable Long id) {
        log.info("Incrementing usage for template ID: {}", id);
        templateService.incrementTemplateUsage(id);
        return ResponseEntity.ok(Map.of("message", "Usage incremented successfully"));
    }
    
    @GetMapping("/popular")
    @Operation(summary = "Get popular templates", description = "Get most used templates")
    @ApiResponse(responseCode = "200", description = "Popular templates retrieved successfully")
    public ResponseEntity<List<TemplateDto>> getPopularTemplates(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Fetching popular templates with limit: {}", limit);
        // This would need to be implemented in the service
        return ResponseEntity.ok(List.of());
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recent templates", description = "Get recently created templates")
    @ApiResponse(responseCode = "200", description = "Recent templates retrieved successfully")
    public ResponseEntity<List<TemplateDto>> getRecentTemplates(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Fetching recent templates with limit: {}", limit);
        // This would need to be implemented in the service
        return ResponseEntity.ok(List.of());
    }
}
