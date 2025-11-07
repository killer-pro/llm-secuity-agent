package com.security.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * System Command Tool
 * Allows safe execution of common system commands for security analysis
 */
@Slf4j
@Service
public class SystemCommandTool {

    private static final long COMMAND_TIMEOUT_MS = 30000; // 30 seconds
    private static final String[] ALLOWED_COMMANDS = {
        "ls", "dir", "pwd", "cd", "cat", "type", "find", "where", "which",
        "whoami", "hostname", "uname", "systeminfo", "tasklist", "ps",
        "netstat", "ss", "ipconfig", "ifconfig", "ping", "tracert", "traceroute"
    };

    private static final String[] BLOCKED_KEYWORDS = {
        "rm -rf", "del /s", "format", "wipe", "shutdown", "reboot",
        "; rm", "&& rm", "| rm", ">", "<"
    };

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String currentDirectory = System.getProperty("user.dir");

    /**
     * Execute a command safely
     * @param command Command to execute
     * @return Command output
     */
    public String executeCommand(String command) {
        try {
            // Validate command safety
            if (!isCommandSafe(command)) {
                return createErrorResponse("Command is not allowed for security reasons");
            }

            log.info("Executing command: {}", sanitizeForLog(command));

            ProcessBuilder pb;
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                pb = new ProcessBuilder("/bin/sh", "-c", command);
            }

            pb.directory(new File(currentDirectory));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Read output with timeout
            StringBuilder output = new StringBuilder();
            boolean completed = process.waitFor(COMMAND_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);

            if (!completed) {
                process.destroy();
                return createErrorResponse("Command execution timeout after " + COMMAND_TIMEOUT_MS + "ms");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.exitValue();
            log.debug("Command completed with exit code: {}", exitCode);

            return createSuccessResponse(Map.of(
                "output", output.toString().trim(),
                "exitCode", exitCode,
                "success", exitCode == 0
            ));
        } catch (Exception e) {
            log.error("Error executing command: {}", e.getMessage());
            return createErrorResponse("Execution error: " + e.getMessage());
        }
    }

    /**
     * Change directory
     * @param path Directory path
     * @return Success status
     */
    public String changeDirectory(String path) {
        try {
            File newDir = new File(path);

            if (!newDir.exists()) {
                return createErrorResponse("Directory does not exist: " + path);
            }

            if (!newDir.isDirectory()) {
                return createErrorResponse("Path is not a directory: " + path);
            }

            currentDirectory = newDir.getAbsolutePath();
            log.info("Changed directory to: {}", currentDirectory);

            return createSuccessResponse(Map.of(
                "currentDirectory", currentDirectory,
                "message", "Directory changed successfully"
            ));
        } catch (Exception e) {
            log.error("Error changing directory: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Get current directory
     * @return Current working directory
     */
    public String getCurrentDirectory() {
        return createSuccessResponse(Map.of(
            "currentDirectory", currentDirectory
        ));
    }

    /**
     * List files in current directory
     * @return File listing
     */
    public String listDirectory() {
        try {
            File dir = new File(currentDirectory);
            File[] files = dir.listFiles();

            if (files == null) {
                return createErrorResponse("Cannot read directory: " + currentDirectory);
            }

            List<Map<String, Object>> fileList = new ArrayList<>();
            for (File file : files) {
                fileList.add(Map.of(
                    "name", file.getName(),
                    "isDirectory", file.isDirectory(),
                    "size", file.length(),
                    "lastModified", file.lastModified()
                ));
            }

            return createSuccessResponse(Map.of(
                "directory", currentDirectory,
                "fileCount", fileList.size(),
                "files", fileList
            ));
        } catch (Exception e) {
            log.error("Error listing directory: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Read file content
     * @param filePath Path to file
     * @param maxLines Maximum lines to read
     * @return File content
     */
    public String readFile(String filePath, Integer maxLines) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                return createErrorResponse("File not found: " + filePath);
            }

            if (!file.isFile()) {
                return createErrorResponse("Path is not a file: " + filePath);
            }

            if (file.length() > 10 * 1024 * 1024) { // 10 MB limit
                return createErrorResponse("File is too large: " + file.length() + " bytes");
            }

            List<String> lines = Files.readAllLines(Paths.get(filePath));
            int limit = Math.min(maxLines, lines.size());
            List<String> content = lines.subList(0, limit);

            return createSuccessResponse(Map.of(
                "filePath", filePath,
                "totalLines", lines.size(),
                "displayedLines", content.size(),
                "content", String.join("\n", content)
            ));
        } catch (Exception e) {
            log.error("Error reading file: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Get system information
     * @return System info
     */
    public String getSystemInfo() {
        try {
            Map<String, Object> sysInfo = new HashMap<>();
            sysInfo.put("os.name", System.getProperty("os.name"));
            sysInfo.put("os.version", System.getProperty("os.version"));
            sysInfo.put("os.arch", System.getProperty("os.arch"));
            sysInfo.put("java.version", System.getProperty("java.version"));
            sysInfo.put("java.vendor", System.getProperty("java.vendor"));
            sysInfo.put("user.name", System.getProperty("user.name"));
            sysInfo.put("user.home", System.getProperty("user.home"));
            sysInfo.put("user.dir", System.getProperty("user.dir"));
            sysInfo.put("java.io.tmpdir", System.getProperty("java.io.tmpdir"));
            sysInfo.put("total.memory", Runtime.getRuntime().totalMemory());
            sysInfo.put("max.memory", Runtime.getRuntime().maxMemory());
            sysInfo.put("free.memory", Runtime.getRuntime().freeMemory());

            return createSuccessResponse(sysInfo);
        } catch (Exception e) {
            log.error("Error getting system info: {}", e.getMessage());
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Check if command is safe to execute
     */
    private boolean isCommandSafe(String command) {
        // Block dangerous patterns
        String lowerCommand = command.toLowerCase();

        for (String blocked : BLOCKED_KEYWORDS) {
            if (lowerCommand.contains(blocked)) {
                log.warn("Command blocked - contains dangerous keyword: {}", blocked);
                return false;
            }
        }

        // Extract base command
        String baseCommand = command.split("\\s")[0].toLowerCase();

        // Check if command is in allowed list
        boolean isAllowed = false;
        for (String allowed : ALLOWED_COMMANDS) {
            if (baseCommand.equals(allowed) || baseCommand.endsWith("/" + allowed) || baseCommand.endsWith("\\" + allowed)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            log.warn("Command blocked - not in allowed commands list: {}", baseCommand);
            return false;
        }

        return true;
    }

    /**
     * Sanitize command for logging (hide sensitive info)
     */
    private String sanitizeForLog(String command) {
        return command.replaceAll("password|passwd|pwd|token|key|secret", "***");
    }

    private String createSuccessResponse(Object data) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                "status", "success",
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
