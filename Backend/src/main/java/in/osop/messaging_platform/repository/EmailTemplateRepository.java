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
}
