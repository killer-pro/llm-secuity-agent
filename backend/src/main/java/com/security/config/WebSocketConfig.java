package com.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration for WebSocket communication.
 * Enables real-time messaging for scan progress updates and agent responses.
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker for WebSocket
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("Configuring WebSocket message broker");

        // Enable SimpleBroker for message distribution
        config.enableSimpleBroker("/topic", "/queue");

        // Set the prefix for client-to-server messages
        config.setApplicationDestinationPrefixes("/app");

        // Set the user destination prefix
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register WebSocket endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering WebSocket STOMP endpoints");

        // Register the main WebSocket endpoint
        registry.addEndpoint("/ws/security-agent")
                .setAllowedOrigins("*")
                .withSockJS();

        // Register scan progress endpoint
        registry.addEndpoint("/ws/scan-progress")
                .setAllowedOrigins("*")
                .withSockJS();

        // Register chat endpoint
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
