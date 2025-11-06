package com.security.controller;

import com.security.model.dto.ChatRequest;
import com.security.model.dto.AgentResponse;
import com.security.service.ai.SecurityAIAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for AI Agent operations.
 * Handles chat requests and conversational interactions with the security AI.
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AIAgentController {

    private final SecurityAIAgent securityAIAgent;

    @Autowired
    public AIAgentController(SecurityAIAgent securityAIAgent) {
        this.securityAIAgent = securityAIAgent;
    }

    /**
     * Send a chat message to the AI agent
     */
    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: {}", request.getMessage());

        try {
            AgentResponse response = securityAIAgent.processChat(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ResponseEntity.internalServerError()
                    .body(AgentResponse.builder()
                            .error(e.getMessage())
                            .response("An error occurred while processing your request.")
                            .build());
        }
    }

    /**
     * Health check for AI agent
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Agent is running");
    }

    /**
     * Get agent status
     */
    @GetMapping("/status")
    public ResponseEntity<AgentResponse> getStatus() {
        return ResponseEntity.ok(AgentResponse.builder()
                .response("AI Agent is operational and ready to process requests.")
                .isComplete(true)
                .build());
    }
}
