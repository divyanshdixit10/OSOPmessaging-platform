package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.Subscriber;
import in.osop.messaging_platform.model.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    
    Optional<Subscriber> findByEmail(String email);
    
    List<Subscriber> findByStatus(SubscriptionStatus status);
    
    List<Subscriber> findByIsVerifiedTrue();
    
    List<Subscriber> findByIsVerifiedFalse();
    
    @Query("SELECT s FROM Subscriber s WHERE " +
           "(:email IS NULL OR s.email LIKE %:email%) AND " +
           "(:firstName IS NULL OR s.firstName LIKE %:firstName%) AND " +
           "(:lastName IS NULL OR s.lastName LIKE %:lastName%) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:isVerified IS NULL OR s.isVerified = :isVerified)")
    Page<Subscriber> findByFilters(
        @Param("email") String email,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("status") SubscriptionStatus status,
        @Param("isVerified") Boolean isVerified,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(s) FROM Subscriber s WHERE s.status = :status")
    long countByStatus(@Param("status") SubscriptionStatus status);
    
    @Query("SELECT COUNT(s) FROM Subscriber s WHERE s.status = :status AND s.createdAt >= :startDate")
    long countByStatusAndDateAfter(@Param("status") SubscriptionStatus status, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT s FROM Subscriber s WHERE s.lastEmailOpenedAt >= :date")
    List<Subscriber> findEngagedSubscribers(@Param("date") LocalDateTime date);
    
    @Query("SELECT s FROM Subscriber s WHERE s.lastEmailOpenedAt IS NULL OR s.lastEmailOpenedAt < :date")
    List<Subscriber> findInactiveSubscribers(@Param("date") LocalDateTime date);
    
    @Query("SELECT s FROM Subscriber s WHERE s.optedInAt >= :startDate AND s.optedInAt <= :endDate")
    List<Subscriber> findSubscribersByOptInPeriod(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT s FROM Subscriber s WHERE s.totalEmailsOpened > 0 ORDER BY s.totalEmailsOpened DESC")
    List<Subscriber> findTopEngagedSubscribers(Pageable pageable);
    
    @Query("SELECT s FROM Subscriber s WHERE s.totalEmailsSent > 0 ORDER BY s.totalEmailsSent DESC")
    List<Subscriber> findTopActiveSubscribers(Pageable pageable);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT DISTINCT s.source FROM Subscriber s WHERE s.source IS NOT NULL")
    List<String> findAllSources();
}
