package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ActivityLog entity
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    /**
     * Find activities by entity type and ID
     */
    Page<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, Long entityId, Pageable pageable);
    
    /**
     * Find activities by user
     */
    Page<ActivityLog> findByPerformedByOrderByCreatedAtDesc(String performedBy, Pageable pageable);
    
    /**
     * Find activities by type
     */
    Page<ActivityLog> findByActivityTypeOrderByCreatedAtDesc(ActivityLog.ActivityType activityType, Pageable pageable);
    
    /**
     * Find activities by type (simple list)
     */
    List<ActivityLog> findByActivityType(ActivityLog.ActivityType activityType);
    
    /**
     * Find all activities ordered by timestamp
     */
    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find top 10 recent activities
     */
    List<ActivityLog> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * Find activities by time range
     */
    Page<ActivityLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    /**
     * Find activities by entity type
     */
    Page<ActivityLog> findByEntityTypeOrderByCreatedAtDesc(String entityType, Pageable pageable);
    
    /**
     * Find activities by type and entity type
     */
    Page<ActivityLog> findByActivityTypeAndEntityTypeOrderByCreatedAtDesc(
            ActivityLog.ActivityType activityType, String entityType, Pageable pageable);
    
    /**
     * Count activities by type
     */
    long countByActivityType(ActivityLog.ActivityType activityType);
    
    /**
     * Count activities by entity type and ID
     */
    long countByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Find recent activities by entity type and ID
     */
    List<ActivityLog> findTop10ByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
    
    /**
     * Find activities by entity type and ID (simple list)
     */
    List<ActivityLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Find activities by performed by (simple list)
     */
    List<ActivityLog> findByPerformedBy(String performedBy);
}