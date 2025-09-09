package in.osop.messaging_platform.service.impl;

import in.osop.messaging_platform.model.UsageTracking;
import in.osop.messaging_platform.repository.UsageTrackingRepository;
import in.osop.messaging_platform.service.UsageTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageTrackingServiceImpl implements UsageTrackingService {

    private final UsageTrackingRepository usageTrackingRepository;

    @Override
    @Transactional
    public UsageTracking trackUsage(Long tenantId, Long userId, UsageTracking.ResourceType resourceType, Long count) {
        if (count <= 0) {
            log.debug("Skipping usage tracking for zero or negative count: {}", count);
            return null;
        }

        LocalDate today = LocalDate.now();
        
        // Try to find existing usage tracking for today
        UsageTracking usageTracking = usageTrackingRepository
                .findByTenantIdAndResourceTypeAndUsageDate(tenantId, resourceType, today)
                .orElse(null);
        
        if (usageTracking == null) {
            // Create new usage tracking
            usageTracking = UsageTracking.builder()
                    .tenantId(tenantId)
                    .userId(userId)
                    .resourceType(resourceType)
                    .usageCount(count)
                    .usageDate(today)
                    .build();
        } else {
            // Update existing usage tracking
            usageTracking.setUsageCount(usageTracking.getUsageCount() + count);
        }
        
        log.debug("Tracked usage for tenant {}, resource {}: {} units", tenantId, resourceType, count);
        return usageTrackingRepository.save(usageTracking);
    }

    @Override
    public Long getUsageForTenant(Long tenantId, UsageTracking.ResourceType resourceType, LocalDate startDate, LocalDate endDate) {
        Long sum = usageTrackingRepository.sumUsageByTenantAndResourceTypeAndDateRange(
                tenantId, resourceType, startDate, endDate);
        return sum != null ? sum : 0L;
    }

    @Override
    public Long getCurrentMonthUsageForTenant(Long tenantId, UsageTracking.ResourceType resourceType) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        return getUsageForTenant(tenantId, resourceType, startOfMonth, endOfMonth);
    }

    @Override
    public Long getPreviousMonthUsageForTenant(Long tenantId, UsageTracking.ResourceType resourceType) {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        LocalDate startOfMonth = previousMonth.atDay(1);
        LocalDate endOfMonth = previousMonth.atEndOfMonth();
        return getUsageForTenant(tenantId, resourceType, startOfMonth, endOfMonth);
    }

    @Override
    public Map<UsageTracking.ResourceType, Long> getAllResourceUsageForCurrentMonth(Long tenantId) {
        Map<UsageTracking.ResourceType, Long> result = new HashMap<>();
        
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        // Get all usage records for the current month
        List<UsageTracking> usageList = usageTrackingRepository.findByTenantIdAndUsageDateBetween(
                tenantId, startOfMonth, endOfMonth);
        
        // Group by resource type and sum usage
        Map<UsageTracking.ResourceType, Long> usageByType = usageList.stream()
                .collect(Collectors.groupingBy(
                        UsageTracking::getResourceType,
                        Collectors.summingLong(UsageTracking::getUsageCount)));
        
        // Ensure all resource types are included in the result
        Arrays.stream(UsageTracking.ResourceType.values()).forEach(type -> 
                result.put(type, usageByType.getOrDefault(type, 0L)));
        
        return result;
    }

    @Override
    public List<UsageTracking> getUsageHistory(Long tenantId, UsageTracking.ResourceType resourceType, int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);
        
        return usageTrackingRepository.findByTenantIdAndResourceTypeAndUsageDateBetween(
                tenantId, resourceType, startDate, endDate);
    }

    @Override
    public Map<Integer, Long> getDailyUsageForMonth(Long tenantId, UsageTracking.ResourceType resourceType, int year, int month) {
        Map<Integer, Long> result = new HashMap<>();
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // Get all usage records for the specified month
        List<UsageTracking> usageList = usageTrackingRepository.findByTenantIdAndResourceTypeAndUsageDateBetween(
                tenantId, resourceType, startDate, endDate);
        
        // Group by day of month and sum usage
        Map<Integer, Long> usageByDay = usageList.stream()
                .collect(Collectors.groupingBy(
                        ut -> ut.getUsageDate().getDayOfMonth(),
                        Collectors.summingLong(UsageTracking::getUsageCount)));
        
        // Ensure all days of the month are included in the result
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            result.put(day, usageByDay.getOrDefault(day, 0L));
        }
        
        return result;
    }
}
