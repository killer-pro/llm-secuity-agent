package com.security.controller;

import com.security.model.dto.ScanRequest;
import com.security.model.entity.ScanResult;
import com.security.model.enums.TaskStatus;
import com.security.service.storage.ScanResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for security scan operations.
 * Handles scan creation, monitoring, and result retrieval.
 */
@Slf4j
@RestController
@RequestMapping("/api/scans")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScanController {

    private final ScanResultStorageService scanResultStorageService;

    @Autowired
    public ScanController(ScanResultStorageService scanResultStorageService) {
        this.scanResultStorageService = scanResultStorageService;
    }

    /**
     * Initiate a new security scan
     */
    @PostMapping("/initiate")
    public ResponseEntity<ScanResult> initiateScan(@RequestBody ScanRequest scanRequest) {
        log.info("Initiating scan for target: {}", scanRequest.getTarget());

        try {
            ScanResult scanResult = ScanResult.builder()
                    .scanType(scanRequest.getScanType())
                    .target(scanRequest.getTarget())
                    .scanName(scanRequest.getScanName())
                    .description(scanRequest.getParameters())
                    .status(TaskStatus.PENDING)
                    .progress(0)
                    .createdAt(LocalDateTime.now())
                    .build();

            ScanResult saved = scanResultStorageService.saveScanResult(scanResult);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            log.error("Error initiating scan", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all scans
     */
    @GetMapping("/")
    public ResponseEntity<List<ScanResult>> getAllScans() {
        log.info("Fetching all scans");

        try {
            List<ScanResult> scans = scanResultStorageService.getAllScanResults();
            return ResponseEntity.ok(scans);

        } catch (Exception e) {
            log.error("Error fetching scans", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get recent scans
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ScanResult>> getRecentScans() {
        log.info("Fetching recent scans");

        try {
            List<ScanResult> scans = scanResultStorageService.getRecentScans();
            return ResponseEntity.ok(scans);

        } catch (Exception e) {
            log.error("Error fetching recent scans", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get scan by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScanResult> getScanById(@PathVariable Long id) {
        log.info("Fetching scan with ID: {}", id);

        try {
            Optional<ScanResult> scan = scanResultStorageService.getScanResult(id);
            return scan.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (Exception e) {
            log.error("Error fetching scan", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update scan progress
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<ScanResult> updateProgress(@PathVariable Long id, @RequestParam Integer progress) {
        log.info("Updating scan {} progress to {}%", id, progress);

        try {
            ScanResult updated = scanResultStorageService.updateProgress(id, progress);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error updating progress", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get critical scans
     */
    @GetMapping("/critical")
    public ResponseEntity<List<ScanResult>> getCriticalScans() {
        log.info("Fetching critical scans");

        try {
            List<ScanResult> scans = scanResultStorageService.getCriticalScans();
            return ResponseEntity.ok(scans);

        } catch (Exception e) {
            log.error("Error fetching critical scans", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get scans with vulnerabilities
     */
    @GetMapping("/vulnerabilities")
    public ResponseEntity<List<ScanResult>> getScansWithVulnerabilities() {
        log.info("Fetching scans with vulnerabilities");

        try {
            List<ScanResult> scans = scanResultStorageService.getScansWithVulnerabilities();
            return ResponseEntity.ok(scans);

        } catch (Exception e) {
            log.error("Error fetching vulnerability scans", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a scan
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScan(@PathVariable Long id) {
        log.info("Deleting scan with ID: {}", id);

        try {
            scanResultStorageService.deleteScanResult(id);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error deleting scan", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
