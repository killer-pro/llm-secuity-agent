import axios, { AxiosInstance } from 'axios';
import { ChatRequest, AgentResponse, ScanRequest, ScanResult, ProviderStatus } from '@types';

/**
 * API Service for backend communication
 */
class ApiService {
  private api: AxiosInstance;
  private baseURL: string;

  constructor() {
    this.baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

    this.api = axios.create({
      baseURL: this.baseURL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add response interceptor for error handling
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        console.error('API Error:', error);
        return Promise.reject(error);
      }
    );
  }

  /**
   * Chat with AI Agent
   */
  async chat(request: ChatRequest): Promise<AgentResponse> {
    const response = await this.api.post<AgentResponse>('/agent/chat', request);
    return response.data;
  }

  /**
   * Get agent status
   */
  async getAgentStatus(): Promise<AgentResponse> {
    const response = await this.api.get<AgentResponse>('/agent/status');
    return response.data;
  }

  /**
   * Initiate a scan
   */
  async initiateScan(request: ScanRequest): Promise<ScanResult> {
    const response = await this.api.post<ScanResult>('/scans/initiate', request);
    return response.data;
  }

  /**
   * Get all scans
   */
  async getAllScans(): Promise<ScanResult[]> {
    const response = await this.api.get<ScanResult[]>('/scans/');
    return response.data;
  }

  /**
   * Get recent scans
   */
  async getRecentScans(): Promise<ScanResult[]> {
    const response = await this.api.get<ScanResult[]>('/scans/recent');
    return response.data;
  }

  /**
   * Get scan by ID
   */
  async getScanById(id: number): Promise<ScanResult> {
    const response = await this.api.get<ScanResult>(`/scans/${id}`);
    return response.data;
  }

  /**
   * Update scan progress
   */
  async updateScanProgress(id: number, progress: number): Promise<ScanResult> {
    const response = await this.api.put<ScanResult>(`/scans/${id}/progress`, null, {
      params: { progress },
    });
    return response.data;
  }

  /**
   * Get critical scans
   */
  async getCriticalScans(): Promise<ScanResult[]> {
    const response = await this.api.get<ScanResult[]>('/scans/critical');
    return response.data;
  }

  /**
   * Get scans with vulnerabilities
   */
  async getScansWithVulnerabilities(): Promise<ScanResult[]> {
    const response = await this.api.get<ScanResult[]>('/scans/vulnerabilities');
    return response.data;
  }

  /**
   * Delete a scan
   */
  async deleteScan(id: number): Promise<void> {
    await this.api.delete(`/scans/${id}`);
  }

  /**
   * Get all providers
   */
  async getAllProviders(): Promise<ProviderStatus[]> {
    const response = await this.api.get<ProviderStatus[]>('/providers/');
    return response.data;
  }

  /**
   * Get active provider
   */
  async getActiveProvider(): Promise<ProviderStatus> {
    const response = await this.api.get<ProviderStatus>('/providers/active');
    return response.data;
  }

  /**
   * Switch provider
   */
  async switchProvider(providerName: string): Promise<ProviderStatus> {
    const response = await this.api.post<ProviderStatus>('/providers/switch', null, {
      params: { providerName },
    });
    return response.data;
  }

  /**
   * Get model capabilities
   */
  async getCapabilities(): Promise<string> {
    const response = await this.api.get<string>('/providers/capabilities');
    return response.data;
  }

  /**
   * Search CVEs
   */
  async searchCVEs(query: string): Promise<any> {
    const response = await this.api.get('/cve/search', {
      params: { query },
    });
    return response.data;
  }

  /**
   * Get CVE details
   */
  async getCVEDetails(cveId: string): Promise<any> {
    const response = await this.api.get(`/cve/${cveId}`);
    return response.data;
  }

  /**
   * Search CVEs by severity
   */
  async searchBySeverity(level: string): Promise<any> {
    const response = await this.api.get(`/cve/severity/${level}`);
    return response.data;
  }
}

export default new ApiService();
