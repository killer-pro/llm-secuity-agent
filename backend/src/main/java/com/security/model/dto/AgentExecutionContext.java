package com.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent Execution Context
 * Maintains state during recursive reasoning iterations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentExecutionContext {

    /**
     * Unique execution ID
     */
    private String executionId;

    /**
     * Session/Conversation ID
     */
    private String sessionId;

    /**
     * Initial user prompt
     */
    private String userPrompt;

    /**
     * Current iteration number (0-based)
     */
    @Builder.Default
    private Integer currentIteration = 0;

    /**
     * Maximum iterations allowed
     */
    @Builder.Default
    private Integer maxIterations = 10;

    /**
     * Execution state
     */
    @Builder.Default
    private ExecutionState state = ExecutionState.INITIALIZED;

    /**
     * Conversation memory
     */
    private ConversationMemory memory;

    /**
     * Current thinking/reasoning
     */
    private String currentThinking;

    /**
     * Current action being taken
     */
    private String currentAction;

    /**
     * List of decisions made
     */
    @Builder.Default
    private List<Decision> decisions = new ArrayList<>();

    /**
     * List of errors encountered
     */
    @Builder.Default
    private List<ErrorRecord> errors = new ArrayList<>();

    /**
     * Total tokens used so far
     */
    @Builder.Default
    private Long tokensUsed = 0L;

    /**
     * Execution start time
     */
    private LocalDateTime startedAt;

    /**
     * Execution end time
     */
    private LocalDateTime completedAt;

    /**
     * Final response/result
     */
    private String finalResponse;

    /**
     * Whether execution succeeded
     */
    @Builder.Default
    private Boolean success = false;

    /**
     * Increment iteration and check if we've exceeded max
     */
    public void nextIteration() {
        currentIteration++;
    }

    /**
     * Check if max iterations exceeded
     */
    public Boolean shouldContinue() {
        return currentIteration < maxIterations && !hasError();
    }

    /**
     * Record a decision
     */
    public void recordDecision(String decision, String rationale) {
        decisions.add(Decision.builder()
            .iterationNumber(currentIteration)
            .decision(decision)
            .rationale(rationale)
            .timestamp(LocalDateTime.now())
            .build());
    }

    /**
     * Record an error
     */
    public void recordError(String toolName, String errorMessage) {
        errors.add(ErrorRecord.builder()
            .iterationNumber(currentIteration)
            .toolName(toolName)
            .errorMessage(errorMessage)
            .timestamp(LocalDateTime.now())
            .build());
    }

    /**
     * Check if any errors occurred
     */
    public Boolean hasError() {
        return !errors.isEmpty();
    }

    /**
     * Get execution duration in milliseconds
     */
    public Long getExecutionDurationMs() {
        if (startedAt == null) return 0L;
        LocalDateTime end = completedAt != null ? completedAt : LocalDateTime.now();
        return java.time.temporal.ChronoUnit.MILLIS.between(startedAt, end);
    }

    /**
     * Execution states
     */
    public enum ExecutionState {
        INITIALIZED,    // Just created
        THINKING,       // Agent is reasoning
        EXECUTING,      // Tools are executing
        EVALUATING,     // Agent evaluating results
        ITERATING,      // Starting next iteration
        COMPLETED,      // Execution finished
        FAILED          // Execution failed
    }

    /**
     * Decision record
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Decision {
        private Integer iterationNumber;
        private String decision;
        private String rationale;
        private LocalDateTime timestamp;
    }

    /**
     * Error record
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorRecord {
        private Integer iterationNumber;
        private String toolName;
        private String errorMessage;
        private LocalDateTime timestamp;
    }
}
