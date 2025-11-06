package com.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for incoming chat messages from the client.
 * Represents a user message to be processed by the AI agent.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    /**
     * The user's message or command
     */
    private String message;

    /**
     * Session ID for conversation tracking
     */
    private String sessionId;

    /**
     * Optional parameters for tool execution
     */
    private String toolInput;

    /**
     * Whether to use available tools for this request
     */
    @Builder.Default
    private Boolean useTools = true;

    /**
     * Maximum tokens for response
     */
    @Builder.Default
    private Integer maxTokens = 2000;
}
