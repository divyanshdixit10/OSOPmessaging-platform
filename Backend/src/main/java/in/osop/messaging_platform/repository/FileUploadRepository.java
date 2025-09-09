package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    
    List<FileUpload> findByTenantId(Long tenantId);
    
    List<FileUpload> findByUserId(Long userId);
    
    List<FileUpload> findByTenantIdAndPurpose(Long tenantId, FileUpload.FilePurpose purpose);
    
    List<FileUpload> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
    
    @Query("SELECT SUM(f.fileSize) FROM FileUpload f WHERE f.tenantId = :tenantId")
    Long sumFileSizeByTenantId(@Param("tenantId") Long tenantId);
}
