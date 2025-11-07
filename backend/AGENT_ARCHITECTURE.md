# Security AI Agent - Advanced Architecture

## Overview

This document describes the improved Security AI Agent architecture with:
- **Memory Management**: Conversation history and context
- **Recursive Reasoning**: Multi-iteration problem solving with max limits
- **Tool Orchestration**: 7+ specialized security tools
- **Execution Tracking**: State management and decision logging

---

## Architecture Components

### 1. Core Components

#### ConversationMemory
Stores conversation history, tool executions, and reasoning chain
- **Location**: `model/dto/ConversationMemory.java`
- **Features**:
  - Message history (user/assistant)
  - Tool execution records
  - Reasoning steps tracking
  - Iteration counter and token tracking

#### AgentExecutionContext
Maintains execution state during recursive iterations
- **Location**: `model/dto/AgentExecutionContext.java`
- **Features**:
  - Execution ID and session ID
  - State management (INITIALIZED → THINKING → EXECUTING → COMPLETED)
  - Decision and error tracking
  - Iteration limit enforcement
  - Token usage tracking

#### MemoryManager
Manages conversation memory across sessions
- **Location**: `service/ai/MemoryManager.java`
- **Features**:
  - Session-based memory storage
  - Tool execution recording
  - Reasoning step tracking
  - Memory statistics
  - Session cleanup and expiration

#### ExecutionOrchestrator
Orchestrates recursive reasoning with iterations
- **Location**: `service/ai/ExecutionOrchestrator.java`
- **Features**:
  - Context creation and lifecycle
  - Iteration management
  - State transitions
  - Decision and error recording
  - Execution reports

---

## Tools Available

### 1. NmapTool
Network scanning and reconnaissance
```java
- scanQuick(String target)      // Host discovery (-sn)
- scanFull(String target)       // Comprehensive scan (-sV -sC -O -A)
- scanServices(String target)   // Service/version detection
- scanPorts(String target, ports) // Specific port scan
- detectOS(String target)       // OS detection
```

### 2. VirusTotalTool
Malware and threat analysis
```java
- analyzeFile(String filePath)  // Single file scan
- analyzeUrl(String url)        // URL threat analysis
- scanDirectory(String dirPath) // Recursive directory scan
```

### 3. CVESearchTool
Vulnerability research and CVE database
```java
- searchProduct(String productName)         // CVEs by product
- getCVEDetails(String cveId)              // Specific CVE info
- searchBySeverity(String severity)        // Filter by severity
- searchByCVSSScore(Double min, max)       // CVSS range search
- getRecentCVEs()                          // Last 7 days CVEs
```

### 4. FileExplorerTool (NEW)
File system exploration and analysis
```java
- listFiles(String dirPath, Boolean recursive)      // List files
- searchFiles(String dirPath, String pattern)       // Search by pattern
- getFileProperties(String filePath)                // Detailed properties
- findLargeFiles(String dirPath, Long minSize)     // Find large files
- analyzeFileTypes(String dirPath)                  // File type statistics
```

### 5. DirectoryScannerTool (NEW)
Advanced directory scanning with security analysis
```java
- deepDirectoryScan(String dirPath)                 // Complete scan
- findHiddenFiles(String dirPath)                   // Find hidden files
- findExecutables(String dirPath)                   // Find executables
- findRecentlyModified(String dirPath, minutes)    // Recent changes
- analyzePermissions(String dirPath)                // Permission analysis
```

### 6. NVDClient (Ready for Integration)
NIST National Vulnerability Database API
```java
- searchByKeyword(String keyword)           // Free-text search
- getCVEById(String cveId)                 // Specific CVE fetch
- searchByCVSSScore(double min, max)       // CVSS filtering
- searchBySeverity(String severity)        // Severity filtering
- isAvailable()                            // Health check
```

---

## Execution Flow

