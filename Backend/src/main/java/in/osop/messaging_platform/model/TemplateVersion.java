package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for tracking template version history
 */
@Entity
@Table(name = "template_versions", indexes = {
    @Index(name = "idx_template_id", columnList = "templateId"),
    @Index(name = "idx_version_number", columnList = "versionNumber"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "template_id", nullable = false)
    private Long templateId;
    
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;
    
    @Column(name = "content_html", columnDefinition = "LONGTEXT", nullable = false)
    private String contentHtml;
    
    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;
    
    @Column(name = "subject", nullable = false)
    private String subject;
    
    @Column(name = "css_styles", columnDefinition = "TEXT")
    private String cssStyles;
    
    @Column(name = "variables", columnDefinition = "TEXT")
    private String variables;
    
    @Column(name = "change_description", columnDefinition = "TEXT")
    private String changeDescription;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_current_version")
    @Builder.Default
    private Boolean isCurrentVersion = false;
    
    @Column(name = "file_size")
    private Long fileSize; // Size in bytes
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional metadata
}
