package com.security.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.security.service.cve.NVDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for CVE (Common Vulnerabilities and Exposures) operations.
 * Handles CVE search and details retrieval from NVD API.
 */
@Slf4j
@RestController
@RequestMapping("/api/cve")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CVEController {

    private final NVDClient nvdClient;

    @Autowired
    public CVEController(NVDClient nvdClient) {
        this.nvdClient = nvdClient;
    }

    /**
     * Search for CVEs by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCVEs(@RequestParam String query) {
        log.info("Searching CVEs for query: {}", query);

        try {
            JsonNode result = nvdClient.searchByKeyword(query);

            if (result == null) {
                return ResponseEntity.ok(createErrorResponse("Failed to search NVD API"));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error searching CVEs", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get CVE details by CVE ID
     */
    @GetMapping("/{cveId}")
    public ResponseEntity<?> getCVEDetails(@PathVariable String cveId) {
        log.info("Fetching details for CVE: {}", cveId);

        try {
            // Validate CVE ID format
            if (!cveId.matches("CVE-\\d{4}-\\d+")) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Invalid CVE ID format. Expected: CVE-YYYY-XXXXX"));
            }

            JsonNode result = nvdClient.getCVEById(cveId);

            if (result == null) {
                return ResponseEntity.ok(createErrorResponse("CVE not found in NVD database"));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching CVE details for: {}", cveId, e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search CVEs by severity level
     */
    @GetMapping("/severity/{level}")
    public ResponseEntity<?> searchBySeverity(@PathVariable String level) {
        log.info("Searching CVEs by severity: {}", level);

        try {
            // Validate severity level
            String upperLevel = level.toUpperCase();
            if (!upperLevel.matches("(LOW|MEDIUM|HIGH|CRITICAL)")) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Invalid severity. Must be: LOW, MEDIUM, HIGH, or CRITICAL"));
            }

            JsonNode result = nvdClient.searchBySeverity(upperLevel);

            if (result == null) {
                return ResponseEntity.ok(createErrorResponse("Failed to search NVD API"));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error searching CVEs by severity", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search CVEs by CVSS score range
     */
    @GetMapping("/cvss-score")
    public ResponseEntity<?> searchByCVSSScore(
            @RequestParam(required = false, defaultValue = "0.0") Double minScore,
            @RequestParam(required = false, defaultValue = "10.0") Double maxScore) {
        try {
            log.info("Searching CVEs by CVSS score range: {} - {}", minScore, maxScore);

            // Validate CVSS scores
            if (minScore < 0 || maxScore > 10 || minScore > maxScore) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("CVSS scores must be between 0.0 and 10.0, and minScore <= maxScore"));
            }

            JsonNode result = nvdClient.searchByCVSSScore(minScore, maxScore);

            if (result == null) {
                return ResponseEntity.ok(createErrorResponse("Failed to search NVD API"));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error searching CVEs by CVSS score", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Health check endpoint for NVD API
     */
    @GetMapping("/health")
    public ResponseEntity<?> checkNVDHealth() {
        try {
            boolean available = nvdClient.isAvailable();
            Map<String, Object> response = new HashMap<>();
            response.put("status", available ? "healthy" : "unavailable");
            response.put("nvd_api_available", available);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking NVD API health", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("results", new Object[0]);
        error.put("count", 0);
        return error;
    }
}