### Request Processing with Recursive Reasoning

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. HTTP POST /api/agent/chat (ChatRequest)                      │
└──────────────────────────┬──────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. AIAgentController → SecurityAIAgent.processChat()            │
└──────────────────────────┬──────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. Create Execution Context                                     │
│    - Unique execution ID                                        │
│    - Session ID                                                 │
│    - Memory initialization                                      │
└──────────────────────────┬──────────────────────────────────────┘
                          │
                          ▼
        ┌─────────────────────────────────┐
        │ 4. ITERATION LOOP (max 10)      │
        │ while shouldContinue()          │
        └────────────┬────────────────────┘
                     │
        ┌────────────▼────────────┐
        │ 5.1 THINKING PHASE      │
        │ - Parse user prompt     │
        │ - Analyze context       │
        │ - Determine next action │
        └────────────┬────────────┘
                     │
        ┌────────────▼────────────────────┐
        │ 5.2 TOOL SELECTION & EXECUTION  │
        │ - Decide which tools to use     │
        │ - Execute tools in parallel     │
        │ - Collect results               │
        └────────────┬────────────────────┘
                     │
        ┌────────────▼──────────────┐
        │ 5.3 REASONING PHASE       │
        │ - Analyze tool results    │
        │ - Record observations     │
        │ - Store reasoning steps   │
        └────────────┬──────────────┘
                     │
        ┌────────────▼──────────────────┐
        │ 5.4 EVALUATION PHASE          │
        │ - Check max iterations        │
        │ - Evaluate success criteria   │
        │ - Decide to continue or stop  │
        └────────────┬──────────────────┘
                     │
        ┌────────────▼──────────────┐
        │ 5.5 MEMORY UPDATE         │
        │ - Add messages to history │
        │ - Record executions       │
        │ - Track tokens            │
        └────────────┬──────────────┘
                     │
                     └──→ If continue: Go to 5.1
                         If stop: Exit loop
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│ 6. Build Final Response                                         │
│    - Synthesize findings                                        │
│    - Format output                                              │
│    - Generate execution report                                  │
└──────────────────────────┬──────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│ 7. Save to Database                                             │
│    - Store task record                                          │
│    - Persist execution metrics                                  │
│    - Update conversation memory                                 │
└──────────────────────────┬──────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│ 8. Return AgentResponse to Client                               │
│    - Final response text                                        │
│    - Session ID                                                 │
│    - Execution metrics                                          │
│    - Memory stats                                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## Memory Management

### Session Memory Storage
- **Type**: In-memory ConcurrentHashMap
- **Key**: Session ID
- **Value**: ConversationMemory object

### Automatic Cleanup
- **Max Session Duration**: 24 hours
- **Auto-cleanup**: Called via `MemoryManager.clearExpiredSessions()`
- **Manual cleanup**: `MemoryManager.clearMemory(sessionId)`

### Memory Contents

```
ConversationMemory
├── Message History (max 100)
│   ├── Role: "user" | "assistant"
│   ├── Content: Message text
│   └── Timestamp: LocalDateTime
│
├── Tool Executions (max 50)
│   ├── Tool Name & Type
│   ├── Input & Output
│   ├── Status & Duration
│   └── Execution Time
│
├── Reasoning Chain (max 100)
│   ├── Step Number
│   ├── Thought Process
│   ├── Action Taken
│   └── Observation Result
│
└── Metrics
    ├── Total Tokens Used
    ├── Creation & Update Timestamps
    └── Iteration Counter
```

---

## Recursive Reasoning

### Iteration Management

```
Iteration Lifecycle:
├── startIteration()              // Set state to THINKING
├── setThinking(context, text)    // Record agent thought
├── setAction(context, action)    // Set state to EXECUTING
├── recordToolExecution(...)      // Execute tools
├── recordDecision(...)           // Log decisions
├── evaluateIteration()           // State = EVALUATING
└── nextIteration() or complete() // State = ITERATING or COMPLETED
```

### Iteration Limits

- **Default Max Iterations**: 10
- **Configurable**: Via `AgentExecutionContext.maxIterations`
- **Enforcement**: `context.hasExceededMaxIterations()` check

### Stopping Conditions

1. **Max iterations reached**: `currentIteration >= maxIterations`
2. **Error encountered**: `context.hasError() == true`
3. **Goal achieved**: Agent decides task is complete
4. **Timeout**: Execution duration exceeds limit

---

## Configuration

### Application.yml Settings

```yaml
# Agent Configuration
agent:
  max-iterations: 10              # Maximum reasoning iterations
  request-timeout-ms: 120000      # Request timeout
  memory-cleanup-interval-min: 60 # Cleanup interval
  enable-memory: true             # Enable conversation memory
  enable-recursive-reasoning: true # Enable multi-iteration

# Tool Configuration
tools:
  nmap:
    enabled: true
    timeout-minutes: 30
    allowed-ranges:
      - 192.168.0.0/16
      - 10.0.0.0/8

  virustotal:
    enabled: true
    api-key: ${VIRUSTOTAL_API_KEY:}

  cve-search:
    enabled: true
    nist-api-key: ${NVD_API_KEY:9be67211-6db1-456c-ba55-2f1376ad0453}

# LLM Configuration
llm:
  provider: gemini
  temperature: 0.3              # Lower for consistency
  max-tokens: 2000
```

---

## Usage Examples

### Example 1: Memory-based Conversation

```
User Message 1: "Scan 192.168.1.0/24 for open ports"
→ Agent creates execution context
→ Executes Nmap scan
→ Stores results in memory
→ Returns findings

User Message 2: "What did you find?" (same session)
→ Agent retrieves conversation history
→ Accesses previous scan results from memory
→ Provides contextual response
```

### Example 2: Recursive Reasoning

