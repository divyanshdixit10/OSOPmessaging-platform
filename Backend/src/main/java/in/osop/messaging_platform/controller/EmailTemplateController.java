package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.EmailTemplateDto;
import in.osop.messaging_platform.dto.EmailTemplateFormData;
import in.osop.messaging_platform.model.EmailTemplate;
import in.osop.messaging_platform.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Email Template API", description = "APIs for managing email templates")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailTemplateController {
    
    private final EmailTemplateService emailTemplateService;
    
    @GetMapping
    @Operation(summary = "Get all email templates", description = "Get paginated list of email templates with optional filters")
    @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    public ResponseEntity<Page<EmailTemplateDto>> getTemplates(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching templates with filters: name={}, category={}, type={}, isActive={}", 
                name, category, type, isActive);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EmailTemplateDto> templates = emailTemplateService.getTemplates(name, category, type, isActive, pageable);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID", description = "Get a specific email template by its ID")
    @ApiResponse(responseCode = "200", description = "Template retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<EmailTemplateDto> getTemplateById(@PathVariable Long id) {
        log.info("Fetching template with ID: {}", id);
        EmailTemplateDto template = emailTemplateService.getTemplateById(id);
        return ResponseEntity.ok(template);
    }
    
    @PostMapping
    @Operation(summary = "Create new template", description = "Create a new email template")
    @ApiResponse(responseCode = "201", description = "Template created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid template data")
    public ResponseEntity<EmailTemplateDto> createTemplate(@Valid @RequestBody EmailTemplateFormData templateData) {
        log.info("Creating new template: {}", templateData.getName());
        EmailTemplateDto createdTemplate = emailTemplateService.createTemplate(templateData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update template", description = "Update an existing email template")
    @ApiResponse(responseCode = "200", description = "Template updated successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<EmailTemplateDto> updateTemplate(
            @PathVariable Long id, 
            @Valid @RequestBody EmailTemplateFormData templateData) {
        log.info("Updating template with ID: {}", id);
        EmailTemplateDto updatedTemplate = emailTemplateService.updateTemplate(id, templateData);
        return ResponseEntity.ok(updatedTemplate);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template", description = "Delete an email template")
    @ApiResponse(responseCode = "204", description = "Template deleted successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        log.info("Deleting template with ID: {}", id);
        emailTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get template categories", description = "Get list of all template categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<String>> getTemplateCategories() {
        log.info("Fetching template categories");
        List<String> categories = emailTemplateService.getTemplateCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/types")
    @Operation(summary = "Get template types", description = "Get list of all template types")
    @ApiResponse(responseCode = "200", description = "Types retrieved successfully")
    public ResponseEntity<List<String>> getTemplateTypes() {
        log.info("Fetching template types");
        List<String> types = emailTemplateService.getTemplateTypes();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/stats/count")
    @Operation(summary = "Get template statistics", description = "Get template count statistics")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getTemplateStats() {
        log.info("Fetching template statistics");
        Map<String, Object> stats = emailTemplateService.getTemplateStats();
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate template", description = "Create a copy of an existing template")
    @ApiResponse(responseCode = "201", description = "Template duplicated successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<EmailTemplateDto> duplicateTemplate(@PathVariable Long id) {
        log.info("Duplicating template with ID: {}", id);
        EmailTemplateDto duplicatedTemplate = emailTemplateService.duplicateTemplate(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedTemplate);
    }
    
    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate template", description = "Activate an email template")
    @ApiResponse(responseCode = "200", description = "Template activated successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<EmailTemplateDto> activateTemplate(@PathVariable Long id) {
        log.info("Activating template with ID: {}", id);
        EmailTemplateDto activatedTemplate = emailTemplateService.activateTemplate(id);
        return ResponseEntity.ok(activatedTemplate);
    }
    
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate template", description = "Deactivate an email template")
    @ApiResponse(responseCode = "200", description = "Template deactivated successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<EmailTemplateDto> deactivateTemplate(@PathVariable Long id) {
        log.info("Deactivating template with ID: {}", id);
        EmailTemplateDto deactivatedTemplate = emailTemplateService.deactivateTemplate(id);
        return ResponseEntity.ok(deactivatedTemplate);
    }
}