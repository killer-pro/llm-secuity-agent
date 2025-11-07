package com.security.service.ai;

import com.security.model.dto.ConversationMemory;
import com.security.model.dto.ConversationMemory.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Conversation Memory Manager
 * Manages conversation history, tool execution tracking, and reasoning chains
 */
@Slf4j
@Service
public class MemoryManager {

    private final Map<String, ConversationMemory> sessions = new ConcurrentHashMap<>();

    private static final int MAX_MESSAGES_PER_SESSION = 100;
    private static final int MAX_TOOL_EXECUTIONS = 50;
    private static final int MAX_REASONING_STEPS = 100;
    private static final long SESSION_TIMEOUT_MS = 24 * 60 * 60 * 1000; // 24 hours

    /**
     * Get or create conversation memory for a session
     */
    public ConversationMemory getOrCreateMemory(String sessionId) {
        return sessions.computeIfAbsent(sessionId, sid -> {
            ConversationMemory memory = ConversationMemory.builder()
                .sessionId(sid)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .maxIterations(10)
                .build();
            log.info("Created new memory for session: {}", sid);
            return memory;
        });
    }

    /**
     * Add user message to conversation
     */
    public void addUserMessage(String sessionId, String message) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        memory.addUserMessage(message);
        log.debug("Added user message to session {}: {}", sessionId, message.substring(0, Math.min(50, message.length())));
    }

    /**
     * Add assistant message to conversation
     */
    public void addAssistantMessage(String sessionId, String message) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        memory.addAssistantMessage(message);
        log.debug("Added assistant message to session {}", sessionId);
    }

    /**
     * Record tool execution
     */
    public void recordToolExecution(String sessionId, String toolName, String toolType,
                                   String input, String output, String status, Long executionTimeMs) {
        ConversationMemory memory = getOrCreateMemory(sessionId);

        ToolExecution execution = ToolExecution.builder()
            .toolName(toolName)
            .toolType(toolType)
            .input(input)
            .output(output)
            .status(status)
            .executionTimeMs(executionTimeMs)
            .executedAt(LocalDateTime.now())
            .build();

        memory.recordToolExecution(execution);

        // Maintain size limits
        if (memory.getToolExecutions().size() > MAX_TOOL_EXECUTIONS) {
            memory.getToolExecutions().remove(0);
        }

        log.debug("Recorded tool execution for {}: {} in {}ms", sessionId, toolName, executionTimeMs);
    }

    /**
     * Add reasoning step
     */
    public void addReasoningStep(String sessionId, Integer stepNumber, String thought,
                                String action, String observation) {
        ConversationMemory memory = getOrCreateMemory(sessionId);

        ReasoningStep step = ReasoningStep.builder()
            .stepNumber(stepNumber)
            .thought(thought)
            .action(action)
            .observation(observation)
            .timestamp(LocalDateTime.now())
            .build();

        memory.addReasoningStep(step);

        // Maintain size limits
        if (memory.getReasoningChain().size() > MAX_REASONING_STEPS) {
            memory.getReasoningChain().remove(0);
        }
    }

    /**
     * Get conversation context (recent messages)
     */
    public String getConversationContext(String sessionId, Integer lastN) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        return memory.getConversationContext(lastN);
    }

    /**
     * Get full conversation history
     */
    public List<MessageEntry> getConversationHistory(String sessionId) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        return new ArrayList<>(memory.getMessageHistory());
    }

    /**
     * Get tool execution history
     */
    public List<ToolExecution> getToolExecutionHistory(String sessionId) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        return new ArrayList<>(memory.getToolExecutions());
    }

    /**
     * Get reasoning chain
     */
    public List<ReasoningStep> getReasoningChain(String sessionId) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        return new ArrayList<>(memory.getReasoningChain());
    }

    /**
     * Update tokens used
     */
    public void updateTokensUsed(String sessionId, Long tokensUsed) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        memory.setTotalTokensUsed(memory.getTotalTokensUsed() + tokensUsed);
        memory.setLastUpdatedAt(LocalDateTime.now());
    }

    /**
     * Get total tokens used in session
     */
    public Long getTotalTokensUsed(String sessionId) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        return memory.getTotalTokensUsed();
    }

    /**
     * Get memory statistics for a session
     */
    public Map<String, Object> getMemoryStats(String sessionId) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        return Map.of(
            "sessionId", sessionId,
            "messageCount", memory.getMessageHistory().size(),
            "toolExecutionCount", memory.getToolExecutions().size(),
            "reasoningStepCount", memory.getReasoningChain().size(),
            "totalTokensUsed", memory.getTotalTokensUsed(),
            "createdAt", memory.getCreatedAt(),
            "lastUpdatedAt", memory.getLastUpdatedAt()
        );
    }

    /**
     * Clear memory for a session
     */
    public void clearMemory(String sessionId) {
        sessions.remove(sessionId);
        log.info("Cleared memory for session: {}", sessionId);
    }

    /**
     * Clear all expired sessions
     */
    public void clearExpiredSessions() {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry -> {
            ConversationMemory memory = entry.getValue();
            long lastUpdated = memory.getLastUpdatedAt().atZone(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli();
            return (now - lastUpdated) > SESSION_TIMEOUT_MS;
        });
        log.info("Cleared expired sessions");
    }

    /**
     * Search memory by keyword
     */
    public List<MessageEntry> searchMemory(String sessionId, String keyword) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        return memory.getMessageHistory().stream()
            .filter(msg -> msg.getContent().toLowerCase().contains(keyword.toLowerCase()))
            .toList();
    }

    /**
     * Get session summary
     */
    public String getSessionSummary(String sessionId) {
        ConversationMemory memory = getOrCreateMemory(sessionId);
        StringBuilder summary = new StringBuilder();

        summary.append("=== SESSION SUMMARY ===\n");
        summary.append("Session ID: ").append(sessionId).append("\n");
        summary.append("Messages: ").append(memory.getMessageHistory().size()).append("\n");
        summary.append("Tool Executions: ").append(memory.getToolExecutions().size()).append("\n");
        summary.append("Reasoning Steps: ").append(memory.getReasoningChain().size()).append("\n");
        summary.append("Total Tokens: ").append(memory.getTotalTokensUsed()).append("\n");
        summary.append("Created: ").append(memory.getCreatedAt()).append("\n");

        if (!memory.getToolExecutions().isEmpty()) {
            summary.append("\n=== TOOL EXECUTIONS ===\n");
            for (ToolExecution exec : memory.getToolExecutions()) {
                summary.append("- ").append(exec.getToolName())
                    .append(" (").append(exec.getStatus()).append(") ")
                    .append(exec.getExecutionTimeMs()).append("ms\n");
            }
        }

        return summary.toString();
    }
}
