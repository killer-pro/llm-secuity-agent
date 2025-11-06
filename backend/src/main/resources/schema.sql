-- ============================================
-- Security AI Platform - Database Schema
-- PostgreSQL Database Script
-- ============================================

-- Drop existing schema if it exists
DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

-- ============================================
-- 1. TASK TABLE (Tracks AI Agent Tasks)
-- ============================================
CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    tool_type VARCHAR(255),
    tool_input TEXT,
    tool_output TEXT,
    agent_response TEXT,
    progress INTEGER DEFAULT 0,
    error_message TEXT,
    execution_time_ms BIGINT,
    session_id VARCHAR(255),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_task_status ON task(status);
CREATE INDEX idx_task_session_id ON task(session_id);
CREATE INDEX idx_task_created_at ON task(created_at);

-- ============================================
-- 2. CVE_DATA TABLE (Common Vulnerabilities and Exposures)
-- ============================================
CREATE TABLE cve_data (
    id BIGSERIAL PRIMARY KEY,
    cve_id VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    cvss_score DOUBLE PRECISION,
    cvss_vector VARCHAR(255),
    severity VARCHAR(50),
    cwe_ids TEXT,
    reference_urls TEXT,
    published_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    affected_products TEXT,
    mitigation TEXT,
    json_data TEXT,
    indexed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cve_data_cve_id ON cve_data(cve_id);
CREATE INDEX idx_cve_data_cvss_score ON cve_data(cvss_score);
CREATE INDEX idx_cve_data_severity ON cve_data(severity);
CREATE INDEX idx_cve_data_published_date ON cve_data(published_date);

-- ============================================
-- 3. SCAN_RESULT TABLE (Security Scan Results)
-- ============================================
CREATE TABLE scan_result (
    id BIGSERIAL PRIMARY KEY,
    scan_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    target VARCHAR(255) NOT NULL,
    scan_name VARCHAR(255) NOT NULL,
    description TEXT,
    raw_output TEXT,
    json_data TEXT,
    progress INTEGER DEFAULT 0,
    hosts_discovered INTEGER DEFAULT 0,
    vulnerabilities_found INTEGER DEFAULT 0,
    critical_issues INTEGER DEFAULT 0,
    max_cvss_score DOUBLE PRECISION,
    duration_seconds BIGINT,
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    task_id BIGINT,
    FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE SET NULL
);

CREATE INDEX idx_scan_result_status ON scan_result(status);
CREATE INDEX idx_scan_result_scan_type ON scan_result(scan_type);
CREATE INDEX idx_scan_result_target ON scan_result(target);
CREATE INDEX idx_scan_result_created_at ON scan_result(created_at);

-- ============================================
-- 4. SCAN_RESULT_CVE_DATA JUNCTION TABLE
-- ============================================
CREATE TABLE scan_result_cve_data (
    scan_result_id BIGINT NOT NULL,
    cve_data_id BIGINT NOT NULL,
    PRIMARY KEY (scan_result_id, cve_data_id),
    FOREIGN KEY (scan_result_id) REFERENCES scan_result(id) ON DELETE CASCADE,
    FOREIGN KEY (cve_data_id) REFERENCES cve_data(id) ON DELETE CASCADE
);

CREATE INDEX idx_scan_cve_scan_result ON scan_result_cve_data(scan_result_id);
CREATE INDEX idx_scan_cve_cve_data ON scan_result_cve_data(cve_data_id);

-- ============================================
-- 5. SCAN_HOST TABLE (Individual Hosts in Scans)
-- ============================================
CREATE TABLE scan_host (
    id BIGSERIAL PRIMARY KEY,
    scan_result_id BIGINT NOT NULL,
    host_ip VARCHAR(50) NOT NULL,
    hostname VARCHAR(255),
    os_detected VARCHAR(255),
    os_confidence INTEGER,
    uptime VARCHAR(255),
    status VARCHAR(50),
    open_ports_count INTEGER DEFAULT 0,
    vulnerability_count INTEGER DEFAULT 0,
    max_cvss DECIMAL(3,1),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (scan_result_id) REFERENCES scan_result(id) ON DELETE CASCADE
);

CREATE INDEX idx_scan_host_scan_result ON scan_host(scan_result_id);
CREATE INDEX idx_scan_host_host_ip ON scan_host(host_ip);

-- ============================================
-- 6. SCAN_PORT TABLE (Ports Found in Scans)
-- ============================================
CREATE TABLE scan_port (
    id BIGSERIAL PRIMARY KEY,
    scan_host_id BIGINT NOT NULL,
    port_number INTEGER NOT NULL,
    protocol VARCHAR(10),
    state VARCHAR(50),
    service_name VARCHAR(255),
    service_version VARCHAR(255),
    product VARCHAR(255),
    extrainfo TEXT,
    cpe TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (scan_host_id) REFERENCES scan_host(id) ON DELETE CASCADE
);

CREATE INDEX idx_scan_port_scan_host ON scan_port(scan_host_id);
CREATE INDEX idx_scan_port_port_number ON scan_port(port_number);
CREATE INDEX idx_scan_port_state ON scan_port(state);

-- ============================================
-- 7. VIRUSTOTAL_SCAN TABLE (File/URL Analysis)
-- ============================================
CREATE TABLE virustotal_scan (
    id BIGSERIAL PRIMARY KEY,
    scan_type VARCHAR(50),
    target VARCHAR(500) NOT NULL,
    hash VARCHAR(255),
    scan_id VARCHAR(255),
    status VARCHAR(50),
    detection_ratio VARCHAR(50),
    engine_results TEXT,
    verdict VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_virustotal_target ON virustotal_scan(target);
CREATE INDEX idx_virustotal_hash ON virustotal_scan(hash);
CREATE INDEX idx_virustotal_status ON virustotal_scan(status);

-- ============================================
-- 8. CVE_KNOWLEDGE_BASE TABLE (Vector Store)
-- ============================================
CREATE TABLE cve_knowledge_base (
    id BIGSERIAL PRIMARY KEY,
    cve_id VARCHAR(100) NOT NULL,
    embedding_id VARCHAR(255),
    text_segment TEXT NOT NULL,
    vector FLOAT8[],
    indexed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cve_id) REFERENCES cve_data(cve_id) ON DELETE CASCADE
);

CREATE INDEX idx_cve_kb_cve_id ON cve_knowledge_base(cve_id);
CREATE INDEX idx_cve_kb_indexed ON cve_knowledge_base(indexed);

-- ============================================
-- 9. AUDIT_LOG TABLE (Security Audit Trail)
-- ============================================
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    details TEXT,
    user_ip VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_log_action ON audit_log(action);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);

