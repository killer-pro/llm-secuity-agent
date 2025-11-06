package com.security.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration for LangChain4j
 */
@Slf4j
@Configuration
public class LLMConfig {

    @Value("${llm.provider:gemini}")
    private String activeProvider;

    @Value("${llm.api.key:}")
    private String apiKey;

    @Value("${llm.model.name:gemini-2.0-flash}")
    private String modelName;

    @Value("${llm.temperature:0.0}")
    private Double temperature;

    @Value("${llm.max-tokens:2000}")
    private Integer maxTokens;

    @Value("${llm.gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    @Value("${llm.claude.model:claude-3-5-sonnet-20241022}")
    private String claudeModel;

    @Value("${llm.openai.model:gpt-4}")
    private String openaiModel;

    @Value("${llm.ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${llm.ollama.model:neural-chat}")
    private String ollamaModel;

    /**
     * ChatLanguageModel bean - creates appropriate model based on provider
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        log.info("Initializing ChatLanguageModel with provider: {}", activeProvider);

        return switch (activeProvider.toLowerCase()) {
            case "gemini" -> createGeminiModel();
            case "claude" -> createClaudeModel();
            case "openai" -> createOpenAiModel();
            case "ollama" -> createOllamaModel();
            default -> {
                log.warn("Unknown provider: {}, defaulting to Gemini", activeProvider);
                yield createGeminiModel();
            }
        };
    }

    /**
     * EmbeddingModel bean
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("Creating EmbeddingModel");

        if ("ollama".equalsIgnoreCase(activeProvider)) {
            return OllamaEmbeddingModel.builder()
                    .baseUrl(ollamaUrl)
                    .modelName(ollamaModel)
                    .build();
        }

        // Default mock embedding model
        return new MockEmbeddingModel();
    }

    /**
     * Mock EmbeddingModel implementation for development
     */
    private static class MockEmbeddingModel implements EmbeddingModel {
        @Override
        public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
            List<Embedding> embeddings = new ArrayList<>();
            for (TextSegment segment : textSegments) {
                float[] vector = new float[384];
                Arrays.fill(vector, 0.1f);
                embeddings.add(new Embedding(vector));
            }
            return new Response<>(embeddings, null, null);
        }

        @Override
        public Response<Embedding> embed(TextSegment textSegment) {
            float[] vector = new float[384];
            Arrays.fill(vector, 0.1f);
            return new Response<>(new Embedding(vector), null, null);
        }

        @Override
        public int dimension() {
            return 384;
        }
    }

    /**
     * Create Google Gemini Chat Model
     */
    private ChatLanguageModel createGeminiModel() {
        log.info("Creating Google Gemini Chat Model: {}", geminiModel);

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY environment variable is not set. Using mock responses.");
            return new MockChatLanguageModel("gemini");
        }

        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(geminiModel)
                .temperature(temperature)
                .maxOutputTokens(maxTokens)
                .build();
    }

    /**
     * Mock ChatLanguageModel for development (when API keys are not available)
     */
    private static class MockChatLanguageModel implements ChatLanguageModel {
        private final String provider;

        public MockChatLanguageModel(String provider) {
            this.provider = provider;
        }

        @Override
        public dev.langchain4j.model.output.Response<dev.langchain4j.data.message.AiMessage> generate(java.util.List<dev.langchain4j.data.message.ChatMessage> messages) {
            String lastMessage = messages.isEmpty() ? "" : messages.get(messages.size() - 1).text();
            String response = "[Mock " + provider + " Response] " + lastMessage;
            dev.langchain4j.data.message.AiMessage aiMessage = dev.langchain4j.data.message.AiMessage.from(response);
            return new dev.langchain4j.model.output.Response<>(aiMessage, null, null);
        }
    }

    /**
     * Create Anthropic Claude Chat Model
     */
    private ChatLanguageModel createClaudeModel() {
        log.info("Creating Anthropic Claude Chat Model: {}", claudeModel);

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("CLAUDE_API_KEY environment variable is not set");
        }

        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(claudeModel)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }

    /**
     * Create OpenAI Chat Model
     */
    private ChatLanguageModel createOpenAiModel() {
        log.info("Creating OpenAI Chat Model: {}", openaiModel);

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set");
        }

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(openaiModel)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }

    /**
     * Create Ollama Chat Model (local)
     */
    private ChatLanguageModel createOllamaModel() {
        log.info("Creating Ollama Chat Model: {} at {}", ollamaModel, ollamaUrl);

        return OllamaChatModel.builder()
                .baseUrl(ollamaUrl)
                .modelName(ollamaModel)
                .temperature(temperature)
                .build();
    }
}