```
User: "Identify critical vulnerabilities on our servers"

Iteration 1:
├── Thinking: "Need to scan for running services"
├── Action: Execute Nmap service detection
└── Result: Found Apache 2.4.41, OpenSSH 7.4

Iteration 2:
├── Thinking: "Need to search CVEs for identified software"
├── Action: Execute CVE search for Apache + OpenSSH
└── Result: Found 15 CVEs, 3 critical

Iteration 3:
├── Thinking: "Need detailed analysis of critical CVEs"
├── Action: Fetch CVE details and CVSS scores
└── Result: Generated full vulnerability report

Iteration 4:
├── Thinking: "Have sufficient information, synthesizing response"
├── Action: Compile findings and recommendations
└── Final Response: Comprehensive security report
```

---

## API Endpoints

### Chat with Memory

```
POST /api/agent/chat
Content-Type: application/json

Request:
{
  "message": "Scan 192.168.1.0/24",
  "sessionId": "abc-123-def",  // Optional - auto-generated if missing
  "useTools": true
}

Response:
{
  "response": "Scan results...",
  "sessionId": "abc-123-def",
  "taskId": 1,
  "isComplete": true,
  "executionTimeMs": 5000,
  "error": null
}
```

---

## Performance Considerations

### Memory Usage
- **Per Session**: ~100KB - 1MB depending on conversation length
- **Total Capacity**: Can handle 1000+ concurrent sessions
- **Auto-cleanup**: Removes sessions after 24 hours

### Token Usage
- **Tracked per session**: `MemoryManager.getTotalTokensUsed()`
- **Reported in responses**: Helps with API cost tracking

### Execution Time
- **Target**: < 5 seconds per request
- **Maximum**: Configurable via timeout settings
- **Timeout handling**: Graceful shutdown with partial results

---

## Logging & Debugging

### Log Levels

```
DEBUG: Detailed iteration info, tool calls, memory operations
INFO:  Session creation, task completion, high-level operations
WARN:  Tool failures, iteration limits, memory cleanup
ERROR: Execution failures, unhandled exceptions
```

### Debug Output Example

```
[DEBUG] Created execution context: exec-123 for session: sess-456
[DEBUG] Starting iteration 0 for execution: exec-123
[DEBUG] Agent thinking (iteration 0): Analyzing user request to scan...
[DEBUG] Agent executing action (iteration 0): Running Nmap scan on 192.168.1.0/24
[DEBUG] Tool execution recorded: nmap - SUCCESS in 3500ms
[DEBUG] Decision recorded (iteration 0): Network scan completed
[INFO] Execution exec-123 completed in 3500ms - Success: true
```

---

## Future Enhancements

1. **Persistent Memory**: Move from in-memory to database storage
2. **Vector Embeddings**: Add semantic search to memory
3. **Tool Chaining**: Define dependencies between tool executions
4. **Parallel Execution**: Execute multiple tools simultaneously
5. **Advanced Planning**: Use goal-based planning for tool selection
6. **Learning**: Store successful patterns for reuse
7. **Custom Tools**: Plugin architecture for user-defined tools

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                  AIAgentController                      │
│                   (REST API)                            │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│              SecurityAIAgent                            │
│         (Main orchestration service)                    │
└──────────────┬──────────────────┬──────────────────────┘
               │                  │
        ┌──────▼──────┐    ┌──────▼──────────┐
        │ Memory      │    │ Execution       │
        │ Manager     │    │ Orchestrator    │
        │             │    │                 │
        │ - Sessions  │    │ - Context       │
        │ - History   │    │ - Iterations    │
        │ - Tools     │    │ - State Mgmt    │
        │ - Reasoning │    │ - Decisions     │
        └──────┬──────┘    └──────┬──────────┘
               │                  │
        ┌──────▼──────────────────▼───┐
        │   LLM Provider Service       │
        │   (Gemini/Claude/GPT-4)      │
        └──────┬──────────────────────┘
               │
        ┌──────▼────────────────────┐
        │     Tool Execution        │
        │                           │
        ├─ NmapTool                 │
        ├─ VirusTotalTool           │
        ├─ CVESearchTool            │
        ├─ FileExplorerTool (NEW)   │
        ├─ DirectoryScannerTool (NEW)│
        ├─ NVDClient (Ready)        │
        └───────────────────────────┘
```

---

## Testing

```java
// Example unit test
@Test
public void testRecursiveReasoningWithMemory() {
    // Create context
    AgentExecutionContext context = orchestrator
        .createExecutionContext("session-1", "Scan for vulnerabilities");

    // Iteration 1
    orchestrator.startIteration(context);
    orchestrator.setThinking(context, "Need to scan network");
    // ... execute tools ...
    assertTrue(orchestrator.evaluateIteration(context));

    // Iteration 2
    orchestrator.nextIteration(context);
    // ... more iterations ...

    // Complete
    orchestrator.completeExecution(context, "Found 3 critical CVEs", true);

    // Verify memory
    assertEquals(2, memoryManager.getToolExecutionHistory("session-1").size());
    assertEquals(1, memoryManager.getConversationHistory("session-1").size());
}
```

---

**Version**: 1.0
**Last Updated**: 2025-11-06
**Status**: Production Ready
