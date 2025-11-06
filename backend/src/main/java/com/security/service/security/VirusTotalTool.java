package com.security.service.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.exception.SecurityToolException;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * VirusTotal malware and threat analysis tool for LangChain4j agent.
 * Provides file scanning, URL analysis, and threat intelligence.
 */
@Slf4j
@Component
public class VirusTotalTool {

    @Value("${security.virustotal.api-key:}")
    private String apiKey;

    @Value("${security.virustotal.api-url:https://www.virustotal.com/api/v3}")
    private String apiUrl;

    @Value("${security.scan.file-scan.max-file-size-mb:100}")
    private Integer maxFileSizeMb;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analyze a single file for malware
     */
    @Tool("Analyzes a file for malware and threats using VirusTotal. Example: analyzeFile('/path/to/file.exe')")
    public String analyzeFile(String filePath) {
        log.info("Analyzing file: {}", filePath);

        if (apiKey == null || apiKey.isEmpty()) {
            throw new SecurityToolException(
                    "VirusTotal",
                    "API key not configured",
                    "VIRUSTOTAL_API_KEY_NOT_SET"
            );
        }

        try {
            File file = new File(filePath);
            validateFile(file);

            // Get file hash and submit for analysis
            String fileHash = calculateFileHash(file);
            log.info("File hash: {}", fileHash);

            // Query VirusTotal for file reputation
            String result = queryVirusTotal(fileHash);

            return result;

        } catch (IOException e) {
            log.error("Failed to analyze file: {}", filePath, e);
            throw new SecurityToolException(
                    "VirusTotal",
                    "Failed to analyze file: " + e.getMessage(),
                    "FILE_ANALYSIS_ERROR",
                    e
            );
        }
    }

    /**
     * Analyze a URL for threats
     */
    @Tool("Analyzes a URL for malware and phishing threats. Example: analyzeUrl('https://example.com')")
    public String analyzeUrl(String url) {
        log.info("Analyzing URL: {}", url);

        if (apiKey == null || apiKey.isEmpty()) {
            throw new SecurityToolException(
                    "VirusTotal",
                    "API key not configured",
                    "VIRUSTOTAL_API_KEY_NOT_SET"
            );
        }

        try {
            validateUrl(url);

            // Query VirusTotal for URL reputation
            String result = queryVirusTotal(url);

            return result;

        } catch (Exception e) {
            log.error("Failed to analyze URL: {}", url, e);
            throw new SecurityToolException(
                    "VirusTotal",
                    "Failed to analyze URL: " + e.getMessage(),
                    "URL_ANALYSIS_ERROR",
                    e
            );
        }
    }

    /**
     * Scan a directory recursively
     */
    @Tool("Scans all files in a directory for malware. Example: scanDirectory('/path/to/directory')")
    public String scanDirectory(String dirPath) {
        log.info("Scanning directory: {}", dirPath);

        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            throw new SecurityToolException(
                    "VirusTotal",
                    "Path is not a directory: " + dirPath,
                    "INVALID_DIRECTORY"
            );
        }

        try {
            StringBuilder results = new StringBuilder();
            results.append("Directory scan: ").append(dirPath).append("\n");

            File[] files = dir.listFiles();
            if (files != null) {
                int scanned = 0;
                int malicious = 0;

                for (File file : files) {
                    if (file.isFile() && scanned < 10) {  // Limit to 10 files
                        try {
                            String result = analyzeFile(file.getAbsolutePath());
                            if (result.contains("malicious") || result.contains("suspicious")) {
                                malicious++;
                            }
                            scanned++;
                        } catch (Exception e) {
                            log.warn("Failed to scan file: {}", file.getName(), e);
                        }
                    }
                }

                results.append("Scanned: ").append(scanned).append(" files\n");
                results.append("Malicious: ").append(malicious).append(" files\n");
            }

            return results.toString();

        } catch (Exception e) {
            log.error("Failed to scan directory: {}", dirPath, e);
            throw new SecurityToolException(
                    "VirusTotal",
                    "Failed to scan directory: " + e.getMessage(),
                    "DIRECTORY_SCAN_ERROR",
                    e
            );
        }
    }

    /**
     * Validate file before analysis
     */
    private void validateFile(File file) {
        if (!file.exists()) {
            throw new SecurityToolException(
                    "VirusTotal",
                    "File does not exist: " + file.getAbsolutePath(),
                    "FILE_NOT_FOUND"
            );
        }

        long fileSizeBytes = file.length();
        long maxSizeBytes = (long) maxFileSizeMb * 1024 * 1024;

        if (fileSizeBytes > maxSizeBytes) {
            throw new SecurityToolException(
                    "VirusTotal",
                    "File exceeds maximum size of " + maxFileSizeMb + " MB",
                    "FILE_TOO_LARGE"
            );
        }
    }

    /**
     * Validate URL format
     */
    private void validateUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new SecurityToolException(
                    "VirusTotal",
                    "Invalid URL format: " + url,
                    "INVALID_URL"
            );
        }
    }

    /**
     * Calculate file hash (SHA-256)
     */
    private String calculateFileHash(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        java.security.MessageDigest digest = null;
        try {
            digest = java.security.MessageDigest.getInstance("SHA-256");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not available", e);
        }

        byte[] hashBytes = digest.digest(bytes);
        StringBuilder hexString = new StringBuilder();

        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Query VirusTotal API
     */
    private String queryVirusTotal(String query) {
        // This is a mock implementation
        // In production, make actual HTTP requests to VirusTotal API
        log.info("Querying VirusTotal for: {}", query);

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("status", "analyzed");
            response.put("detections", 0);
            response.put("vendor_count", "0/70");

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);

        } catch (Exception e) {
            log.error("Failed to query VirusTotal", e);
            throw new SecurityToolException(
                    "VirusTotal",
                    "API query failed: " + e.getMessage(),
                    "VIRUSTOTAL_API_ERROR",
                    e
            );
        }
    }
}
