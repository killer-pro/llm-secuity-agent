package com.security.repository;

import com.security.model.entity.Task;
import com.security.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Task entities.
 * Provides database operations for AI agent task tracking.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks with a specific status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find tasks by tool type
     */
    List<Task> findByToolType(String toolType);

    /**
     * Find tasks by session ID
     */
    List<Task> findBySessionId(String sessionId);

    /**
     * Find all tasks created within a time range
     */
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Task> findTasksBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find failed tasks
     */
    List<Task> findByStatusAndNameContainingIgnoreCase(TaskStatus status, String name);

    /**
     * Find recent tasks (limit 20)
     */
    @Query(value = "SELECT * FROM tasks ORDER BY created_at DESC LIMIT 20", nativeQuery = true)
    List<Task> findRecentTasks();

    /**
     * Find in-progress tasks
     */
    @Query("SELECT t FROM Task t WHERE t.status = 'IN_PROGRESS' ORDER BY t.startedAt ASC")
    List<Task> findInProgressTasks();

    /**
     * Count tasks by status
     */
    long countByStatus(TaskStatus status);

    /**
     * Find tasks by session and status
     */
    @Query("SELECT t FROM Task t WHERE t.sessionId = :sessionId AND t.status = :status ORDER BY t.createdAt DESC")
    List<Task> findBySessionIdAndStatus(
            @Param("sessionId") String sessionId,
            @Param("status") TaskStatus status
    );

    /**
     * Find the latest task for a session
     */
    @Query(value = "SELECT * FROM tasks WHERE session_id = :sessionId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Task> findLatestBySessionId(@Param("sessionId") String sessionId);

    /**
     * Find tasks by tool type and status
     */
    @Query("SELECT t FROM Task t WHERE t.toolType = :toolType AND t.status = :status ORDER BY t.createdAt DESC")
    List<Task> findByToolTypeAndStatus(
            @Param("toolType") String toolType,
            @Param("status") TaskStatus status
    );
}
