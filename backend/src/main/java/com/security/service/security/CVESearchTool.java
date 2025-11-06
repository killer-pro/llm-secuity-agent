package com.security.service.security;

import com.security.exception.SecurityToolException;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CVE search tool for LangChain4j agent.
 * Performs semantic search on the CVE knowledge base using RAG.
 */
@Slf4j
@Component
public class CVESearchTool {

    @Autowired(required = false)
    private VulnerabilityScannerService vulnerabilityScannerService;

    /**
     * Search for CVEs by product name
     */
    @Tool("Searches the CVE database for vulnerabilities affecting a specific product. Example: searchProduct('Apache HTTP Server')")
    public String searchProduct(String productName) {
        log.info("Searching for CVEs affecting product: {}", productName);

        try {
            if (vulnerabilityScannerService == null) {
                return "Vulnerability scanner not available. Please check configuration.";
            }

            String result = vulnerabilityScannerService.searchCVEs(productName);
            return result;

        } catch (Exception e) {
            log.error("CVE search failed for product: {}", productName, e);
            throw new SecurityToolException(
                    "CVESearch",
                    "Failed to search CVEs: " + e.getMessage(),
                    "CVE_SEARCH_ERROR",
                    e
            );
        }
    }

    /**
     * Get details about a specific CVE
     */
    @Tool("Retrieves detailed information about a specific CVE. Example: getCVEDetails('CVE-2024-1234')")
    public String getCVEDetails(String cveId) {
        log.info("Retrieving details for CVE: {}", cveId);

        try {
            if (!cveId.matches("^CVE-\\d{4}-\\d+$")) {
                throw new SecurityToolException(
                        "CVESearch",
                        "Invalid CVE ID format: " + cveId,
                        "INVALID_CVE_ID"
                );
            }

            if (vulnerabilityScannerService == null) {
                return "Vulnerability scanner not available. Please check configuration.";
            }

            String result = vulnerabilityScannerService.getCVEDetails(cveId);
            return result;

        } catch (Exception e) {
            log.error("Failed to get CVE details: {}", cveId, e);
            throw new SecurityToolException(
                    "CVESearch",
                    "Failed to get CVE details: " + e.getMessage(),
                    "CVE_DETAILS_ERROR",
                    e
            );
        }
    }

    /**
     * Search CVEs by severity level
     */
    @Tool("Searches for CVEs with a specific severity level. Example: searchBySeverity('CRITICAL')")
    public String searchBySeverity(String severity) {
        log.info("Searching for CVEs with severity: {}", severity);

        // Validate severity
        if (!severity.matches("(?i)^(CRITICAL|HIGH|MEDIUM|LOW|UNKNOWN)$")) {
            throw new SecurityToolException(
                    "CVESearch",
                    "Invalid severity level: " + severity,
                    "INVALID_SEVERITY"
            );
        }

        try {
            if (vulnerabilityScannerService == null) {
                return "Vulnerability scanner not available. Please check configuration.";
            }

            String result = vulnerabilityScannerService.searchBySeverity(severity.toUpperCase());
            return result;

        } catch (Exception e) {
            log.error("CVE search failed for severity: {}", severity, e);
            throw new SecurityToolException(
                    "CVESearch",
                    "Failed to search CVEs by severity: " + e.getMessage(),
                    "SEVERITY_SEARCH_ERROR",
                    e
            );
        }
    }

    /**
     * Search CVEs by CVSS score range
     */
    @Tool("Searches for CVEs within a CVSS score range. Example: searchByCVSSScore(7.0, 10.0)")
    public String searchByCVSSScore(Double minScore, Double maxScore) {
        log.info("Searching for CVEs with CVSS score between {} and {}", minScore, maxScore);

        // Validate CVSS scores
        if (minScore < 0.0 || minScore > 10.0 || maxScore < 0.0 || maxScore > 10.0 || minScore > maxScore) {
            throw new SecurityToolException(
                    "CVESearch",
                    "Invalid CVSS score range: " + minScore + " to " + maxScore,
                    "INVALID_CVSS_RANGE"
            );
        }

        try {
            if (vulnerabilityScannerService == null) {
                return "Vulnerability scanner not available. Please check configuration.";
            }

            String result = vulnerabilityScannerService.searchByCVSSScore(minScore, maxScore);
            return result;

        } catch (Exception e) {
            log.error("CVE search failed for CVSS score range", e);
            throw new SecurityToolException(
                    "CVESearch",
                    "Failed to search CVEs by CVSS score: " + e.getMessage(),
                    "CVSS_SEARCH_ERROR",
                    e
            );
        }
    }

    /**
     * Get recent CVEs
     */
    @Tool("Retrieves recently published CVEs. Example: getRecentCVEs()")
    public String getRecentCVEs() {
        log.info("Retrieving recent CVEs");

        try {
            if (vulnerabilityScannerService == null) {
                return "Vulnerability scanner not available. Please check configuration.";
            }

            String result = vulnerabilityScannerService.getRecentCVEs();
            return result;

        } catch (Exception e) {
            log.error("Failed to retrieve recent CVEs", e);
            throw new SecurityToolException(
                    "CVESearch",
                    "Failed to retrieve recent CVEs: " + e.getMessage(),
                    "RECENT_CVE_ERROR",
                    e
            );
        }
    }
}
