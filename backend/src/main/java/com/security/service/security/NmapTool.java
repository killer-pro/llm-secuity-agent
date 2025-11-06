package com.security.service.security;

import com.security.exception.SecurityToolException;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Nmap network scanning tool for LangChain4j agent.
 * Provides network discovery, port scanning, and service detection capabilities.
 * SECURITY: Input validation is enforced to prevent command injection.
 */
@Slf4j
@Component
public class NmapTool {

    @Value("${security.scan.nmap.path:/usr/bin/nmap}")
    private String nmapPath;

    @Value("${security.scan.allowed-ranges:192.168.0.0/16,10.0.0.0/8,172.16.0.0/12}")
    private String allowedRanges;

    @Value("${security.scan.timeout-minutes:30}")
    private Integer timeoutMinutes;

    private static final Set<String> ALLOWED_ARGS = new HashSet<>(Arrays.asList(
            "-sV", "-sC", "-p-", "-p", "-sn", "-sS", "-sT", "-O", "-A",
            "-T0", "-T1", "-T2", "-T3", "-T4", "-T5", "-v", "-oX", "-oN", "-oG", "--reason", "--open"
    ));

    @Tool("Performs a quick ping scan to discover active hosts on the network. Example: scanQuick('192.168.1.0/24')")
    public String scanQuick(String target) {
        log.info("Executing quick Nmap scan on target: {}", target);
        validateTarget(target);
        return executeScan(target, "-sn");
    }

    @Tool("Performs a comprehensive Nmap scan with service and OS detection. Example: scanFull('192.168.1.10')")
    public String scanFull(String target) {
        log.info("Executing full Nmap scan on target: {}", target);
        validateTarget(target);
        return executeScan(target, "-sV", "-sC", "-O", "-A");
    }

    @Tool("Detects services and versions running on a target. Example: scanServices('192.168.1.10')")
    public String scanServices(String target) {
        log.info("Executing service detection scan on target: {}", target);
        validateTarget(target);
        return executeScan(target, "-sV", "-p-");
    }

    @Tool("Scans specific ports on a target. Example: scanPorts('192.168.1.10', '22,80,443,3306')")
    public String scanPorts(String target, String ports) {
        log.info("Executing port scan on target: {} with ports: {}", target, ports);
        validateTarget(target);
        validatePorts(ports);
        return executeScan(target, "-p" + ports, "-sV");
    }

    @Tool("Attempts to detect the operating system of the target. Example: detectOS('192.168.1.10')")
    public String detectOS(String target) {
        log.info("Executing OS detection scan on target: {}", target);
        validateTarget(target);
        return executeScan(target, "-O", "-sV", "-sC");
    }

    private String executeScan(String target, String... args) {
        try {
            log.info("Executing Nmap scan on: {}", target);
            // Mock response for now
            return "Nmap scan started for target: " + target + "\nDetected hosts: 1\nOpen ports: 22, 80, 443";
        } catch (Exception e) {
            log.error("Nmap scan failed", e);
            throw new SecurityToolException("Nmap", "Failed to execute scan: " + e.getMessage(), "NMAP_EXECUTION_ERROR", e);
        }
    }

    private void validateTarget(String target) {
        if (!target.matches("^[\\d./:a-zA-Z-]+$")) {
            throw new SecurityToolException("Nmap", "Invalid target format: " + target, "INVALID_TARGET");
        }
    }

    private void validatePorts(String ports) {
        if (!ports.matches("^[0-9,\\-]+$")) {
            throw new SecurityToolException("Nmap", "Invalid port specification: " + ports, "INVALID_PORTS");
        }
    }
}
