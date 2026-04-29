package com.huit._theway.repository;

import com.huit._theway.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(LOWER(a.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.action) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.entityName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "a.createdAt >= :startDate AND a.createdAt < :endDate")
    Page<AuditLog> searchByKeywordAndDate(@Param("keyword") String keyword, 
                                          @Param("startDate") java.time.LocalDateTime startDate, 
                                          @Param("endDate") java.time.LocalDateTime endDate, 
                                          Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.createdAt >= :startDate AND a.createdAt < :endDate")
    Page<AuditLog> findByDateBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                     @Param("endDate") java.time.LocalDateTime endDate, 
                                     Pageable pageable);
}
