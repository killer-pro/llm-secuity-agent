package com.security.service.ai;

import com.security.model.dto.AgentExecutionContext;
import com.security.model.dto.ConversationMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Execution Orchestrator
 * Manages recursive reasoning with iterations, memory, and state tracking
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionOrchestrator {

    private final MemoryManager memoryManager;

    /**
     * Create new execution context for a request
     */
    public AgentExecutionContext createExecutionContext(String sessionId, String userPrompt) {
        String executionId = UUID.randomUUID().toString();
        ConversationMemory memory = memoryManager.getOrCreateMemory(sessionId);

        AgentExecutionContext context = AgentExecutionContext.builder()
            .executionId(executionId)
            .sessionId(sessionId)
            .userPrompt(userPrompt)
            .memory(memory)
            .startedAt(LocalDateTime.now())
            .state(AgentExecutionContext.ExecutionState.INITIALIZED)
            .build();

        // Add user message to memory
        memoryManager.addUserMessage(sessionId, userPrompt);

        log.info("Created execution context: {} for session: {}", executionId, sessionId);
        return context;
    }

    /**
     * Start iteration - update state and increment counter
     */
    public void startIteration(AgentExecutionContext context) {
        context.setState(AgentExecutionContext.ExecutionState.THINKING);
        log.debug("Starting iteration {} for execution: {}", context.getCurrentIteration(), context.getExecutionId());
    }

    /**
     * Set current thinking
     */
    public void setThinking(AgentExecutionContext context, String thinking) {
        context.setCurrentThinking(thinking);
        log.debug("Agent thinking (iteration {}): {}", context.getCurrentIteration(), thinking.substring(0, Math.min(100, thinking.length())));
    }

    /**
     * Set current action
     */
    public void setAction(AgentExecutionContext context, String action) {
        context.setCurrentAction(action);
        context.setState(AgentExecutionContext.ExecutionState.EXECUTING);
        log.debug("Agent executing action (iteration {}): {}", context.getCurrentIteration(), action.substring(0, Math.min(100, action.length())));
    }

    /**
     * Record tool execution
     */
    public void recordToolExecution(AgentExecutionContext context, String toolName, String toolType,
                                   String input, String output, String status, Long executionTimeMs) {
        memoryManager.recordToolExecution(
            context.getSessionId(),
            toolName,
            toolType,
            input,
            output,
            status,
            executionTimeMs
        );

        log.debug("Tool execution recorded: {} - {} in {}ms", toolName, status, executionTimeMs);
    }

    /**
     * Record a decision
     */
    public void recordDecision(AgentExecutionContext context, String decision, String rationale) {
        context.recordDecision(decision, rationale);
        memoryManager.addReasoningStep(
            context.getSessionId(),
            context.getCurrentIteration(),
            decision,
            rationale,
            ""
        );
        log.debug("Decision recorded (iteration {}): {}", context.getCurrentIteration(), decision);
    }

    /**
     * Record error
     */
    public void recordError(AgentExecutionContext context, String toolName, String errorMessage) {
        context.recordError(toolName, errorMessage);
        log.warn("Error recorded for tool {} at iteration {}: {}", toolName, context.getCurrentIteration(), errorMessage);
    }

    /**
     * Evaluate iteration results and decide whether to continue
     */
    public Boolean evaluateIteration(AgentExecutionContext context) {
        context.setState(AgentExecutionContext.ExecutionState.EVALUATING);

        log.debug("Evaluating iteration {} for execution: {}", context.getCurrentIteration(), context.getExecutionId());

        // Check if we have an error
        if (context.hasError()) {
            log.warn("Errors detected at iteration {}, stopping", context.getCurrentIteration());
            return false;
        }

        // Check if we should continue to next iteration
        if (!context.shouldContinue()) {
            log.info("Max iterations {} reached or error detected, stopping", context.getMaxIterations());
            return false;
        }

        return true;
    }

    /**
     * Move to next iteration
     */
    public void nextIteration(AgentExecutionContext context) {
        context.nextIteration();
        context.setState(AgentExecutionContext.ExecutionState.ITERATING);
        context.setCurrentThinking(null);
        context.setCurrentAction(null);
        log.debug("Moving to iteration {} for execution: {}", context.getCurrentIteration(), context.getExecutionId());
    }

    /**
     * Complete execution
     */
    public void completeExecution(AgentExecutionContext context, String finalResponse, Boolean success) {
        context.setFinalResponse(finalResponse);
        context.setSuccess(success);
        context.setCompletedAt(LocalDateTime.now());
        context.setState(success ? AgentExecutionContext.ExecutionState.COMPLETED : AgentExecutionContext.ExecutionState.FAILED);

        // Add final response to memory
        memoryManager.addAssistantMessage(context.getSessionId(), finalResponse);

        long durationMs = context.getExecutionDurationMs();
        log.info("Execution {} completed in {}ms - Success: {}", context.getExecutionId(), durationMs, success);
    }

    /**
     * Get execution summary
     */
    public String getExecutionSummary(AgentExecutionContext context) {
        StringBuilder summary = new StringBuilder();

        summary.append("=== EXECUTION SUMMARY ===\n");
        summary.append("Execution ID: ").append(context.getExecutionId()).append("\n");
        summary.append("Session ID: ").append(context.getSessionId()).append("\n");
        summary.append("Status: ").append(context.getState()).append("\n");
        summary.append("Success: ").append(context.getSuccess()).append("\n");
        summary.append("Iterations: ").append(context.getCurrentIteration()).append("/").append(context.getMaxIterations()).append("\n");
        summary.append("Duration: ").append(context.getExecutionDurationMs()).append("ms\n");

        if (!context.getDecisions().isEmpty()) {
            summary.append("\n=== DECISIONS ===\n");
            for (AgentExecutionContext.Decision decision : context.getDecisions()) {
                summary.append("Iteration ").append(decision.getIterationNumber())
                    .append(": ").append(decision.getDecision()).append("\n");
            }
        }

        if (!context.getErrors().isEmpty()) {
            summary.append("\n=== ERRORS ===\n");
            for (AgentExecutionContext.ErrorRecord error : context.getErrors()) {
                summary.append("Iteration ").append(error.getIterationNumber())
                    .append(" - ").append(error.getToolName())
                    .append(": ").append(error.getErrorMessage()).append("\n");
            }
        }

        return summary.toString();
    }

    /**
     * Get detailed execution report
     */
    public Map<String, Object> getExecutionReport(AgentExecutionContext context) {
        Map<String, Object> report = new java.util.LinkedHashMap<>();
        report.put("executionId", context.getExecutionId());
        report.put("sessionId", context.getSessionId());
        report.put("state", context.getState());
        report.put("success", context.getSuccess());
        report.put("iterations", context.getCurrentIteration());
        report.put("maxIterations", context.getMaxIterations());
        report.put("durationMs", context.getExecutionDurationMs());
        report.put("tokensUsed", context.getTokensUsed());
        report.put("decisionsCount", context.getDecisions().size());
        report.put("errorsCount", context.getErrors().size());
        report.put("finalResponse", context.getFinalResponse());
        report.put("startedAt", context.getStartedAt());
        report.put("completedAt", context.getCompletedAt());
        return report;
    }
}
