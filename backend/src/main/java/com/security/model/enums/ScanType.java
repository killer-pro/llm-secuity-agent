package com.security.model.enums;

/**
 * Enumeration of supported security scan types.
 * Represents different types of network and vulnerability scans that can be performed.
 */
public enum ScanType {
    /**
     * Quick network scan to discover active hosts
     */
    QUICK("Quick Scan", "Discovers active hosts on the network"),

    /**
     * Comprehensive network scan with service and version detection
     */
    FULL("Full Scan", "Comprehensive scan with service detection and OS fingerprinting"),

    /**
     * Service-focused scan that detects running services and versions
     */
    SERVICE_DETECTION("Service Detection", "Detects services and their versions"),

    /**
     * Vulnerability-focused scan with CVE correlation
     */
    VULNERABILITY("Vulnerability Scan", "Detects vulnerabilities and correlates with CVEs"),

    /**
     * File analysis scan for malware detection
     */
    FILE_ANALYSIS("File Analysis", "Analyzes files for malware and suspicious behavior"),

    /**
     * Custom scan with user-defined parameters
     */
    CUSTOM("Custom Scan", "Custom scan with user-defined parameters");

    private final String displayName;
    private final String description;

    ScanType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
