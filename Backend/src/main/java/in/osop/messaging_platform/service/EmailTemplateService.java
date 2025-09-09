package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.EmailTemplateDto;
import in.osop.messaging_platform.dto.EmailTemplateFormData;
import in.osop.messaging_platform.model.EmailTemplate;
import in.osop.messaging_platform.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {
    
    private final EmailTemplateRepository emailTemplateRepository;
    
    public Page<EmailTemplateDto> getTemplates(String name, String category, String type, Boolean isActive, Pageable pageable) {
        log.info("Fetching templates with filters: name={}, category={}, type={}, isActive={}", 
                name, category, type, isActive);
        
        Page<EmailTemplate> templates = emailTemplateRepository.findByFilters(name, category, type, isActive, pageable);
        return templates.map(this::convertToDto);
    }
    
    public EmailTemplateDto getTemplateById(Long id) {
        log.info("Fetching template with ID: {}", id);
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        return convertToDto(template);
    }
    
    @Transactional
    public EmailTemplateDto createTemplate(EmailTemplateFormData templateData) {
        log.info("Creating new template: {}", templateData.getName());
        
        // Check if template name already exists
        if (emailTemplateRepository.existsByName(templateData.getName())) {
            throw new RuntimeException("Template with name '" + templateData.getName() + "' already exists");
        }
        
        EmailTemplate template = EmailTemplate.builder()
                .name(templateData.getName())
                .subject(templateData.getSubject())
                .contentHtml(templateData.getBody())
                .category(EmailTemplate.TemplateCategory.valueOf(templateData.getCategory()))
                .type(EmailTemplate.TemplateType.valueOf(templateData.getType()))
                .description(templateData.getDescription())
                .variables(templateData.getVariables())
                .isActive(templateData.getIsActive() != null ? templateData.getIsActive() : true)
                .isDefault(templateData.getIsDefault() != null ? templateData.getIsDefault() : false)
                .createdBy("admin@osop.com") // TODO: Get from authentication context
                .build();
        
        EmailTemplate savedTemplate = emailTemplateRepository.save(template);
        log.info("Template created successfully with ID: {}", savedTemplate.getId());
        
        return convertToDto(savedTemplate);
    }
    
    @Transactional
    public EmailTemplateDto updateTemplate(Long id, EmailTemplateFormData templateData) {
        log.info("Updating template with ID: {}", id);
        
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        // Check if new name conflicts with existing templates (excluding current one)
        if (!template.getName().equals(templateData.getName()) && 
            emailTemplateRepository.existsByName(templateData.getName())) {
            throw new RuntimeException("Template with name '" + templateData.getName() + "' already exists");
        }
        
        template.setName(templateData.getName());
        template.setSubject(templateData.getSubject());
        template.setContentHtml(templateData.getBody());
        template.setCategory(EmailTemplate.TemplateCategory.valueOf(templateData.getCategory()));
        template.setType(EmailTemplate.TemplateType.valueOf(templateData.getType()));
        template.setDescription(templateData.getDescription());
        template.setVariables(templateData.getVariables());
        template.setIsActive(templateData.getIsActive() != null ? templateData.getIsActive() : template.getIsActive());
        template.setIsDefault(templateData.getIsDefault() != null ? templateData.getIsDefault() : template.getIsDefault());
        
        EmailTemplate updatedTemplate = emailTemplateRepository.save(template);
        log.info("Template updated successfully with ID: {}", updatedTemplate.getId());
        
        return convertToDto(updatedTemplate);
    }
    
    @Transactional
    public void deleteTemplate(Long id) {
        log.info("Deleting template with ID: {}", id);
        
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        // Check if template is being used by any campaigns
        // TODO: Add campaign dependency check
        
        emailTemplateRepository.delete(template);
        log.info("Template deleted successfully with ID: {}", id);
    }
    
    public List<String> getTemplateCategories() {
        log.info("Fetching template categories");
        return emailTemplateRepository.findDistinctCategories();
    }
    
    public List<String> getTemplateTypes() {
        log.info("Fetching template types");
        return emailTemplateRepository.findDistinctTypes();
    }
    
    public Map<String, Object> getTemplateStats() {
        log.info("Fetching template statistics");
        
        long totalTemplates = emailTemplateRepository.count();
        long activeTemplates = emailTemplateRepository.countByIsActiveTrue();
        long defaultTemplates = emailTemplateRepository.countByIsDefaultTrue();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTemplates", totalTemplates);
        stats.put("activeTemplates", activeTemplates);
        stats.put("defaultTemplates", defaultTemplates);
        stats.put("inactiveTemplates", totalTemplates - activeTemplates);
        
        return stats;
    }
    
    @Transactional
    public EmailTemplateDto duplicateTemplate(Long id) {
        log.info("Duplicating template with ID: {}", id);
        
        EmailTemplate originalTemplate = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        EmailTemplate duplicatedTemplate = EmailTemplate.builder()
                .name(originalTemplate.getName() + " (Copy)")
                .subject(originalTemplate.getSubject())
                .contentHtml(originalTemplate.getContentHtml())
                .category(originalTemplate.getCategory())
                .type(originalTemplate.getType())
                .description(originalTemplate.getDescription())
                .variables(originalTemplate.getVariables())
                .isActive(false) // Duplicated templates start as inactive
                .isDefault(false) // Duplicated templates are never default
                .createdBy("admin@osop.com") // TODO: Get from authentication context
                .build();
        
        EmailTemplate savedTemplate = emailTemplateRepository.save(duplicatedTemplate);
        log.info("Template duplicated successfully with ID: {}", savedTemplate.getId());
        
        return convertToDto(savedTemplate);
    }
    
    @Transactional
    public EmailTemplateDto activateTemplate(Long id) {
        log.info("Activating template with ID: {}", id);
        
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        template.setIsActive(true);
        template.setUpdatedAt(LocalDateTime.now());
        
        EmailTemplate updatedTemplate = emailTemplateRepository.save(template);
        log.info("Template activated successfully with ID: {}", updatedTemplate.getId());
        
        return convertToDto(updatedTemplate);
    }
    
    @Transactional
    public EmailTemplateDto deactivateTemplate(Long id) {
        log.info("Deactivating template with ID: {}", id);
        
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
        
        template.setIsActive(false);
        template.setUpdatedAt(LocalDateTime.now());
        
        EmailTemplate updatedTemplate = emailTemplateRepository.save(template);
        log.info("Template deactivated successfully with ID: {}", updatedTemplate.getId());
        
        return convertToDto(updatedTemplate);
    }
    
    private EmailTemplateDto convertToDto(EmailTemplate template) {
        return EmailTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .subject(template.getSubject())
                .body(template.getContentHtml())
                .category(template.getCategory().toString())
                .type(template.getType().toString())
                .description(template.getDescription())
                .variables(template.getVariables())
                .isActive(template.getIsActive())
                .isDefault(template.getIsDefault())
                .createdBy(template.getCreatedBy())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}