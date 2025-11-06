package com.security.model.entity;

import com.security.model.enums.ScanType;
import com.security.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a security scan result.
 * Stores information about network scans, vulnerability assessments, and file analysis.
 */
@Entity
@Table(name = "scan_result", indexes = {
    @Index(name = "idx_scan_type", columnList = "scan_type"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of scan performed
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScanType scanType;

    /**
     * Current status of the scan
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /**
     * Target of the scan (IP, domain, file path, etc.)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String target;

    /**
     * Human-readable name for the scan
     */
    @Column(nullable = false)
    private String scanName;

    /**
     * Detailed description of scan results
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Raw output from scanning tools (Nmap, VirusTotal, etc.)
     */
    @Column(columnDefinition = "TEXT")
    private String rawOutput;

    /**
     * Structured JSON data from scan results
     */
    @Column(columnDefinition = "TEXT")
    private String jsonData;

    /**
     * Progress percentage (0-100)
     */
    @Column
    @Builder.Default
    private Integer progress = 0;

    /**
     * Number of hosts discovered
     */
    @Column
    @Builder.Default
    private Integer hostsDiscovered = 0;

    /**
     * Number of vulnerabilities found
     */
    @Column
    @Builder.Default
    private Integer vulnerabilitiesFound = 0;

    /**
     * Number of critical issues
     */
    @Column
    @Builder.Default
    private Integer criticalIssues = 0;

    /**
     * Maximum CVSS score found in results
     */
    @Column
    private Double maxCvssScore;

    /**
     * Duration of scan in seconds
     */
    @Column
    private Long durationSeconds;

    /**
     * Timestamp when scan was created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when scan started execution
     */
    @Column
    private LocalDateTime startedAt;

    /**
     * Timestamp when scan completed
     */
    @Column
    private LocalDateTime completedAt;

    /**
     * Error message if scan failed
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Associated CVE data from this scan
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "scan_result_cve_data",
        joinColumns = @JoinColumn(name = "scan_result_id"),
        inverseJoinColumns = @JoinColumn(name = "cve_data_id")
    )
    @Builder.Default
    private List<CVEData> cveData = new ArrayList<>();

    /**
     * Associated task details
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "task_id")
    private Task task;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TaskStatus.PENDING;
        }
    }
}
