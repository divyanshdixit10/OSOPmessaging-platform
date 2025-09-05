package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subscribers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;
    
    @Column(name = "verification_token")
    private String verificationToken;
    
    @Column(name = "verification_expires_at")
    private LocalDateTime verificationExpiresAt;
    
    @Column(name = "unsubscribe_token")
    private String unsubscribeToken;
    
    @Column(name = "opted_in_at")
    private LocalDateTime optedInAt;
    
    @Column(name = "opted_out_at")
    private LocalDateTime optedOutAt;
    
    @Column(name = "last_email_sent_at")
    private LocalDateTime lastEmailSentAt;
    
    @Column(name = "last_email_opened_at")
    private LocalDateTime lastEmailOpenedAt;
    
    @Column(name = "last_email_clicked_at")
    private LocalDateTime lastEmailClickedAt;
    
    @Column(name = "total_emails_sent")
    @Builder.Default
    private Integer totalEmailsSent = 0;
    
    @Column(name = "total_emails_opened")
    @Builder.Default
    private Integer totalEmailsOpened = 0;
    
    @Column(name = "total_emails_clicked")
    @Builder.Default
    private Integer totalEmailsClicked = 0;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Preferences
    @Column(name = "preferences", columnDefinition = "TEXT")
    private String preferences; // JSON string of subscriber preferences
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags
    
    @Column(name = "source")
    private String source; // How they subscribed (website, import, etc.)
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
