package com.security.repository;

import com.security.model.entity.ScanResult;
import com.security.model.enums.ScanType;
import com.security.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing ScanResult entities.
 * Provides database operations for security scan records.
 */
@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {

    /**
     * Find all scans of a specific type
     */
    List<ScanResult> findByScanType(ScanType scanType);

    /**
     * Find all scans with a specific status
     */
    List<ScanResult> findByStatus(TaskStatus status);

    /**
     * Find scans by target
     */
    List<ScanResult> findByTarget(String target);

    /**
     * Find all scans created within a time range
     */
    @Query("SELECT s FROM ScanResult s WHERE s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    List<ScanResult> findScansBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find scans by target and scan type
     */
    @Query("SELECT s FROM ScanResult s WHERE s.target = :target AND s.scanType = :scanType ORDER BY s.createdAt DESC")
    List<ScanResult> findByTargetAndScanType(
            @Param("target") String target,
            @Param("scanType") ScanType scanType
    );

    /**
     * Find all vulnerabilities found in scans (non-zero vulnerability count)
     */
    @Query("SELECT s FROM ScanResult s WHERE s.vulnerabilitiesFound > 0 ORDER BY s.maxCvssScore DESC")
    List<ScanResult> findScansWithVulnerabilities();

    /**
     * Find critical scans
     */
    @Query("SELECT s FROM ScanResult s WHERE s.criticalIssues > 0 ORDER BY s.criticalIssues DESC")
    List<ScanResult> findCriticalScans();

    /**
     * Find recent scans (limit 10)
     */
    @Query(value = "SELECT * FROM scan_results ORDER BY created_at DESC LIMIT 10", nativeQuery = true)
    List<ScanResult> findRecentScans();

    /**
     * Find in-progress scans
     */
    List<ScanResult> findByStatusIn(List<TaskStatus> statuses);

    /**
     * Count scans by status
     */
    long countByStatus(TaskStatus status);

    /**
     * Find by scan name pattern
     */
    List<ScanResult> findByScanNameContainingIgnoreCase(String scanName);
}
