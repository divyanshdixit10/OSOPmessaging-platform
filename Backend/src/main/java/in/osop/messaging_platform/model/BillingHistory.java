package in.osop.messaging_platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingStatus status;

    @Column(name = "billing_date", nullable = false)
    private LocalDate billingDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BillingStatus {
        PENDING, PAID, FAILED, REFUNDED, CANCELLED
    }
}
