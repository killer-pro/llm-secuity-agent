package com.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversation Memory Management
 * Stores conversation history, execution context, and reasoning chain
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMemory {

    /**
     * Unique session identifier
     */
    private String sessionId;

    /**
     * Conversation message history
     */
    @Builder.Default
    private List<MessageEntry> messageHistory = new ArrayList<>();

    /**
     * Tool execution history
     */
    @Builder.Default
    private List<ToolExecution> toolExecutions = new ArrayList<>();

    /**
     * Agent reasoning chain (thought process)
     */
    @Builder.Default
    private List<ReasoningStep> reasoningChain = new ArrayList<>();

    /**
     * Current iteration count
     */
    @Builder.Default
    private Integer currentIteration = 0;

    /**
     * Maximum iterations allowed
     */
    @Builder.Default
    private Integer maxIterations = 10;

    /**
     * Total tokens used in session
     */
    @Builder.Default
    private Long totalTokensUsed = 0L;

    /**
     * Memory created timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last updated timestamp
     */
    private LocalDateTime lastUpdatedAt;

    /**
     * Add a user message to history
     */
    public void addUserMessage(String message) {
        messageHistory.add(MessageEntry.builder()
            .role("user")
            .content(message)
            .timestamp(LocalDateTime.now())
            .build());
        lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Add an assistant message to history
     */
    public void addAssistantMessage(String message) {
        messageHistory.add(MessageEntry.builder()
            .role("assistant")
            .content(message)
            .timestamp(LocalDateTime.now())
            .build());
        lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Add a tool execution record
     */
    public void recordToolExecution(ToolExecution execution) {
        toolExecutions.add(execution);
        lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Add a reasoning step
     */
    public void addReasoningStep(ReasoningStep step) {
        reasoningChain.add(step);
        lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Increment iteration counter
     */
    public void incrementIteration() {
        currentIteration++;
    }

    /**
     * Check if max iterations exceeded
     */
    public Boolean hasExceededMaxIterations() {
        return currentIteration >= maxIterations;
    }

    /**
     * Get conversation context (last N messages)
     */
    public String getConversationContext(Integer lastN) {
        StringBuilder context = new StringBuilder();
        int startIdx = Math.max(0, messageHistory.size() - lastN);
        for (int i = startIdx; i < messageHistory.size(); i++) {
            MessageEntry entry = messageHistory.get(i);
            context.append(entry.getRole().toUpperCase())
                .append(": ")
                .append(entry.getContent())
                .append("\n\n");
        }
        return context.toString();
    }

    /**
     * Message Entry in conversation
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageEntry {
        private String role; // "user" or "assistant"
        private String content;
        private LocalDateTime timestamp;
    }

    /**
     * Tool Execution Record
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToolExecution {
        private String toolName;
        private String toolType; // nmap, virustotal, cve_search, file_explorer, etc.
        private String input;
        private String output;
        private String status; // SUCCESS, FAILED, TIMEOUT
        private Long executionTimeMs;
        private LocalDateTime executedAt;
    }

    /**
     * Reasoning Step in Agent Reasoning Chain
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReasoningStep {
        private Integer stepNumber;
        private String thought; // What the agent is thinking
        private String action; // What action the agent is taking
        private String observation; // Result of the action
        private LocalDateTime timestamp;
    }
}
