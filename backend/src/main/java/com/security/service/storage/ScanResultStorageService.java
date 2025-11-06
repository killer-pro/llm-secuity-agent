package com.security.service.storage;

import com.security.model.entity.ScanResult;
import com.security.model.enums.ScanType;
import com.security.model.enums.TaskStatus;
import com.security.repository.ScanResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing scan result storage and retrieval.
 * Handles persistence of scan data to the database.
 */
@Slf4j
@Service
public class ScanResultStorageService {

    private final ScanResultRepository scanResultRepository;

    @Autowired
    public ScanResultStorageService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    /**
     * Save a new scan result
     */
    @Transactional
    public ScanResult saveScanResult(ScanResult scanResult) {
        log.info("Saving scan result: {}", scanResult.getScanName());
        return scanResultRepository.save(scanResult);
    }

    /**
     * Update an existing scan result
     */
    @Transactional
    public ScanResult updateScanResult(ScanResult scanResult) {
        log.info("Updating scan result: {}", scanResult.getScanName());
        return scanResultRepository.save(scanResult);
    }

    /**
     * Get scan result by ID
     */
    public Optional<ScanResult> getScanResult(Long id) {
        log.info("Retrieving scan result with ID: {}", id);
        return scanResultRepository.findById(id);
    }

    /**
     * Get all scan results
     */
    public List<ScanResult> getAllScanResults() {
        log.info("Retrieving all scan results");
        return scanResultRepository.findAll();
    }

    /**
     * Get recent scans
     */
    public List<ScanResult> getRecentScans() {
        log.info("Retrieving recent scans");
        return scanResultRepository.findRecentScans();
    }

    /**
     * Get scans by type
     */
    public List<ScanResult> getScansByType(ScanType scanType) {
        log.info("Retrieving scans of type: {}", scanType);
        return scanResultRepository.findByScanType(scanType);
    }

    /**
     * Get scans by status
     */
    public List<ScanResult> getScansByStatus(TaskStatus status) {
        log.info("Retrieving scans with status: {}", status);
        return scanResultRepository.findByStatus(status);
    }

    /**
     * Get scans by target
     */
    public List<ScanResult> getScansByTarget(String target) {
        log.info("Retrieving scans for target: {}", target);
        return scanResultRepository.findByTarget(target);
    }

    /**
     * Get scans with vulnerabilities
     */
    public List<ScanResult> getScansWithVulnerabilities() {
        log.info("Retrieving scans with vulnerabilities");
        return scanResultRepository.findScansWithVulnerabilities();
    }

    /**
     * Get critical scans
     */
    public List<ScanResult> getCriticalScans() {
        log.info("Retrieving critical scans");
        return scanResultRepository.findCriticalScans();
    }

    /**
     * Delete scan result
     */
    @Transactional
    public void deleteScanResult(Long id) {
        log.info("Deleting scan result with ID: {}", id);
        scanResultRepository.deleteById(id);
    }

    /**
     * Count scans by status
     */
    public long countByStatus(TaskStatus status) {
        log.info("Counting scans with status: {}", status);
        return scanResultRepository.countByStatus(status);
    }

    /**
     * Update scan progress
     */
    @Transactional
    public ScanResult updateProgress(Long scanId, Integer progress) {
        log.info("Updating scan {} progress to {}%", scanId, progress);

        Optional<ScanResult> scanOpt = scanResultRepository.findById(scanId);
        if (scanOpt.isPresent()) {
            ScanResult scan = scanOpt.get();
            scan.setProgress(progress);

            if (progress == 100) {
                scan.setStatus(TaskStatus.COMPLETED);
                scan.setCompletedAt(LocalDateTime.now());
            }

            return scanResultRepository.save(scan);
        }

        return null;
    }

    /**
     * Mark scan as failed
     */
    @Transactional
    public ScanResult markScanFailed(Long scanId, String errorMessage) {
        log.info("Marking scan {} as failed: {}", scanId, errorMessage);

        Optional<ScanResult> scanOpt = scanResultRepository.findById(scanId);
        if (scanOpt.isPresent()) {
            ScanResult scan = scanOpt.get();
            scan.setStatus(TaskStatus.FAILED);
            scan.setErrorMessage(errorMessage);
            scan.setCompletedAt(LocalDateTime.now());

            return scanResultRepository.save(scan);
        }

        return null;
    }
}
