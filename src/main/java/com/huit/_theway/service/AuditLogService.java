package com.huit._theway.service;

import com.huit._theway.model.AuditLog;
import com.huit._theway.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public void logAction(String action, String entityName, String entityId, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";
        
        AuditLog log = AuditLog.builder()
                .username(username)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }

    public Page<AuditLog> searchAndPaginate(String keyword, String dateStr, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        java.time.LocalDate date;
        if (dateStr == null || dateStr.isEmpty()) {
            date = java.time.LocalDate.now();
        } else {
            try {
                date = java.time.LocalDate.parse(dateStr);
            } catch (Exception e) {
                date = java.time.LocalDate.now();
            }
        }
        
        java.time.LocalDateTime startDate = date.atStartOfDay();
        java.time.LocalDateTime endDate = date.plusDays(1).atStartOfDay();

        if (keyword != null && !keyword.isEmpty()) {
            return auditLogRepository.searchByKeywordAndDate(keyword, startDate, endDate, pageRequest);
        }
        return auditLogRepository.findByDateBetween(startDate, endDate, pageRequest);
    }
}
