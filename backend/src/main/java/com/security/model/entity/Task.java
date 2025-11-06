package com.security.model.entity;

import com.security.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing an AI agent task.
 * Tracks the execution of tool invocations and agent responses.
 */
@Entity
@Table(name = "task", indexes = {
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable task name
     */
    @Column(nullable = false)
    private String name;

    /**
     * Detailed description of the task
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Current status of the task
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /**
     * Type of tool being used (nmap, virustotal, cve_search, etc.)
     */
    @Column
    private String toolType;

    /**
     * Tool-specific input or parameters
     */
    @Column(columnDefinition = "TEXT")
    private String toolInput;

    /**
     * Tool output or result
     */
    @Column(columnDefinition = "TEXT")
    private String toolOutput;

    /**
     * AI agent response based on tool execution
     */
    @Column(columnDefinition = "TEXT")
    private String agentResponse;

    /**
     * Progress percentage (0-100)
     */
    @Column
    @Builder.Default
    private Integer progress = 0;

    /**
     * Error message if task failed
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Time spent executing the task in milliseconds
     */
    @Column
    private Long executionTimeMs;

    /**
     * Conversation session ID this task belongs to
     */
    @Column
    private String sessionId;

    /**
     * Timestamp when task was created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when task started
     */
    @Column
    private LocalDateTime startedAt;

    /**
     * Timestamp when task completed
     */
    @Column
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TaskStatus.PENDING;
        }
    }
}
