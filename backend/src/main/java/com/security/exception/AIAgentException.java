package com.security.exception;

/**
 * Exception thrown when AI Agent operations fail.
 * Covers errors in LLM communication, tool execution, and response generation.
 */
public class AIAgentException extends RuntimeException {

    private String errorCode;
    private String details;

    public AIAgentException(String message) {
        super(message);
        this.errorCode = "AI_AGENT_ERROR";
    }

    public AIAgentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AI_AGENT_ERROR";
    }

    public AIAgentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AIAgentException(String message, String errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public AIAgentException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
