package com.security.model.dto;

import com.security.model.enums.ScanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for initiating security scans.
 * Contains all parameters needed to start a scan operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanRequest {

    /**
     * Type of scan to perform
     */
    private ScanType scanType;

    /**
     * Target of the scan (IP address, CIDR range, domain, file path)
     */
    private String target;

    /**
     * Human-readable name for the scan
     */
    private String scanName;

    /**
     * Additional parameters (Nmap arguments, etc.)
     */
    private String parameters;

    /**
     * Whether to perform vulnerability analysis after scan
     */
    @Builder.Default
    private Boolean performVulnerabilityAnalysis = true;

    /**
     * Whether to check for malware if file-based scan
     */
    @Builder.Default
    private Boolean performMalwareCheck = true;

    /**
     * Timeout for scan in minutes
     */
    @Builder.Default
    private Integer timeoutMinutes = 30;

    /**
     * Session ID for tracking
     */
    private String sessionId;
}
