package com.security.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Directory Scanner Tool
 * Advanced directory scanning with security analysis and threat detection
 */
@Slf4j
@Service
public class DirectoryScannerTool {

    private static final int MAX_SCAN_DEPTH = 10;
    private static final long SCAN_TIMEOUT_MS = 60000; // 1 minute
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Deep directory scan with security analysis
     * @param dirPath Root directory to scan
     * @return Comprehensive directory report
     */
    public String deepDirectoryScan(String dirPath) {
        long startTime = System.currentTimeMillis();
        try {
            Path rootPath = validatePath(dirPath);
            Map<String, Object> scanResult = new HashMap<>();

            AtomicInteger fileCount = new AtomicInteger(0);
            AtomicInteger dirCount = new AtomicInteger(0);
            long totalSize = 0;
            Map<String, Integer> permissions = new HashMap<>();
            List<Map<String, Object>> suspiciousFiles = new ArrayList<>();

            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (System.currentTimeMillis() - startTime > SCAN_TIMEOUT_MS) {
                        return FileVisitResult.TERMINATE;
                    }

                    fileCount.incrementAndGet();
                    try {
                        // Check for suspicious patterns
                        if (isSuspiciousFile(file)) {
                            suspiciousFiles.add(createSuspiciousFileInfo(file));
                        }
                    } catch (Exception e) {
                        log.warn("Error analyzing file: {}", file, e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (System.currentTimeMillis() - startTime > SCAN_TIMEOUT_MS) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    dirCount.incrementAndGet();
                    return FileVisitResult.CONTINUE;
                }
            });

            scanResult.put("rootDirectory", dirPath);
            scanResult.put("fileCount", fileCount.get());
            scanResult.put("directoryCount", dirCount.get());
            scanResult.put("suspiciousFileCount", suspiciousFiles.size());
            scanResult.put("suspiciousFiles", suspiciousFiles);
            scanResult.put("scanDurationMs", System.currentTimeMillis() - startTime);

            return createSuccessResponse("completed", scanResult);
        } catch (Exception e) {
            log.error("Error during directory scan: {}", e.getMessage());
            return createErrorResponse("Scan failed: " + e.getMessage());
        }
    }

    /**
     * Find hidden files (security concern)
     * @param dirPath Directory to scan
     * @return List of hidden files
     */
    public String findHiddenFiles(String dirPath) {
        try {
            Path rootPath = validatePath(dirPath);
            List<Map<String, Object>> hiddenFiles = new ArrayList<>();

            Files.walk(rootPath, MAX_SCAN_DEPTH)
                .filter(p -> {
                    try {
                        return Files.isHidden(p) || p.getFileName().toString().startsWith(".");
                    } catch (Exception e) {
                        return false;
                    }
                })
                .limit(100)
                .forEach(p -> {
                    try {
                        hiddenFiles.add(createFileDetailInfo(p));
                    } catch (Exception e) {
                        log.warn("Error reading hidden file: {}", p, e);
                    }
                });

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "hiddenFileCount", hiddenFiles.size(),
                "files", hiddenFiles
            ));
        } catch (Exception e) {
            log.error("Error finding hidden files: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Find executable files (potential threat vector)
     * @param dirPath Directory to scan
     * @return List of executable files
     */
    public String findExecutables(String dirPath) {
        try {
            Path rootPath = validatePath(dirPath);
            List<Map<String, Object>> executables = new ArrayList<>();

            Files.walk(rootPath, MAX_SCAN_DEPTH)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    try {
                        return Files.isExecutable(p) ||
                               isExecutableByExtension(p.toString());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .limit(100)
                .forEach(p -> {
                    try {
                        executables.add(createFileDetailInfo(p));
                    } catch (Exception e) {
                        log.warn("Error reading executable: {}", p, e);
                    }
                });

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "executableCount", executables.size(),
                "files", executables
            ));
        } catch (Exception e) {
            log.error("Error finding executables: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Find recently modified files
     * @param dirPath Directory to scan
     * @param minutesAgo Time window (in minutes)
     * @return List of recently modified files
     */
    public String findRecentlyModified(String dirPath, Integer minutesAgo) {
        try {
            Path rootPath = validatePath(dirPath);
            long cutoffTime = System.currentTimeMillis() - (minutesAgo * 60 * 1000L);
            List<Map<String, Object>> recentFiles = new ArrayList<>();

            Files.walk(rootPath, MAX_SCAN_DEPTH)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    try {
                        return Files.getLastModifiedTime(p).toMillis() > cutoffTime;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .limit(100)
                .forEach(p -> {
                    try {
                        recentFiles.add(createFileDetailInfo(p));
                    } catch (Exception e) {
                        log.warn("Error reading recent file: {}", p, e);
                    }
                });

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "minutesAgo", minutesAgo,
                "recentFileCount", recentFiles.size(),
                "files", recentFiles
            ));
        } catch (Exception e) {
            log.error("Error finding recent files: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Permission analysis across directory
     * @param dirPath Directory to analyze
     * @return Permission statistics and anomalies
     */
    public String analyzePermissions(String dirPath) {
        try {
            Path rootPath = validatePath(dirPath);
            Map<String, Integer> permissionCounts = new HashMap<>();
            List<Map<String, Object>> anomalies = new ArrayList<>();

            Files.walk(rootPath, MAX_SCAN_DEPTH)
                .limit(200)
                .forEach(p -> {
                    try {
                        Set<PosixFilePermission> perms = Files.getPosixFilePermissions(p);
                        String permString = formatPermissions(perms);
                        permissionCounts.merge(permString, 1, Integer::sum);

                        // Detect anomalies (world-writable, etc.)
                        if (isAnomalousPermission(perms)) {
                            anomalies.add(Map.of(
                                "path", p.toString(),
                                "permissions", permString,
                                "type", getAnomalyType(perms)
                            ));
                        }
                    } catch (Exception e) {
                        // POSIX permissions not supported on all systems
                        log.debug("Cannot read POSIX permissions: {}", p);
                    }
                });

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "permissionPatterns", permissionCounts,
                "anomalies", anomalies,
                "anomalyCount", anomalies.size()
            ));
        } catch (Exception e) {
            log.error("Error analyzing permissions: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    // Helper methods

    private Path validatePath(String dirPath) throws Exception {
        Path path = Paths.get(dirPath).toRealPath();
        if (path.toString().contains("..")) {
            throw new SecurityException("Directory traversal not allowed");
        }
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path is not a directory: " + dirPath);
        }
        return path;
    }

    private boolean isSuspiciousFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".exe") ||
               fileName.endsWith(".bat") ||
               fileName.endsWith(".cmd") ||
               fileName.endsWith(".ps1") ||
               fileName.endsWith(".scr") ||
               fileName.endsWith(".vbs") ||
               fileName.endsWith(".jar") ||
               fileName.contains("malware") ||
               fileName.contains("virus") ||
               fileName.contains("trojan");
    }

    private boolean isExecutableByExtension(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".exe") ||
               lower.endsWith(".bin") ||
               lower.endsWith(".sh") ||
               lower.endsWith(".bat") ||
               lower.endsWith(".cmd") ||
               lower.endsWith(".ps1");
    }

    private Map<String, Object> createSuspiciousFileInfo(Path file) throws Exception {
        return Map.of(
            "name", file.getFileName().toString(),
            "path", file.toString(),
            "size", Files.size(file),
            "lastModified", Files.getLastModifiedTime(file).toString()
        );
    }

    private Map<String, Object> createFileDetailInfo(Path file) throws Exception {
        return Map.of(
            "name", file.getFileName().toString(),
            "path", file.toString(),
            "isDirectory", Files.isDirectory(file),
            "size", Files.size(file),
            "lastModified", Files.getLastModifiedTime(file).toString(),
            "isExecutable", Files.isExecutable(file)
        );
    }

    private String formatPermissions(Set<PosixFilePermission> perms) {
        StringBuilder sb = new StringBuilder();
        for (PosixFilePermission perm : PosixFilePermission.values()) {
            sb.append(perms.contains(perm) ? perm.toString().charAt(0) : '-');
        }
        return sb.toString();
    }

    private boolean isAnomalousPermission(Set<PosixFilePermission> perms) {
        // World-writable
        if (perms.contains(PosixFilePermission.OTHERS_WRITE)) {
            return true;
        }
        // Everyone can read and write
        return perms.contains(PosixFilePermission.OTHERS_READ) &&
               perms.contains(PosixFilePermission.OTHERS_WRITE);
    }

    private String getAnomalyType(Set<PosixFilePermission> perms) {
        if (perms.contains(PosixFilePermission.OTHERS_WRITE)) {
            return "world-writable";
        }
        return "suspicious-permissions";
    }

    private String createSuccessResponse(String status, Object data) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                "status", status,
                "data", data
            ));
        } catch (Exception e) {
            return "{\"status\": \"error\", \"error\": \"Serialization failed\"}";
        }
    }

    private String createErrorResponse(String message) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                "status", "error",
                "error", message
            ));
        } catch (Exception e) {
            return "{\"status\": \"error\", \"error\": \"" + message + "\"}";
        }
    }
}
