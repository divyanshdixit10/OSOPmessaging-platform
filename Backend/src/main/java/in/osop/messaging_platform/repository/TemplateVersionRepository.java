package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.TemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {
    
    /**
     * Find all versions for a template ordered by version number descending
     */
    List<TemplateVersion> findByTemplateIdOrderByVersionNumberDesc(Long templateId);
    
    /**
     * Find the latest version for a template
     */
    @Query("SELECT tv FROM TemplateVersion tv WHERE tv.templateId = :templateId AND tv.isCurrentVersion = true")
    Optional<TemplateVersion> findCurrentVersionByTemplateId(@Param("templateId") Long templateId);
    
    /**
     * Find a specific version of a template
     */
    Optional<TemplateVersion> findByTemplateIdAndVersionNumber(Long templateId, Integer versionNumber);
    
    /**
     * Get the next version number for a template
     */
    @Query("SELECT COALESCE(MAX(tv.versionNumber), 0) + 1 FROM TemplateVersion tv WHERE tv.templateId = :templateId")
    Integer getNextVersionNumber(@Param("templateId") Long templateId);
    
    /**
     * Find versions created by a specific user
     */
    List<TemplateVersion> findByTemplateIdAndCreatedByOrderByVersionNumberDesc(Long templateId, String createdBy);
    
    /**
     * Count total versions for a template
     */
    long countByTemplateId(Long templateId);
    
    /**
     * Find recent versions across all templates
     */
    @Query("SELECT tv FROM TemplateVersion tv ORDER BY tv.createdAt DESC")
    List<TemplateVersion> findRecentVersions();
    
    /**
     * Delete old versions (for cleanup)
     */
    void deleteByTemplateIdAndVersionNumberLessThan(Long templateId, Integer versionNumber);
}
