/**
 * Agent and Chat Types
 */
export interface ChatMessage {
  id: string;
  content: string;
  sender: 'user' | 'agent';
  timestamp: number;
  sessionId: string;
}

export interface ChatRequest {
  message: string;
  sessionId: string;
  useTools?: boolean;
  maxTokens?: number;
}

export interface AgentResponse {
  response: string;
  sessionId: string;
  taskId?: number;
  toolExecutions?: ToolExecution[];
  isComplete: boolean;
  scanMetadata?: ScanMetadata;
  error?: string;
  executionTimeMs: number;
}

export interface ToolExecution {
  toolName: string;
  toolInput: string;
  toolOutput: string;
  executionTimeMs: number;
  success: boolean;
}

export interface ScanMetadata {
  hostsDiscovered: number;
  vulnerabilitiesFound: number;
  criticalIssues: number;
  maxCvssScore: number;
  scanDurationSeconds: number;
}

/**
 * Scan Types
 */
export type ScanType = 'QUICK' | 'FULL' | 'SERVICE_DETECTION' | 'VULNERABILITY' | 'FILE_ANALYSIS' | 'CUSTOM';
export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED' | 'CANCELLED' | 'PAUSED';

export interface ScanRequest {
  scanType: ScanType;
  target: string;
  scanName: string;
  parameters?: string;
  performVulnerabilityAnalysis?: boolean;
  performMalwareCheck?: boolean;
  timeoutMinutes?: number;
  sessionId?: string;
}

export interface ScanResult {
  id: number;
  scanType: ScanType;
  status: TaskStatus;
  target: string;
  scanName: string;
  description?: string;
  progress: number;
  hostsDiscovered: number;
  vulnerabilitiesFound: number;
  criticalIssues: number;
  maxCvssScore?: number;
  durationSeconds?: number;
  createdAt: string;
  startedAt?: string;
  completedAt?: string;
  errorMessage?: string;
  cveData?: CVEData[];
}

/**
 * CVE Types
 */
export interface CVEData {
  id: number;
  cveId: string;
  description: string;
  cvssScore: number;
  cvssVector: string;
  severity: string;
  cweIds?: string;
  references?: string;
  publishedDate: string;
  lastModifiedDate: string;
  affectedProducts?: string;
  mitigation?: string;
}

export interface CVESearchResult {
  cveId: string;
  severity: string;
  cvssScore: number;
  description: string;
  affectedProducts: string[];
}

/**
 * Provider Types
 */
export interface ProviderStatus {
  providerName: string;
  isAvailable: boolean;
  isActive: boolean;
  modelName: string;
  statusMessage: string;
  responseTimeMs?: number;
  errorMessage?: string;
  lastCheckedAt?: number;
  isLocal: boolean;
  supportedFeatures?: string;
}

/**
 * Dashboard Statistics
 */
export interface DashboardStats {
  totalScans: number;
  activeScans: number;
  vulnerabilitiesFound: number;
  criticalIssues: number;
  scansThisWeek: number;
  averageDuration: number;
}

/**
 * Task Types (for AI agent tasks)
 */
export interface Task {
  id: number;
  name: string;
  description: string;
  status: TaskStatus;
  toolType: string;
  toolInput: string;
  toolOutput: string;
  agentResponse: string;
  progress: number;
  errorMessage?: string;
  executionTimeMs: number;
  sessionId: string;
  createdAt: string;
  startedAt?: string;
  completedAt?: string;
}
