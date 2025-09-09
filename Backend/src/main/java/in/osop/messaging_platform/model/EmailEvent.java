package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    
    @Column(name = "campaign_id", insertable = false, updatable = false)
    private Long campaignId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;
    
    @Column(name = "subscriber_id", insertable = false, updatable = false)
    private Long subscriberId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    
    @Column(name = "tenant_id", insertable = false, updatable = false)
    private Long tenantId;
    
    @Column(nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailEventType eventType;
    
    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData; // JSON string with additional event data
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "device_type")
    private String deviceType;
    
    @Column(name = "browser")
    private String browser;
    
    @Column(name = "os")
    private String os;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "processed")
    @Builder.Default
    private Boolean processed = false;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
