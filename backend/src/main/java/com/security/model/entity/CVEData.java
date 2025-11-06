package com.security.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing CVE (Common Vulnerabilities and Exposures) data.
 * Stores information about known vulnerabilities from the NVD database.
 */
@Entity
@Table(name = "cve_data", indexes = {
    @Index(name = "idx_cve_id", columnList = "cve_id"),
    @Index(name = "idx_cvss_score", columnList = "cvss_score"),
    @Index(name = "idx_severity", columnList = "severity")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CVEData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * CVE identifier (e.g., CVE-2024-1234)
     */
    @Column(nullable = false, unique = true)
    private String cveId;

    /**
     * Description of the vulnerability
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * CVSS v3.1 Score (0.0-10.0)
     */
    @Column
    private Double cvssScore;

    /**
     * CVSS v3.1 Vector string
     */
    @Column
    private String cvssVector;

    /**
     * Severity rating (LOW, MEDIUM, HIGH, CRITICAL)
     */
    @Column
    private String severity;

    /**
     * CWE (Common Weakness Enumeration) IDs
     */
    @Column(columnDefinition = "TEXT")
    private String cweIds;

    /**
     * Reference URLs for the CVE
     */
    @Column(name = "reference_urls", columnDefinition = "TEXT")
    private String references;

    /**
     * Published date of the CVE
     */
    @Column
    private LocalDateTime publishedDate;

    /**
     * Last modified date of the CVE
     */
    @Column
    private LocalDateTime lastModifiedDate;

    /**
     * Affected product/software
     */
    @Column(columnDefinition = "TEXT")
    private String affectedProducts;

    /**
     * Recommended mitigation or patches
     */
    @Column(columnDefinition = "TEXT")
    private String mitigation;

    /**
     * Full CVE details in JSON format
     */
    @Column(columnDefinition = "TEXT")
    private String jsonData;

    /**
     * Whether this CVE has been indexed in vector store
     */
    @Column
    @Builder.Default
    private Boolean indexed = false;

    /**
     * Timestamp when this record was created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this record was last updated
     */
    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
