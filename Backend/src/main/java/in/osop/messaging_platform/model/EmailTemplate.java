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
@Table(name = "email_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;
    
    @Column(length = 100)
    private String category;
    
    @Column(length = 50)
    private String type;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
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
}
