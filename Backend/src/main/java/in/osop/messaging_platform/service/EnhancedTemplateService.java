package in.osop.messaging_platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.osop.messaging_platform.dto.*;
import in.osop.messaging_platform.model.EmailTemplate;
import in.osop.messaging_platform.model.TemplateVersion;
import in.osop.messaging_platform.repository.EmailTemplateRepository;
import in.osop.messaging_platform.repository.TemplateVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedTemplateService {
    
    private final EmailTemplateRepository templateRepository;
    private final TemplateVersionRepository versionRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * Create a new template
     */
    @Transactional
    public TemplateDto createTemplate(CreateTemplateRequest request, String createdBy) {
        log.info("Creating new template: {} by user: {}", request.getName(), createdBy);
        
        // Check if template name already exists
        if (templateRepository.existsByName(request.getName())) {
            throw new RuntimeException("Template with name '" + request.getName() + "' already exists");
        }
        
        EmailTemplate template = EmailTemplate.builder()
            .name(request.getName())
            .subject(request.getSubject())
            .contentHtml(request.getContentHtml())
            .contentText(request.getContentText())
            .category(request.getCategory())
            .type(request.getType())
            .createdBy(createdBy)
            .description(request.getDescription())
            .cssStyles(request.getCssStyles())
            .variables(request.getVariables())
            .tags(convertTagsToString(request.getTags()))
            .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
            .metadata(request.getMetadata())
            .version(1)
            .build();
        
        EmailTemplate savedTemplate = templateRepository.save(template);
        
        // Create initial version
        createTemplateVersion(savedTemplate, "Initial version", createdBy);
        
        return convertToDto(savedTemplate);
    }
    
    /**
     * Get all templates with pagination and filtering
     */
    public Page<TemplateDto> getTemplates(String searchTerm, EmailTemplate.TemplateCategory category, 
                                         EmailTemplate.TemplateType type, Boolean isActive, 
                                         String createdBy, Pageable pageable) {
        log.info("Fetching templates with filters: search={}, category={}, type={}, active={}, createdBy={}", 
                searchTerm, category, type, isActive, createdBy);
        
        Page<EmailTemplate> templates;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Use search functionality - simplified implementation
            // In production, implement proper pagination for search
            templates = Page.empty(pageable);
        } else {
            // Use repository filtering
            templates = templateRepository.findByFilters(
                null, // name filter
                category != null ? category.toString() : null,
                type != null ? type.toString() : null,
                isActive,
                pageable
            );
        }
        
        return templates.map(this::convertToDto);
    }
    
    /**
     * Get template by ID
     */
    public TemplateDto getTemplateById(Long id) {
        log.info("Fetching template with ID: {}", id);
        
        EmailTemplate template = templateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        return convertToDto(template);
    }
    
    /**
     * Update template
     */
    @Transactional
    public TemplateDto updateTemplate(Long id, UpdateTemplateRequest request, String updatedBy) {
        log.info("Updating template with ID: {} by user: {}", id, updatedBy);
        
        EmailTemplate template = templateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        if (!template.canBeEdited()) {
            throw new RuntimeException("Template cannot be edited");
        }
        
        // Create version before updating
        createTemplateVersion(template, request.getChangeDescription(), updatedBy);
        
        // Update template
        template.setName(request.getName());
        template.setSubject(request.getSubject());
        template.setContentHtml(request.getContentHtml());
        template.setContentText(request.getContentText());
        template.setCategory(request.getCategory());
        template.setType(request.getType());
        template.setDescription(request.getDescription());
        template.setCssStyles(request.getCssStyles());
        template.setVariables(request.getVariables());
        template.setTags(convertTagsToString(request.getTags()));
        template.setIsPublic(request.getIsPublic());
        template.setIsActive(request.getIsActive());
        template.setMetadata(request.getMetadata());
        template.setVersion(template.getVersion() + 1);
        
        EmailTemplate updatedTemplate = templateRepository.save(template);
        
        return convertToDto(updatedTemplate);
    }
    
    /**
     * Delete template
     */
    @Transactional
    public void deleteTemplate(Long id, String deletedBy) {
        log.info("Deleting template with ID: {} by user: {}", id, deletedBy);
        
        EmailTemplate template = templateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        if (template.getIsDefault()) {
            throw new RuntimeException("Cannot delete default template");
        }
        
        // Soft delete by setting isActive to false
        template.setIsActive(false);
        templateRepository.save(template);
    }
    
    /**
     * Clone template
     */
    @Transactional
    public TemplateDto cloneTemplate(Long id, String newName, String clonedBy) {
        log.info("Cloning template with ID: {} to new name: {} by user: {}", id, newName, clonedBy);
        
        EmailTemplate originalTemplate = templateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        if (templateRepository.existsByName(newName)) {
            throw new RuntimeException("Template with name '" + newName + "' already exists");
        }
        
        EmailTemplate clonedTemplate = EmailTemplate.builder()
            .name(newName)
            .subject(originalTemplate.getSubject())
            .contentHtml(originalTemplate.getContentHtml())
            .contentText(originalTemplate.getContentText())
            .category(originalTemplate.getCategory())
            .type(originalTemplate.getType())
            .createdBy(clonedBy)
            .description("Cloned from: " + originalTemplate.getName())
            .cssStyles(originalTemplate.getCssStyles())
            .variables(originalTemplate.getVariables())
            .tags(originalTemplate.getTags())
            .isPublic(false)
            .metadata(originalTemplate.getMetadata())
            .version(1)
            .parentTemplateId(originalTemplate.getId())
            .build();
        
        EmailTemplate savedTemplate = templateRepository.save(clonedTemplate);
        
        // Create initial version for cloned template
        createTemplateVersion(savedTemplate, "Cloned from template: " + originalTemplate.getName(), clonedBy);
        
        return convertToDto(savedTemplate);
    }
    
    /**
     * Get template versions
     */
    public List<TemplateVersionDto> getTemplateVersions(Long templateId) {
        log.info("Fetching versions for template ID: {}", templateId);
        
        List<TemplateVersion> versions = versionRepository.findByTemplateIdOrderByVersionNumberDesc(templateId);
        
        return versions.stream()
            .map(this::convertVersionToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Revert template to a specific version
     */
    @Transactional
    public TemplateDto revertToVersion(Long templateId, Integer versionNumber, String revertedBy) {
        log.info("Reverting template ID: {} to version: {} by user: {}", templateId, versionNumber, revertedBy);
        
        EmailTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found with ID: " + templateId));
        
        TemplateVersion version = versionRepository.findByTemplateIdAndVersionNumber(templateId, versionNumber)
            .orElseThrow(() -> new RuntimeException("Version not found"));
        
        // Create new version with reverted content
        createTemplateVersion(template, "Reverted to version " + versionNumber, revertedBy);
        
        // Update template with version content
        template.setContentHtml(version.getContentHtml());
        template.setContentText(version.getContentText());
        template.setSubject(version.getSubject());
        template.setCssStyles(version.getCssStyles());
        template.setVariables(version.getVariables());
        template.setVersion(template.getVersion() + 1);
        
        EmailTemplate updatedTemplate = templateRepository.save(template);
        
        return convertToDto(updatedTemplate);
    }
    
    /**
     * Import template
     */
    @Transactional
    public TemplateDto importTemplate(TemplateImportRequest request, String importedBy) {
        log.info("Importing template: {} by user: {}", request.getName(), importedBy);
        
        if (!request.getOverwriteExisting() && templateRepository.existsByName(request.getName())) {
            throw new RuntimeException("Template with name '" + request.getName() + "' already exists");
        }
        
        // If overwriting, delete existing template
        if (request.getOverwriteExisting()) {
            templateRepository.findByName(request.getName())
                .ifPresent(existingTemplate -> {
                    existingTemplate.setIsActive(false);
                    templateRepository.save(existingTemplate);
                });
        }
        
        CreateTemplateRequest createRequest = CreateTemplateRequest.builder()
            .name(request.getName())
            .subject(request.getSubject())
            .contentHtml(request.getContentHtml())
            .contentText(request.getContentText())
            .category(request.getCategory())
            .type(request.getType())
            .description(request.getDescription())
            .cssStyles(request.getCssStyles())
            .variables(request.getVariables())
            .tags(request.getTags())
            .isPublic(request.getIsPublic())
            .metadata(request.getMetadata())
            .build();
        
        return createTemplate(createRequest, importedBy);
    }
    
    /**
     * Export template
     */
    public String exportTemplate(Long id) {
        log.info("Exporting template with ID: {}", id);
        
        EmailTemplate template = templateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        try {
            return objectMapper.writeValueAsString(convertToDto(template));
        } catch (Exception e) {
            log.error("Error exporting template: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to export template");
        }
    }
    
    /**
     * Get template statistics
     */
    public TemplateStatisticsDto getTemplateStatistics() {
        log.info("Fetching template statistics");
        
        long totalTemplates = templateRepository.count();
        long activeTemplates = templateRepository.countByIsActiveTrue();
        long defaultTemplates = templateRepository.countByIsDefaultTrue();
        
        List<String> categories = templateRepository.findDistinctCategories();
        List<String> types = templateRepository.findDistinctTypes();
        
        return TemplateStatisticsDto.builder()
            .totalTemplates(totalTemplates)
            .activeTemplates(activeTemplates)
            .defaultTemplates(defaultTemplates)
            .categories(categories)
            .types(types)
            .build();
    }
    
    /**
     * Increment template usage
     */
    @Transactional
    public void incrementTemplateUsage(Long templateId) {
        log.debug("Incrementing usage for template ID: {}", templateId);
        
        EmailTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found with ID: " + templateId));
        
        template.incrementUsage();
        templateRepository.save(template);
    }
    
    // Helper methods
    
    private void createTemplateVersion(EmailTemplate template, String changeDescription, String createdBy) {
        TemplateVersion version = TemplateVersion.builder()
            .templateId(template.getId())
            .versionNumber(template.getVersion())
            .contentHtml(template.getContentHtml())
            .contentText(template.getContentText())
            .subject(template.getSubject())
            .cssStyles(template.getCssStyles())
            .variables(template.getVariables())
            .changeDescription(changeDescription)
            .createdBy(createdBy)
            .isCurrentVersion(true)
            .fileSize((long) template.getContentHtml().length())
            .metadata(template.getMetadata())
            .build();
        
        versionRepository.save(version);
    }
    
    private TemplateDto convertToDto(EmailTemplate template) {
        return TemplateDto.builder()
            .id(template.getId())
            .name(template.getName())
            .subject(template.getSubject())
            .contentHtml(template.getContentHtml())
            .contentText(template.getContentText())
            .category(template.getCategory())
            .type(template.getType())
            .createdBy(template.getCreatedBy())
            .isDefault(template.getIsDefault())
            .isActive(template.getIsActive())
            .isPublic(template.getIsPublic())
            .createdAt(template.getCreatedAt())
            .updatedAt(template.getUpdatedAt())
            .description(template.getDescription())
            .thumbnailUrl(template.getThumbnailUrl())
            .variables(template.getVariables())
            .version(template.getVersion())
            .parentTemplateId(template.getParentTemplateId())
            .usageCount(template.getUsageCount())
            .lastUsedAt(template.getLastUsedAt())
            .tags(convertStringToTags(template.getTags()))
            .cssStyles(template.getCssStyles())
            .metadata(template.getMetadata())
            .build();
    }
    
    private TemplateVersionDto convertVersionToDto(TemplateVersion version) {
        return TemplateVersionDto.builder()
            .id(version.getId())
            .templateId(version.getTemplateId())
            .versionNumber(version.getVersionNumber())
            .contentHtml(version.getContentHtml())
            .contentText(version.getContentText())
            .subject(version.getSubject())
            .cssStyles(version.getCssStyles())
            .variables(version.getVariables())
            .changeDescription(version.getChangeDescription())
            .createdBy(version.getCreatedBy())
            .createdAt(version.getCreatedAt())
            .isCurrentVersion(version.getIsCurrentVersion())
            .fileSize(version.getFileSize())
            .metadata(version.getMetadata())
            .build();
    }
    
    private String convertTagsToString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (Exception e) {
            log.error("Error converting tags to string: {}", e.getMessage());
            return null;
        }
    }
    
    private List<String> convertStringToTags(String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(tagsString, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("Error converting string to tags: {}", e.getMessage());
            return null;
        }
    }
}
