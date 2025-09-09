package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_templates", indexes = {
    @Index(name = "idx_template_category", columnList = "category"),
    @Index(name = "idx_template_type", columnList = "type"),
    @Index(name = "idx_template_created_by", columnList = "createdBy"),
    @Index(name = "idx_template_active", columnList = "isActive")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(name = "content_html", columnDefinition = "LONGTEXT", nullable = false)
    private String contentHtml;
    
    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private TemplateCategory category = TemplateCategory.CUSTOM;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private TemplateType type = TemplateType.HTML;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "variables", columnDefinition = "TEXT")
    private String variables; // JSON string of template variables
    
    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;
    
    @Column(name = "parent_template_id")
    private Long parentTemplateId; // For versioning/cloning
    
    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags
    
    @Column(name = "css_styles", columnDefinition = "TEXT")
    private String cssStyles; // Custom CSS styles
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional metadata
    
    // Template categories
    public enum TemplateCategory {
        NEWSLETTER,
        PROMOTION,
        TRANSACTIONAL,
        WELCOME,
        FOLLOW_UP,
        ANNOUNCEMENT,
        CUSTOM
    }
    
    // Template types
    public enum TemplateType {
        HTML,
        TEXT,
        RICH_TEXT
    }
    
    // Helper methods
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();
    }
    
    public boolean isLatestVersion() {
        return parentTemplateId == null;
    }
    
    public boolean canBeEdited() {
        return isActive && !isDefault;
    }
}
