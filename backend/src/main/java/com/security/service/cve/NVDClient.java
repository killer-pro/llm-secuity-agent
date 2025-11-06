package com.security.service.cve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for National Vulnerability Database (NVD) API
 * Provides methods to search CVE data from NIST NVD
 */
@Slf4j
@Component
public class NVDClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cve.nvd.api-url:https://services.nvd.nist.gov/rest/json}")
    private String baseUrl;

    @Value("${cve.nvd.api-key:}")
    private String apiKey;

    public NVDClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Search CVEs by keyword
     */
    public JsonNode searchByKeyword(String keyword) {
        try {
            log.debug("Searching NVD for keyword: {}", keyword);

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/cves/2.0")
                    .queryParam("keywordSearch", keyword)
                    .queryParam("resultsPerPage", 20)
                    .queryParam("apiKey", apiKey)
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("Error searching NVD for keyword: {}", keyword, e);
            return null;
        }
    }

    /**
     * Get CVE details by CVE ID
     */
    public JsonNode getCVEById(String cveId) {
        try {
            log.debug("Fetching NVD CVE: {}", cveId);

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/cves/2.0")
                    .queryParam("cveId", cveId)
                    .queryParam("apiKey", apiKey)
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("Error fetching NVD CVE: {}", cveId, e);
            return null;
        }
    }

    /**
     * Search CVEs by CVSS score range
     */
    public JsonNode searchByCVSSScore(double minScore, double maxScore) {
        try {
            log.debug("Searching NVD for CVSS score range: {} - {}", minScore, maxScore);

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/cves/2.0")
                    .queryParam("cvssV3Severity", getSeverityFromScore(minScore, maxScore))
                    .queryParam("resultsPerPage", 20)
                    .queryParam("apiKey", apiKey)
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("Error searching NVD by CVSS score", e);
            return null;
        }
    }

    /**
     * Search CVEs by severity
     */
    public JsonNode searchBySeverity(String severity) {
        try {
            log.debug("Searching NVD for severity: {}", severity);

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/cves/2.0")
                    .queryParam("cvssV3Severity", severity)
                    .queryParam("resultsPerPage", 50)
                    .queryParam("apiKey", apiKey)
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("Error searching NVD by severity: {}", severity, e);
            return null;
        }
    }

    /**
     * Convert CVSS score range to severity level
     */
    private String getSeverityFromScore(double minScore, double maxScore) {
        if (maxScore >= 9.0) return "CRITICAL";
        if (maxScore >= 7.0) return "HIGH";
        if (maxScore >= 4.0) return "MEDIUM";
        return "LOW";
    }

    /**
     * Check API connectivity
     */
    public boolean isAvailable() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/cves/2.0")
                    .queryParam("resultsPerPage", 1)
                    .queryParam("apiKey", apiKey)
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            return response != null && !response.isEmpty();

        } catch (Exception e) {
            log.warn("NVD API is not available", e);
            return false;
        }
    }
}