-- ============================================
-- 10. API_USAGE_STATS TABLE (Rate Limiting)
-- ============================================
CREATE TABLE api_usage_stats (
    id BIGSERIAL PRIMARY KEY,
    endpoint VARCHAR(255) NOT NULL,
    request_count INTEGER DEFAULT 0,
    error_count INTEGER DEFAULT 0,
    total_response_time_ms BIGINT DEFAULT 0,
    avg_response_time_ms DECIMAL(10,2),
    last_request TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_usage_endpoint ON api_usage_stats(endpoint);

-- ============================================
-- 11. VULNERABILITY_CACHE TABLE (Performance)
-- ============================================
CREATE TABLE vulnerability_cache (
    id BIGSERIAL PRIMARY KEY,
    query_hash VARCHAR(255) NOT NULL UNIQUE,
    query_text TEXT NOT NULL,
    results TEXT NOT NULL,
    result_count INTEGER,
    cache_ttl_minutes INTEGER DEFAULT 1440,
    hits_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_vuln_cache_query_hash ON vulnerability_cache(query_hash);
CREATE INDEX idx_vuln_cache_expires_at ON vulnerability_cache(expires_at);

-- ============================================
-- 12. SYSTEM_CONFIG TABLE
-- ============================================
CREATE TABLE system_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT,
    description TEXT,
    is_sensitive BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_config_key ON system_config(config_key);

-- ============================================
-- Insert Initial System Configuration
-- ============================================
INSERT INTO system_config (config_key, config_value, description) VALUES
('nmap_enabled', 'true', 'Enable/disable Nmap scanning'),
('virustotal_enabled', 'true', 'Enable/disable VirusTotal integration'),
('cve_search_enabled', 'true', 'Enable/disable CVE searching'),
('max_scan_duration_minutes', '30', 'Maximum duration for a security scan'),
('max_concurrent_scans', '5', 'Maximum number of concurrent scans'),
('cve_cache_ttl_hours', '24', 'CVE data cache TTL in hours');

-- ============================================
-- Create Views for Common Queries
-- ============================================

-- View: Scan Summary
CREATE VIEW v_scan_summary AS
SELECT
    sr.id,
    sr.scan_name,
    sr.scan_type,
    sr.status,
    sr.target,
    sr.hosts_discovered,
    sr.vulnerabilities_found,
    sr.critical_issues,
    sr.max_cvss_score,
    sr.created_at,
    sr.started_at,
    sr.completed_at,
    EXTRACT(EPOCH FROM (sr.completed_at - sr.started_at))::INTEGER as duration_seconds
FROM scan_result sr
ORDER BY sr.created_at DESC;

-- View: Critical Vulnerabilities
CREATE VIEW v_critical_vulnerabilities AS
SELECT
    cd.cve_id,
    cd.description,
    cd.cvss_score,
    cd.severity,
    cd.affected_products,
    cd.published_date,
    COUNT(DISTINCT src.scan_result_id) as scan_count
FROM cve_data cd
LEFT JOIN scan_result_cve_data src ON cd.id = src.cve_data_id
WHERE cd.severity IN ('CRITICAL', 'HIGH')
GROUP BY cd.id, cd.cve_id, cd.description, cd.cvss_score, cd.severity,
         cd.affected_products, cd.published_date
ORDER BY cd.cvss_score DESC;

-- View: Recent Scans
CREATE VIEW v_recent_scans AS
SELECT
    sr.id,
    sr.scan_name,
    sr.scan_type,
    sr.target,
    sr.status,
    sr.vulnerabilities_found,
    sr.critical_issues,
    sr.max_cvss_score,
    sr.created_at
FROM scan_result sr
ORDER BY sr.created_at DESC
LIMIT 50;

-- ============================================
-- Set Default Permissions
-- ============================================
GRANT ALL PRIVILEGES ON SCHEMA public TO admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO admin;

-- ============================================
-- End of Schema Script
-- ============================================
