package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.EmailTemplateDto;
import in.osop.messaging_platform.model.EmailTemplate;
import in.osop.messaging_platform.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplateDto createTemplate(EmailTemplateDto templateDto) {
        log.info("Creating new email template: {}", templateDto.getName());
        
        EmailTemplate template = EmailTemplate.builder()
                .name(templateDto.getName())
                .subject(templateDto.getSubject())
                .body(templateDto.getBody())
                .category(templateDto.getCategory())
                .type(templateDto.getType())
                .createdBy(templateDto.getCreatedBy())
                .isDefault(templateDto.getIsDefault())
                .isActive(templateDto.getIsActive())
                .description(templateDto.getDescription())
                .thumbnailUrl(templateDto.getThumbnailUrl())
                .variables(templateDto.getVariables() != null ? 
                    templateDto.getVariables().toString() : null)
                .build();

        EmailTemplate saved = emailTemplateRepository.save(template);
        return convertToDto(saved);
    }

    public EmailTemplateDto getTemplateById(Long id) {
        log.info("Fetching template with ID: {}", id);
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
        return convertToDto(template);
    }

    public Page<EmailTemplateDto> getTemplates(String name, String category, String type, Boolean isActive, Pageable pageable) {
        log.info("Fetching templates with filters: name={}, category={}, type={}, isActive={}", name, category, type, isActive);
        Page<EmailTemplate> templates = emailTemplateRepository.findByFilters(name, category, type, isActive, pageable);
        return templates.map(this::convertToDto);
    }

    public EmailTemplateDto updateTemplate(Long id, EmailTemplateDto templateDto) {
        log.info("Updating template with ID: {}", id);
        EmailTemplate existing = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));

        existing.setName(templateDto.getName());
        existing.setSubject(templateDto.getSubject());
        existing.setBody(templateDto.getBody());
        existing.setCategory(templateDto.getCategory());
        existing.setType(templateDto.getType());
        existing.setIsDefault(templateDto.getIsDefault());
        existing.setIsActive(templateDto.getIsActive());
        existing.setDescription(templateDto.getDescription());
        existing.setThumbnailUrl(templateDto.getThumbnailUrl());
        existing.setVariables(templateDto.getVariables() != null ? 
            templateDto.getVariables().toString() : null);

        EmailTemplate updated = emailTemplateRepository.save(existing);
        return convertToDto(updated);
    }

    public void deleteTemplate(Long id) {
        log.info("Deleting template with ID: {}", id);
        if (!emailTemplateRepository.existsById(id)) {
            throw new RuntimeException("Template not found with id: " + id);
        }
        emailTemplateRepository.deleteById(id);
    }

    public EmailTemplateDto duplicateTemplate(Long id) {
        log.info("Duplicating template with ID: {}", id);
        EmailTemplate original = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));

        EmailTemplate duplicate = EmailTemplate.builder()
                .name(original.getName() + " (Copy)")
                .subject(original.getSubject())
                .body(original.getBody())
                .category(original.getCategory())
                .type(original.getType())
                .createdBy(original.getCreatedBy())
                .isDefault(false)
                .isActive(true)
                .description(original.getDescription())
                .thumbnailUrl(original.getThumbnailUrl())
                .variables(original.getVariables())
                .build();

        EmailTemplate saved = emailTemplateRepository.save(duplicate);
        return convertToDto(saved);
    }

    public EmailTemplateDto updateTemplateStatus(Long id, Boolean isActive) {
        log.info("Updating status for template with ID: {} to {}", id, isActive);
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
        
        template.setIsActive(isActive);
        EmailTemplate updated = emailTemplateRepository.save(template);
        return convertToDto(updated);
    }

    public List<String> getAllCategories() {
        log.info("Fetching all template categories");
        return emailTemplateRepository.findAllCategories();
    }

    public List<String> getAllTypes() {
        log.info("Fetching all template types");
        return emailTemplateRepository.findAllTypes();
    }

    public long getActiveTemplateCount() {
        log.info("Fetching active template count");
        return emailTemplateRepository.countActiveTemplates();
    }

    private EmailTemplateDto convertToDto(EmailTemplate template) {
        return EmailTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .subject(template.getSubject())
                .body(template.getBody())
                .category(template.getCategory())
                .type(template.getType())
                .createdBy(template.getCreatedBy())
                .isDefault(template.getIsDefault())
                .isActive(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .description(template.getDescription())
                .thumbnailUrl(template.getThumbnailUrl())
                .variables(parseVariables(template.getVariables()))
                .build();
    }

    private Map<String, String> parseVariables(String variablesJson) {
        // Simple JSON parsing - in production, use a proper JSON library
        if (variablesJson == null || variablesJson.trim().isEmpty()) {
            return Map.of();
        }
        // For now, return empty map - implement proper JSON parsing if needed
        return Map.of();
    }
}
