package com.security.controller;

import com.security.model.dto.ProviderStatus;
import com.security.service.ai.LLMProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for LLM provider management.
 * Handles provider configuration, status, and switching.
 */
@Slf4j
@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProviderController {

    private final LLMProviderService llmProviderService;

    @Autowired
    public ProviderController(LLMProviderService llmProviderService) {
        this.llmProviderService = llmProviderService;
    }

    /**
     * Get all available providers
     */
    @GetMapping("/")
    public ResponseEntity<List<ProviderStatus>> getAllProviders() {
        log.info("Fetching all provider statuses");

        try {
            List<ProviderStatus> providers = llmProviderService.getAllProviderStatus();
            return ResponseEntity.ok(providers);

        } catch (Exception e) {
            log.error("Error fetching providers", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get active provider
     */
    @GetMapping("/active")
    public ResponseEntity<ProviderStatus> getActiveProvider() {
        log.info("Fetching active provider");

        try {
            ProviderStatus provider = llmProviderService.getActiveProviderStatus();
            return ResponseEntity.ok(provider);

        } catch (Exception e) {
            log.error("Error fetching active provider", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Switch to a different provider
     */
    @PostMapping("/switch")
    public ResponseEntity<ProviderStatus> switchProvider(@RequestParam String providerName) {
        log.info("Switching to provider: {}", providerName);

        try {
            ProviderStatus provider = llmProviderService.switchProvider(providerName);
            return ResponseEntity.ok(provider);

        } catch (Exception e) {
            log.error("Error switching provider", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get model capabilities
     */
    @GetMapping("/capabilities")
    public ResponseEntity<String> getCapabilities() {
        log.info("Fetching model capabilities");

        try {
            String capabilities = llmProviderService.getModelCapabilities();
            return ResponseEntity.ok(capabilities);

        } catch (Exception e) {
            log.error("Error fetching capabilities", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
