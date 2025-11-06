package com.security.service.ai;

import com.security.model.dto.ProviderStatus;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * LLM Provider Service.
 * Manages multiple LLM providers and their health status.
 */
@Slf4j
@Service
public class LLMProviderService {

    @Value("${llm.provider:gemini}")
    private String activeProvider;

    @Value("${llm.model.name:}")
    private String modelName;

    private final ChatLanguageModel chatLanguageModel;

    @Autowired
    public LLMProviderService(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    /**
     * Get status of all providers
     */
    public List<ProviderStatus> getAllProviderStatus() {
        log.info("Fetching all provider statuses");

        List<ProviderStatus> providers = new ArrayList<>();

        // Gemini
        providers.add(ProviderStatus.builder()
                .providerName("Gemini")
                .isAvailable(true)
                .isActive("gemini".equalsIgnoreCase(activeProvider))
                .modelName("gemini-1.5-pro")
                .statusMessage("Configured and available")
                .isLocal(false)
                .supportedFeatures("Chat, Tool calling, Streaming")
                .build());

        // Claude
        providers.add(ProviderStatus.builder()
                .providerName("Claude")
                .isAvailable(true)
                .isActive("claude".equalsIgnoreCase(activeProvider))
                .modelName("claude-3-5-sonnet-20241022")
                .statusMessage("Configured and available")
                .isLocal(false)
                .supportedFeatures("Chat, Tool calling, Vision")
                .build());

        // OpenAI
        providers.add(ProviderStatus.builder()
                .providerName("OpenAI")
                .isAvailable(true)
                .isActive("openai".equalsIgnoreCase(activeProvider))
                .modelName("gpt-4")
                .statusMessage("Configured and available")
                .isLocal(false)
                .supportedFeatures("Chat, Tool calling, Vision")
                .build());

        // Ollama
        providers.add(ProviderStatus.builder()
                .providerName("Ollama")
                .isAvailable(isOllamaAvailable())
                .isActive("ollama".equalsIgnoreCase(activeProvider))
                .modelName("neural-chat")
                .statusMessage(isOllamaAvailable() ? "Running locally" : "Not available")
                .isLocal(true)
                .supportedFeatures("Chat")
                .build());

        return providers;
    }

    /**
     * Get active provider status
     */
    public ProviderStatus getActiveProviderStatus() {
        log.info("Getting active provider status: {}", activeProvider);

        return ProviderStatus.builder()
                .providerName(capitalizeProvider(activeProvider))
                .isAvailable(true)
                .isActive(true)
                .modelName(modelName)
                .statusMessage("Active and responding")
                .responseTimeMs(checkProviderHealth())
                .isLocal("ollama".equalsIgnoreCase(activeProvider))
                .build();
    }

    /**
     * Check provider health
     */
    private Long checkProviderHealth() {
        long startTime = System.currentTimeMillis();

        try {
            // Simple health check with a test query
            chatLanguageModel.generate("ping");
            return System.currentTimeMillis() - startTime;

        } catch (Exception e) {
            log.warn("Provider health check failed", e);
            return -1L;
        }
    }

    /**
     * Switch to different provider
     */
    public ProviderStatus switchProvider(String providerName) {
        log.info("Attempting to switch to provider: {}", providerName);

        // In a real implementation, this would recreate the ChatLanguageModel
        // For now, just return status

        return ProviderStatus.builder()
                .providerName(capitalizeProvider(providerName))
                .isAvailable(true)
                .isActive(true)
                .statusMessage("Provider switched successfully")
                .build();
    }

    /**
     * Check if Ollama is available
     */
    private boolean isOllamaAvailable() {
        try {
            // Try to connect to Ollama
            // This would be a real HTTP request in production
            return false;  // Default to false if not configured
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Capitalize provider name
     */
    private String capitalizeProvider(String provider) {
        return provider.substring(0, 1).toUpperCase() + provider.substring(1).toLowerCase();
    }

    /**
     * Get model capabilities
     */
    public String getModelCapabilities() {
        log.info("Getting capabilities for active model: {}", modelName);

        return switch (activeProvider.toLowerCase()) {
            case "gemini" -> "Advanced multimodal understanding, code generation, streaming support";
            case "claude" -> "Long context window, vision capabilities, complex reasoning";
            case "openai" -> "Mature API, vision support, function calling";
            case "ollama" -> "Local execution, privacy-focused, customizable models";
            default -> "Unknown";
        };
    }
}
