package com.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for agent responses sent to the client.
 * Contains the AI agent's response along with tool execution details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponse {

    /**
     * The main response message from the AI agent
     */
    private String response;

    /**
     * Session ID for conversation tracking
     */
    private String sessionId;

    /**
     * Task ID if this response originated from a tool execution
     */
    private Long taskId;

    /**
     * Details of tool executions performed
     */
    @Builder.Default
    private List<ToolExecutionDetail> toolExecutions = new ArrayList<>();

    /**
     * Whether the response is complete or still streaming
     */
    @Builder.Default
    private Boolean isComplete = true;

    /**
     * Metadata about scan results if applicable
     */
    private ScanMetadata scanMetadata;

    /**
     * Error message if agent failed
     */
    private String error;

    /**
     * Execution time in milliseconds
     */
    private Long executionTimeMs;

    /**
     * Nested class for tool execution details
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToolExecutionDetail {
        private String toolName;
        private String toolInput;
        private String toolOutput;
        private Long executionTimeMs;
        private Boolean success;
    }

    /**
     * Nested class for scan metadata
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScanMetadata {
        private Integer hostsDiscovered;
        private Integer vulnerabilitiesFound;
        private Integer criticalIssues;
        private Double maxCvssScore;
        private Long scanDurationSeconds;
    }
}
