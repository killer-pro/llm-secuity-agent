package com.security.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File Explorer Tool
 * Allows the agent to explore directory structures, find files, and analyze file properties
 */
@Slf4j
@Service
public class FileExplorerTool {

    private static final int MAX_DEPTH = 5;
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB
    private static final int MAX_FILES_RETURN = 50;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * List files in a directory
     * @param dirPath Directory path
     * @param recursive Include subdirectories
     * @return List of files with metadata
     */
    @Tool("Lists files in a directory. Example: listFiles('C:/Users/Alice/Desktop', false)")
    public String listFiles(String dirPath, Boolean recursive) {
        try {
            Path path = validateAndResolvePath(dirPath);

            if (!Files.isDirectory(path)) {
                return createErrorResponse("Path is not a directory: " + dirPath);
            }

            List<Map<String, Object>> files = new ArrayList<>();
            int depth = recursive ? MAX_DEPTH : 1;

            Files.walk(path, depth)
                .filter(p -> !p.equals(path))
                .limit(MAX_FILES_RETURN)
                .forEach(p -> {
                    try {
                        files.add(createFileInfo(p));
                    } catch (IOException e) {
                        log.warn("Error reading file info: {}", p, e);
                    }
                });

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "fileCount", files.size(),
                "files", files,
                "recursive", recursive
            ));
        } catch (Exception e) {
            log.error("Error listing files in {}: {}", dirPath, e.getMessage());
            return createErrorResponse("Error listing files: " + e.getMessage());
        }
    }

    /**
     * Search for files by pattern
     * @param dirPath Search directory
     * @param pattern File name pattern (supports wildcards)
     * @return List of matching files
     */
    @Tool("Searches for files matching a pattern. Example: searchFiles('C:/logs', '*.log')")
    public String searchFiles(String dirPath, String pattern) {
        try {
            Path path = validateAndResolvePath(dirPath);

            if (!Files.isDirectory(path)) {
                return createErrorResponse("Path is not a directory: " + dirPath);
            }

            List<Map<String, Object>> matches = new ArrayList<>();
            String regex = patternToRegex(pattern);

            Files.walk(path, MAX_DEPTH)
                .filter(p -> !p.equals(path))
                .filter(p -> p.getFileName().toString().matches(regex))
                .limit(MAX_FILES_RETURN)
                .forEach(p -> {
                    try {
                        matches.add(createFileInfo(p));
                    } catch (IOException e) {
                        log.warn("Error reading file: {}", p, e);
                    }
                });

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "pattern", pattern,
                "matchCount", matches.size(),
                "matches", matches
            ));
        } catch (Exception e) {
            log.error("Error searching for {} in {}: {}", pattern, dirPath, e.getMessage());
            return createErrorResponse("Error searching: " + e.getMessage());
        }
    }

    /**
     * Get detailed file properties
     * @param filePath Path to file
     * @return Detailed file information
     */
    @Tool("Retrieves metadata for a file or directory. Example: getFileProperties('C:/Users/Alice/Desktop/report.pdf')")
    public String getFileProperties(String filePath) {
        try {
            Path path = validateAndResolvePath(filePath);

            if (!Files.exists(path)) {
                return createErrorResponse("File not found: " + filePath);
            }

            Map<String, Object> properties = createFileInfo(path);

            if (Files.isRegularFile(path)) {
                properties.put("extension", getFileExtension(path.toString()));
                properties.put("isBinary", isBinaryFile(path));
                properties.put("isExecutable", Files.isExecutable(path));
                properties.put("isReadable", Files.isReadable(path));
                properties.put("isWritable", Files.isWritable(path));
            }

            return createSuccessResponse("success", properties);
        } catch (Exception e) {
            log.error("Error getting properties for {}: {}", filePath, e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Find large files (potential security concerns)
     * @param dirPath Directory to scan
     * @param minSizeBytes Minimum file size
     * @return List of large files
     */
    @Tool("Finds files larger than a given size in bytes. Example: findLargeFiles('C:/backups', 10485760)")
    public String findLargeFiles(String dirPath, Long minSizeBytes) {
        try {
            Path path = validateAndResolvePath(dirPath);

            if (!Files.isDirectory(path)) {
                return createErrorResponse("Path is not a directory: " + dirPath);
            }

            List<Map<String, Object>> largeFiles = Files.walk(path, MAX_DEPTH)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    try {
                        return Files.size(p) >= minSizeBytes;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .limit(MAX_FILES_RETURN)
                .map(p -> {
                    try {
                        return createFileInfo(p);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> {
                    long sizeA = ((Number) a.get("size")).longValue();
                    long sizeB = ((Number) b.get("size")).longValue();
                    return Long.compare(sizeB, sizeA);
                })
                .collect(Collectors.toList());

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "minSizeBytes", minSizeBytes,
                "largeFileCount", largeFiles.size(),
                "files", largeFiles
            ));
        } catch (Exception e) {
            log.error("Error finding large files: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Count files by type in directory
     * @param dirPath Directory to analyze
     * @return File type statistics
     */
    @Tool("Analyzes file types and counts within a directory. Example: analyzeFileTypes('C:/projects')")
    public String analyzeFileTypes(String dirPath) {
        try {
            Path path = validateAndResolvePath(dirPath);

            if (!Files.isDirectory(path)) {
                return createErrorResponse("Path is not a directory: " + dirPath);
            }

            Map<String, Integer> typeCount = new HashMap<>();
            long totalSize = 0;
            long fileCount = 0;

            List<Path> files = Files.walk(path, MAX_DEPTH)
                .filter(Files::isRegularFile)
                .limit(MAX_FILES_RETURN * 2)
                .collect(Collectors.toList());

            for (Path p : files) {
                try {
                    String ext = getFileExtension(p.toString());
                    String type = ext.isEmpty() ? "no-extension" : ext;
                    typeCount.merge(type, 1, Integer::sum);
                    totalSize += Files.size(p);
                    fileCount++;
                } catch (IOException e) {
                    log.warn("Error processing file: {}", p, e);
                }
            }

            return createSuccessResponse("success", Map.of(
                "directory", dirPath,
                "fileCount", fileCount,
                "totalSizeBytes", totalSize,
                "fileTypes", typeCount
            ));
        } catch (Exception e) {
            log.error("Error analyzing file types: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    // Helper methods

    private Path validateAndResolvePath(String dirPath) throws IOException {
        Path path = Paths.get(dirPath).toRealPath();
        // Security check: prevent directory traversal
        if (path.toString().contains("..")) {
            throw new SecurityException("Directory traversal not allowed");
        }
        return path;
    }

    private Map<String, Object> createFileInfo(Path path) throws IOException {
        Map<String, Object> info = new HashMap<>();
        info.put("name", path.getFileName().toString());
        info.put("path", path.toString());
        info.put("isDirectory", Files.isDirectory(path));
        info.put("size", Files.size(path));
        info.put("lastModified", Files.getLastModifiedTime(path).toString());
        return info;
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }

    private boolean isBinaryFile(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            if (bytes.length == 0) return false;
            int nonTextCount = 0;
            for (int i = 0; i < Math.min(bytes.length, 512); i++) {
                byte b = bytes[i];
                if (b < 0x09 || (b > 0x0d && b < 0x20) || (b > 0x7e && b < 0x80)) {
                    nonTextCount++;
                }
            }
            return (nonTextCount * 100) / Math.min(bytes.length, 512) > 30;
        } catch (IOException e) {
            return true;
        }
    }

    private String patternToRegex(String pattern) {
        return pattern
            .replace(".", "\\.")
            .replace("*", ".*")
            .replace("?", ".");
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
