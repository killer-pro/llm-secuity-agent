import { create } from 'zustand';
import { ChatMessage, ScanResult, ProviderStatus, Task } from '@types';

/**
 * Agent Store - Manages chat and agent state
 */
interface AgentStore {
  messages: ChatMessage[];
  currentSessionId: string;
  isLoading: boolean;
  addMessage: (message: ChatMessage) => void;
  clearMessages: () => void;
  setLoading: (loading: boolean) => void;
  setSessionId: (sessionId: string) => void;
}

export const useAgentStore = create<AgentStore>((set) => ({
  messages: [],
  currentSessionId: '',
  isLoading: false,

  addMessage: (message: ChatMessage) =>
    set((state) => ({
      messages: [...state.messages, message],
    })),

  clearMessages: () => set({ messages: [] }),

  setLoading: (loading: boolean) => set({ isLoading: loading }),

  setSessionId: (sessionId: string) => set({ currentSessionId: sessionId }),
}));

/**
 * Scan Store - Manages scan state
 */
interface ScanStore {
  scans: ScanResult[];
  currentScan: ScanResult | null;
  isScanning: boolean;
  addScan: (scan: ScanResult) => void;
  setScans: (scans: ScanResult[]) => void;
  setCurrentScan: (scan: ScanResult | null) => void;
  updateScan: (id: number, scan: Partial<ScanResult>) => void;
  setIsScanning: (scanning: boolean) => void;
  removeScan: (id: number) => void;
}

export const useScanStore = create<ScanStore>((set) => ({
  scans: [],
  currentScan: null,
  isScanning: false,

  addScan: (scan: ScanResult) =>
    set((state) => ({
      scans: [scan, ...state.scans],
    })),

  setScans: (scans: ScanResult[]) => set({ scans }),

  setCurrentScan: (scan: ScanResult | null) => set({ currentScan: scan }),

  updateScan: (id: number, updateData: Partial<ScanResult>) =>
    set((state) => ({
      scans: state.scans.map((scan) =>
        scan.id === id ? { ...scan, ...updateData } : scan
      ),
      currentScan:
        state.currentScan?.id === id
          ? { ...state.currentScan, ...updateData }
          : state.currentScan,
    })),

  setIsScanning: (scanning: boolean) => set({ isScanning: scanning }),

  removeScan: (id: number) =>
    set((state) => ({
      scans: state.scans.filter((scan) => scan.id !== id),
    })),
}));

/**
 * Provider Store - Manages LLM provider state
 */
interface ProviderStore {
  providers: ProviderStatus[];
  activeProvider: ProviderStatus | null;
  setProviders: (providers: ProviderStatus[]) => void;
  setActiveProvider: (provider: ProviderStatus | null) => void;
  updateProvider: (name: string, provider: Partial<ProviderStatus>) => void;
}

export const useProviderStore = create<ProviderStore>((set) => ({
  providers: [],
  activeProvider: null,

  setProviders: (providers: ProviderStatus[]) => set({ providers }),

  setActiveProvider: (provider: ProviderStatus | null) => set({ activeProvider: provider }),

  updateProvider: (name: string, updateData: Partial<ProviderStatus>) =>
    set((state) => ({
      providers: state.providers.map((p) =>
        p.providerName === name ? { ...p, ...updateData } : p
      ),
    })),
}));

/**
 * Settings Store - Manages user preferences
 */
interface SettingsStore {
  theme: 'light' | 'dark';
  autoScans: boolean;
  notificationsEnabled: boolean;
  setTheme: (theme: 'light' | 'dark') => void;
  setAutoScans: (enabled: boolean) => void;
  setNotifications: (enabled: boolean) => void;
}

export const useSettingsStore = create<SettingsStore>((set) => ({
  theme: 'light',
  autoScans: false,
  notificationsEnabled: true,

  setTheme: (theme) => set({ theme }),

  setAutoScans: (enabled) => set({ autoScans: enabled }),

  setNotifications: (enabled) => set({ notificationsEnabled: enabled }),
}));
