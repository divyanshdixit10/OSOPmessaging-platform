package in.osop.messaging_platform.repository;

import in.osop.messaging_platform.model.MessageChannel;
import in.osop.messaging_platform.model.MessageLog;
import in.osop.messaging_platform.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
    
    List<MessageLog> findByChannel(MessageChannel channel);
    
    List<MessageLog> findByStatus(MessageStatus status);
    
    List<MessageLog> findByRecipient(String recipient);
    
    List<MessageLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
} 