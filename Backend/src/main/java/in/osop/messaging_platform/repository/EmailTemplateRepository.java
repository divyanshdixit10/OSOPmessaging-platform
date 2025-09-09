package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    
    Optional<EmailTemplate> findByName(String name);
    
    List<EmailTemplate> findByCategory(String category);
    
    List<EmailTemplate> findByType(String type);
    
    List<EmailTemplate> findByIsDefaultTrue();
    
    List<EmailTemplate> findByIsActiveTrue();
    
    @Query("SELECT t FROM EmailTemplate t WHERE " +
           "(:name IS NULL OR t.name LIKE %:name%) AND " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:isActive IS NULL OR t.isActive = :isActive)")
    Page<EmailTemplate> findByFilters(
        @Param("name") String name,
        @Param("category") String category,
        @Param("type") String type,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
    
    @Query("SELECT DISTINCT t.category FROM EmailTemplate t WHERE t.category IS NOT NULL")
    List<String> findAllCategories();
    
    @Query("SELECT DISTINCT t.type FROM EmailTemplate t WHERE t.type IS NOT NULL")
    List<String> findAllTypes();
    
    boolean existsByName(String name);
    
    @Query("SELECT COUNT(t) FROM EmailTemplate t WHERE t.isActive = true")
    long countActiveTemplates();
    
    // Additional methods for the service
    long countByIsActiveTrue();
    
    long countByIsDefaultTrue();
    
    @Query("SELECT DISTINCT t.category FROM EmailTemplate t WHERE t.category IS NOT NULL")
    List<String> findDistinctCategories();
    
    @Query("SELECT DISTINCT t.type FROM EmailTemplate t WHERE t.type IS NOT NULL")
    List<String> findDistinctTypes();
    
    // Enhanced methods for template management
    
    /**
     * Find templates by category enum
     */
    List<EmailTemplate> findByCategory(EmailTemplate.TemplateCategory category);
    
    /**
     * Find templates by type enum
     */
    List<EmailTemplate> findByType(EmailTemplate.TemplateType type);
    
    /**
     * Find templates created by a specific user
     */
    List<EmailTemplate> findByCreatedByOrderByCreatedAtDesc(String createdBy);
    
    /**
     * Find public templates
     */
    List<EmailTemplate> findByIsPublicTrueAndIsActiveTrue();
    
    /**
     * Find templates with usage count greater than specified
     */
    List<EmailTemplate> findByUsageCountGreaterThanOrderByUsageCountDesc(Integer usageCount);
    
    /**
     * Find recently used templates
     */
    List<EmailTemplate> findByLastUsedAtIsNotNullOrderByLastUsedAtDesc();
    
    /**
     * Find templates by parent template ID (for versioning)
     */
    List<EmailTemplate> findByParentTemplateIdOrderByVersionDesc(Long parentTemplateId);
    
    /**
     * Find templates with specific tags
     */
    @Query("SELECT t FROM EmailTemplate t WHERE t.tags LIKE %:tag%")
    List<EmailTemplate> findByTag(@Param("tag") String tag);
    
    /**
     * Search templates by name or description
     */
    @Query("SELECT t FROM EmailTemplate t WHERE " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "t.isActive = true")
    List<EmailTemplate> searchTemplates(@Param("searchTerm") String searchTerm);
    
    /**
     * Find templates by multiple categories
     */
    @Query("SELECT t FROM EmailTemplate t WHERE t.category IN :categories AND t.isActive = true")
    List<EmailTemplate> findByCategoriesIn(@Param("categories") List<EmailTemplate.TemplateCategory> categories);
    
    /**
     * Get template statistics
     */
    @Query("SELECT COUNT(t) FROM EmailTemplate t WHERE t.category = :category")
    long countByCategory(@Param("category") EmailTemplate.TemplateCategory category);
    
    /**
     * Find most used templates
     */
    @Query("SELECT t FROM EmailTemplate t WHERE t.isActive = true ORDER BY t.usageCount DESC")
    List<EmailTemplate> findMostUsedTemplates();
    
    /**
     * Find templates created in date range
     */
    @Query("SELECT t FROM EmailTemplate t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<EmailTemplate> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                               @Param("endDate") java.time.LocalDateTime endDate);
}
