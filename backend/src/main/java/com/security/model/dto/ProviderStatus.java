package com.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for LLM provider status information.
 * Provides details about available AI providers and their health status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderStatus {

    /**
     * Name of the LLM provider
     */
    private String providerName;

    /**
     * Whether the provider is currently configured and available
     */
    private Boolean isAvailable;

    /**
     * Whether the provider is the current active provider
     */
    private Boolean isActive;

    /**
     * Model name being used
     */
    private String modelName;

    /**
     * Health status message
     */
    private String statusMessage;

    /**
     * Response time in milliseconds for a test query
     */
    private Long responseTimeMs;

    /**
     * Error message if provider is not available
     */
    private String errorMessage;

    /**
     * Last time provider status was checked
     */
    private Long lastCheckedAt;

    /**
     * Whether this is a local provider (like Ollama)
     */
    private Boolean isLocal;

    /**
     * Supported features of this provider
     */
    private String supportedFeatures;
}
