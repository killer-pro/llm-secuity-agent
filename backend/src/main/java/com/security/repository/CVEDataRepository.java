package com.security.repository;

import com.security.model.entity.CVEData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CVE Data Repository
 * Manages CVE data persistence and queries
 */
@Repository
public interface CVEDataRepository extends JpaRepository<CVEData, Long> {

    /**
     * Find CVE by CVE ID
     */
    Optional<CVEData> findByCveId(String cveId);

    /**
     * Check if CVE exists
     */
    boolean existsByCveId(String cveId);

    /**
     * Find CVEs by severity
     */
    List<CVEData> findBySeverity(String severity);

    /**
     * Find CVEs by CVSS score range
     */
    List<CVEData> findByCvssScoreBetween(Double minScore, Double maxScore);

    /**
     * Find CVEs containing a keyword
     */
    List<CVEData> findByDescriptionContainingIgnoreCase(String keyword);

    /**
     * Find unindexed CVEs
     */
    List<CVEData> findByIndexedFalse();
}
