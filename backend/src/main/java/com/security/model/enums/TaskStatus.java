package com.security.model.enums;

/**
 * Enumeration of task statuses throughout their lifecycle.
 * Represents the different states a security scan or analysis task can be in.
 */
public enum TaskStatus {
    /**
     * Task has been created but not started yet
     */
    PENDING("Pending", "Task created, waiting to start"),

    /**
     * Task is currently executing
     */
    IN_PROGRESS("In Progress", "Task is currently executing"),

    /**
     * Task has completed successfully
     */
    COMPLETED("Completed", "Task completed successfully"),

    /**
     * Task failed during execution
     */
    FAILED("Failed", "Task failed during execution"),

    /**
     * Task was cancelled by user
     */
    CANCELLED("Cancelled", "Task was cancelled by user"),

    /**
     * Task is paused and can be resumed
     */
    PAUSED("Paused", "Task is paused and can be resumed");

    private final String displayName;
    private final String description;

    TaskStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
