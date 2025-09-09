package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    
    List<NotificationSetting> findByUserId(Long userId);
    
    List<NotificationSetting> findByTenantId(Long tenantId);
    
    List<NotificationSetting> findByUserIdAndEnabled(Long userId, Boolean enabled);
    
    Optional<NotificationSetting> findByUserIdAndNotificationTypeAndEventType(
            Long userId, 
            NotificationSetting.NotificationType notificationType, 
            NotificationSetting.EventType eventType);
    
    List<NotificationSetting> findByEventTypeAndEnabled(
            NotificationSetting.EventType eventType, 
            Boolean enabled);
}
