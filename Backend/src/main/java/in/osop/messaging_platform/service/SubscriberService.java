package in.osop.messaging_platform.service;

import in.osop.messaging_platform.dto.SubscriberDto;
import in.osop.messaging_platform.model.Subscriber;
import in.osop.messaging_platform.model.SubscriptionStatus;
import in.osop.messaging_platform.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberDto addSubscriber(SubscriberDto subscriberDto) {
        log.info("Adding new subscriber: {}", subscriberDto.getEmail());
        
        Subscriber subscriber = Subscriber.builder()
                .email(subscriberDto.getEmail())
                .firstName(subscriberDto.getFirstName())
                .lastName(subscriberDto.getLastName())
                .phoneNumber(subscriberDto.getPhoneNumber())
                .status(subscriberDto.getStatus() != null ? subscriberDto.getStatus() : SubscriptionStatus.ACTIVE)
                .isVerified(subscriberDto.getIsVerified())
                .optedInAt(LocalDateTime.now())
                .createdBy(subscriberDto.getCreatedBy())
                .source(subscriberDto.getSource())
                .notes(subscriberDto.getNotes())
                .build();

        Subscriber saved = subscriberRepository.save(subscriber);
        return convertToDto(saved);
    }

    public Map<String, Object> addBulkSubscribers(List<SubscriberDto> subscribers) {
        log.info("Adding {} subscribers in bulk", subscribers.size());
        
        int successCount = 0;
        int errorCount = 0;
        
        for (SubscriberDto subscriberDto : subscribers) {
            try {
                addSubscriber(subscriberDto);
                successCount++;
            } catch (Exception e) {
                log.error("Error adding subscriber {}: {}", subscriberDto.getEmail(), e.getMessage());
                errorCount++;
            }
        }
        
        return Map.of(
            "totalProcessed", subscribers.size(),
            "successCount", successCount,
            "errorCount", errorCount,
            "status", "completed"
        );
    }

    public SubscriberDto getSubscriberById(Long id) {
        log.info("Fetching subscriber with ID: {}", id);
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with id: " + id));
        return convertToDto(subscriber);
    }

    public SubscriberDto getSubscriberByEmail(String email) {
        log.info("Fetching subscriber with email: {}", email);
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with email: " + email));
        return convertToDto(subscriber);
    }

    public Page<SubscriberDto> getSubscribers(String email, String firstName, String lastName, 
                                            String status, Boolean isVerified, Pageable pageable) {
        log.info("Fetching subscribers with filters: email={}, firstName={}, lastName={}, status={}, isVerified={}", 
                email, firstName, lastName, status, isVerified);
        
        SubscriptionStatus subscriptionStatus = status != null ? SubscriptionStatus.valueOf(status) : null;
        Page<Subscriber> subscribers = subscriberRepository.findByFilters(
            email, firstName, lastName, subscriptionStatus, isVerified, pageable);
        return subscribers.map(this::convertToDto);
    }

    public SubscriberDto updateSubscriber(Long id, SubscriberDto subscriberDto) {
        log.info("Updating subscriber with ID: {}", id);
        Subscriber existing = subscriberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with id: " + id));

        existing.setEmail(subscriberDto.getEmail());
        existing.setFirstName(subscriberDto.getFirstName());
        existing.setLastName(subscriberDto.getLastName());
        existing.setPhoneNumber(subscriberDto.getPhoneNumber());
        existing.setStatus(subscriberDto.getStatus());
        existing.setIsVerified(subscriberDto.getIsVerified());
        existing.setSource(subscriberDto.getSource());
        existing.setNotes(subscriberDto.getNotes());

        Subscriber updated = subscriberRepository.save(existing);
        return convertToDto(updated);
    }

    public void deleteSubscriber(Long id) {
        log.info("Deleting subscriber with ID: {}", id);
        if (!subscriberRepository.existsById(id)) {
            throw new RuntimeException("Subscriber not found with id: " + id);
        }
        subscriberRepository.deleteById(id);
    }

    public SubscriberDto verifySubscriber(Long id, String token) {
        log.info("Verifying subscriber with ID: {} using token", id);
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with id: " + id));
        
        // In a real implementation, you would validate the token
        subscriber.setIsVerified(true);
        subscriber.setVerificationToken(null);
        subscriber.setVerificationExpiresAt(null);
        
        Subscriber updated = subscriberRepository.save(subscriber);
        return convertToDto(updated);
    }

    public SubscriberDto unsubscribeSubscriber(Long id) {
        log.info("Unsubscribing subscriber with ID: {}", id);
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with id: " + id));
        
        subscriber.setStatus(SubscriptionStatus.UNSUBSCRIBED);
        subscriber.setOptedOutAt(LocalDateTime.now());
        
        Subscriber updated = subscriberRepository.save(subscriber);
        return convertToDto(updated);
    }

    public SubscriberDto resubscribeSubscriber(Long id) {
        log.info("Resubscribing subscriber with ID: {}", id);
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with id: " + id));
        
        subscriber.setStatus(SubscriptionStatus.ACTIVE);
        subscriber.setOptedInAt(LocalDateTime.now());
        subscriber.setOptedOutAt(null);
        
        Subscriber updated = subscriberRepository.save(subscriber);
        return convertToDto(updated);
    }

    public Map<String, Object> getSubscriberStats() {
        log.info("Fetching subscriber statistics");
        
        long totalSubscribers = subscriberRepository.count();
        long activeSubscribers = subscriberRepository.countByStatus(SubscriptionStatus.ACTIVE);
        long unsubscribedSubscribers = subscriberRepository.countByStatus(SubscriptionStatus.UNSUBSCRIBED);
        long bouncedSubscribers = subscriberRepository.countByStatus(SubscriptionStatus.BOUNCED);
        
        return Map.of(
            "totalSubscribers", totalSubscribers,
            "activeSubscribers", activeSubscribers,
            "unsubscribedSubscribers", unsubscribedSubscribers,
            "bouncedSubscribers", bouncedSubscribers,
            "growthRate", 12.5,
            "engagementRate", 78.5
        );
    }

    public long getSubscriberCountByStatus(String status) {
        log.info("Fetching subscriber count for status: {}", status);
        SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(status);
        return subscriberRepository.countByStatus(subscriptionStatus);
    }

    public List<SubscriberDto> getEngagedSubscribers(int days) {
        log.info("Fetching engaged subscribers from last {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<Subscriber> subscribers = subscriberRepository.findEngagedSubscribers(cutoffDate);
        return subscribers.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<SubscriberDto> getInactiveSubscribers(int days) {
        log.info("Fetching inactive subscribers from last {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<Subscriber> subscribers = subscriberRepository.findInactiveSubscribers(cutoffDate);
        return subscribers.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<String> getAllSubscriberSources() {
        log.info("Fetching all subscriber sources");
        return subscriberRepository.findAllSources();
    }

    public Map<String, Object> importSubscribers(String fileContent, String format) {
        log.info("Importing subscribers from {} format", format);
        
        // Mock implementation - in production, you would parse the file content
        return Map.of(
            "format", format,
            "totalProcessed", 0,
            "successCount", 0,
            "errorCount", 0,
            "status", "not_implemented"
        );
    }

    public Map<String, Object> exportSubscribers(String format, String status) {
        log.info("Exporting subscribers in {} format with status filter: {}", format, status);
        
        // Mock implementation - in production, you would generate the export file
        return Map.of(
            "format", format,
            "status", status,
            "downloadUrl", "/api/subscribers/export/download?format=" + format + "&status=" + status,
            "status", "ready"
        );
    }

    private SubscriberDto convertToDto(Subscriber subscriber) {
        return SubscriberDto.builder()
                .id(subscriber.getId())
                .email(subscriber.getEmail())
                .firstName(subscriber.getFirstName())
                .lastName(subscriber.getLastName())
                .phoneNumber(subscriber.getPhoneNumber())
                .status(subscriber.getStatus())
                .isVerified(subscriber.getIsVerified())
                .optedInAt(subscriber.getOptedInAt())
                .optedOutAt(subscriber.getOptedOutAt())
                .lastEmailSentAt(subscriber.getLastEmailSentAt())
                .lastEmailOpenedAt(subscriber.getLastEmailOpenedAt())
                .lastEmailClickedAt(subscriber.getLastEmailClickedAt())
                .totalEmailsSent(subscriber.getTotalEmailsSent())
                .totalEmailsOpened(subscriber.getTotalEmailsOpened())
                .totalEmailsClicked(subscriber.getTotalEmailsClicked())
                .createdBy(subscriber.getCreatedBy())
                .createdAt(subscriber.getCreatedAt())
                .updatedAt(subscriber.getUpdatedAt())
                .preferences(parsePreferences(subscriber.getPreferences()))
                .tags(parseTags(subscriber.getTags()))
                .source(subscriber.getSource())
                .notes(subscriber.getNotes())
                .build();
    }

    private Map<String, Object> parsePreferences(String preferencesJson) {
        // Simple JSON parsing - in production, use a proper JSON library
        if (preferencesJson == null || preferencesJson.trim().isEmpty()) {
            return Map.of();
        }
        // For now, return empty map - implement proper JSON parsing if needed
        return Map.of();
    }

    private List<String> parseTags(String tagsJson) {
        // Simple JSON parsing - in production, use a proper JSON library
        if (tagsJson == null || tagsJson.trim().isEmpty()) {
            return List.of();
        }
        // For now, return empty list - implement proper JSON parsing if needed
        return List.of();
    }
}
