package in.osop.messaging_platform.controller;

import in.osop.messaging_platform.dto.EmailTemplateDto;
import in.osop.messaging_platform.service.EmailTemplateService;
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
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Email Template API", description = "APIs for managing email templates")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @PostMapping
    @Operation(summary = "Create a new email template", description = "Create a new email template with the provided details")
    @ApiResponse(responseCode = "201", description = "Template created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Template with this name already exists")
    public ResponseEntity<EmailTemplateDto> createTemplate(@Valid @RequestBody EmailTemplateDto templateDto) {
        log.info("Creating new email template: {}", templateDto.getName());
        EmailTemplateDto created = emailTemplateService.createTemplate(templateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID", description = "Retrieve an email template by its ID")
    @ApiResponse(responseCode = "200", description = "Template found")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<EmailTemplateDto> getTemplate(@PathVariable Long id) {
        log.info("Fetching template with ID: {}", id);
        EmailTemplateDto template = emailTemplateService.getTemplateById(id);
        return ResponseEntity.ok(template);
    }

    @GetMapping
    @Operation(summary = "Get all templates", description = "Retrieve all email templates with optional filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    public ResponseEntity<Page<EmailTemplateDto>> getAllTemplates(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        log.info("Fetching templates with filters: name={}, category={}, type={}, isActive={}", name, category, type, isActive);
        Page<EmailTemplateDto> templates = emailTemplateService.getTemplates(name, category, type, isActive, pageable);
        return ResponseEntity.ok(templates);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update template", description = "Update an existing email template")
    @ApiResponse(responseCode = "200", description = "Template updated successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    @ApiResponse(responseCode = "409", description = "Template name conflict")
    public ResponseEntity<EmailTemplateDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody EmailTemplateDto templateDto) {
        log.info("Updating template with ID: {}", id);
        EmailTemplateDto updated = emailTemplateService.updateTemplate(id, templateDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template", description = "Delete an email template by ID")
    @ApiResponse(responseCode = "204", description = "Template deleted successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        log.info("Deleting template with ID: {}", id);
        emailTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all template categories", description = "Retrieve all available template categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<String>> getAllCategories() {
        log.info("Fetching all template categories");
        List<String> categories = emailTemplateService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/types")
    @Operation(summary = "Get all template types", description = "Retrieve all available template types")
    @ApiResponse(responseCode = "200", description = "Types retrieved successfully")
    public ResponseEntity<List<String>> getAllTypes() {
        log.info("Fetching all template types");
        List<String> types = emailTemplateService.getAllTypes();
        return ResponseEntity.ok(types);
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate template", description = "Create a copy of an existing template")
    @ApiResponse(responseCode = "201", description = "Template duplicated successfully")
    @ApiResponse(responseCode = "404", description = "Original template not found")
    public ResponseEntity<EmailTemplateDto> duplicateTemplate(@PathVariable Long id) {
        log.info("Duplicating template with ID: {}", id);
        EmailTemplateDto duplicated = emailTemplateService.duplicateTemplate(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicated);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update template status", description = "Update the active status of a template")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<EmailTemplateDto> updateTemplateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusUpdate) {
        log.info("Updating status for template with ID: {}", id);
        EmailTemplateDto updated = emailTemplateService.updateTemplateStatus(id, statusUpdate.get("isActive"));
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Get template statistics", description = "Get count of active templates")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Long>> getTemplateStats() {
        log.info("Fetching template statistics");
        long activeCount = emailTemplateService.getActiveTemplateCount();
        return ResponseEntity.ok(Map.of("activeTemplates", activeCount));
    }
}
