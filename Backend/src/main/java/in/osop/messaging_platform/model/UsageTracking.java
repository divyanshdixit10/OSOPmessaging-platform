package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_tracking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "usage_count", nullable = false)
    private Long usageCount;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ResourceType {
        EMAIL, SMS, WHATSAPP, STORAGE, CAMPAIGN, TEMPLATE, SUBSCRIBER, API_CALL
    }
}
