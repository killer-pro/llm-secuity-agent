package com.security.exception;

/**
 * Exception thrown when security tool operations fail.
 * Covers errors in Nmap, VirusTotal, CVE search, and other security tools.
 */
public class SecurityToolException extends RuntimeException {

    private String toolName;
    private String errorCode;
    private Integer exitCode;

    public SecurityToolException(String message) {
        super(message);
        this.errorCode = "TOOL_ERROR";
    }

    public SecurityToolException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TOOL_ERROR";
    }

    public SecurityToolException(String toolName, String message, String errorCode) {
        super("Tool: " + toolName + " - " + message);
        this.toolName = toolName;
        this.errorCode = errorCode;
    }

    public SecurityToolException(String toolName, String message, String errorCode, Integer exitCode) {
        super("Tool: " + toolName + " - " + message + " (Exit Code: " + exitCode + ")");
        this.toolName = toolName;
        this.errorCode = errorCode;
        this.exitCode = exitCode;
    }

    public SecurityToolException(String toolName, String message, String errorCode, Throwable cause) {
        super("Tool: " + toolName + " - " + message, cause);
        this.toolName = toolName;
        this.errorCode = errorCode;
    }

    public String getToolName() {
        return toolName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }
}
