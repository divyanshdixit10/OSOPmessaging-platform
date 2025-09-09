package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    /**
     * Find recent activities ordered by creation date descending
     */
    Page<ActivityLog> findByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find activities by type
     */
    List<ActivityLog> findByActivityTypeOrderByCreatedAtDesc(ActivityLog.ActivityType activityType);
    
    /**
     * Find activities by entity type and entity ID
     */
    List<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
    
    /**
     * Find activities within date range
     */
    List<ActivityLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find recent activities for dashboard (last 10)
     */
    @Query("SELECT a FROM ActivityLog a ORDER BY a.createdAt DESC")
    List<ActivityLog> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * Count activities by type within date range
     */
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.activityType = :activityType AND a.createdAt BETWEEN :startDate AND :endDate")
    Long countByActivityTypeAndCreatedAtBetween(@Param("activityType") ActivityLog.ActivityType activityType, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
}
