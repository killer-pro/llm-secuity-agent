package com.security.service.ai;

import com.security.config.AgentPrompts;
import com.security.model.dto.AgentResponse;
import com.security.model.dto.ChatRequest;
import com.security.model.entity.Task;
import com.security.model.enums.TaskStatus;
import com.security.repository.TaskRepository;
import com.security.service.security.CVESearchTool;
import com.security.service.security.NmapTool;
import com.security.service.security.VirusTotalTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Security AI Agent Service.
 * AI-powered security analysis with integrated tools (Nmap, VirusTotal, CVE Search).
 * The agent uses LangChain4j with function calling to leverage security tools.
 */
@Slf4j
@Service
public class SecurityAIAgent {

    private final ChatLanguageModel chatLanguageModel;
    private final NmapTool nmapTool;
    private final VirusTotalTool virusTotalTool;
    private final CVESearchTool cveSearchTool;
    private final TaskRepository taskRepository;
    private SecurityAiService securityAiService;

    @Autowired
    public SecurityAIAgent(
            ChatLanguageModel chatLanguageModel,
            NmapTool nmapTool,
            VirusTotalTool virusTotalTool,
            CVESearchTool cveSearchTool,
            TaskRepository taskRepository
    ) {
        this.chatLanguageModel = chatLanguageModel;
        this.nmapTool = nmapTool;
        this.virusTotalTool = virusTotalTool;
        this.cveSearchTool = cveSearchTool;
        this.taskRepository = taskRepository;

        initializeAgent();
    }

    /**
     * Initialize the AI agent with tools via LangChain4j
     */
    private void initializeAgent() {
        log.info("Initializing Security AI Agent with integrated tools");

        try {
            // Create AI service with tools
            this.securityAiService = AiServices.builder(SecurityAiService.class)
                    .chatLanguageModel(chatLanguageModel)
                    .tools(nmapTool, virusTotalTool, cveSearchTool)
                    .build();

            log.info("Security AI Agent initialized with tools:");
            log.info("  - NmapTool (Network scanning)");
            log.info("  - VirusTotalTool (Malware analysis)");
            log.info("  - CVESearchTool (Vulnerability research)");

        } catch (Exception e) {
            log.error("Failed to initialize Security AI Agent", e);
            throw new RuntimeException("Agent initialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Process a chat request using LLM
     */
    public AgentResponse processChat(ChatRequest request) {
        log.info("Processing chat request: {}", request.getMessage());

        String sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            // Create task record
            Task task = Task.builder()
                    .name("Chat: " + request.getMessage().substring(0, Math.min(50, request.getMessage().length())))
                    .description(request.getMessage())
                    .status(TaskStatus.IN_PROGRESS)
                    .sessionId(sessionId)
                    .startedAt(LocalDateTime.now())
                    .build();

            task = taskRepository.save(task);

            // Generate response using real LLM
            String response = generateAIResponse(request.getMessage(), request.getUseTools());

            // Update task
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            task.setAgentResponse(response);
            task.setExecutionTimeMs(System.currentTimeMillis() - startTime);

            taskRepository.save(task);

            // Build response
            return AgentResponse.builder()
                    .response(response)
                    .sessionId(sessionId)
                    .taskId(task.getId())
                    .isComplete(true)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process chat request", e);

            return AgentResponse.builder()
                    .response("Sorry, I encountered an error processing your request: " + e.getMessage())
                    .sessionId(sessionId)
                    .error(e.getMessage())
                    .isComplete(true)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    /**
     * Generate AI response with tools enabled
     */
    private String generateAIResponse(String userMessage, Boolean useTools) {
        log.debug("Generating AI response for: {} (tools enabled: {})", userMessage, useTools);

        try {
            // Use the AI service with tools if enabled
            if (useTools != null && useTools && securityAiService != null) {
                log.debug("Using AI service with tools");
                String response = securityAiService.chat(userMessage);
                log.debug("AI response with tools: {}", response);
                return response;
            } else {
                // Fallback to direct LLM call
                log.debug("Using direct LLM without tools");
                dev.langchain4j.data.message.ChatMessage chatMessage =
                    dev.langchain4j.data.message.UserMessage.from(userMessage);

                dev.langchain4j.model.output.Response<dev.langchain4j.data.message.AiMessage> response =
                    chatLanguageModel.generate(chatMessage);

                String aiResponse = response.content().text();
                log.debug("Direct LLM response: {}", aiResponse);
                return aiResponse;
            }

        } catch (Exception e) {
            log.error("Error generating AI response", e);
            throw new RuntimeException("Failed to generate AI response: " + e.getMessage(), e);
        }
    }

    /**
     * LangChain4j service interface for the security AI agent
     * Uses function calling to invoke available tools
     */
    interface SecurityAiService {
        @SystemMessage(AgentPrompts.SYSTEM_PROMPT)
        String chat(@UserMessage String userMessage);
    }
}
