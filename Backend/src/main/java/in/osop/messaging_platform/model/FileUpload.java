package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_uploads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Enumerated(EnumType.STRING)
    private FilePurpose purpose;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum FilePurpose {
        SUBSCRIBER_IMPORT, 
        TEMPLATE_IMPORT, 
        CAMPAIGN_ATTACHMENT, 
        PROFILE_PICTURE, 
        TENANT_LOGO, 
        EMAIL_ATTACHMENT
    }
}
