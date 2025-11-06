import React, { useEffect, useState } from 'react';
import { useSettingsStore, useProviderStore } from '@store';
import { ProviderStatus } from '@types';
import apiService from '@services/api.service';

/**
 * Settings component
 */
export default function Settings() {
  const { theme, autoScans, notificationsEnabled, setTheme, setAutoScans, setNotifications } =
    useSettingsStore();
  const { providers, setProviders, activeProvider } = useProviderStore();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadProviders = async () => {
      setLoading(true);
      try {
        const data = await apiService.getAllProviders();
        setProviders(data);
      } catch (error) {
        console.error('Error loading providers:', error);
      } finally {
        setLoading(false);
      }
    };

    loadProviders();
  }, []);

  const handleSwitchProvider = async (providerName: string) => {
    try {
      await apiService.switchProvider(providerName);
      // Reload providers
      const data = await apiService.getAllProviders();
      setProviders(data);
    } catch (error) {
      console.error('Error switching provider:', error);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Settings</h1>

      {/* Theme Settings */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Appearance</h2>
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <label className="text-gray-700">Theme</label>
            <select
              value={theme}
              onChange={(e) => setTheme(e.target.value as 'light' | 'dark')}
              className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none"
            >
              <option value="light">Light</option>
              <option value="dark">Dark</option>
            </select>
          </div>
        </div>
      </div>

      {/* Notification Settings */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Notifications</h2>
        <div className="space-y-4">
          <label className="flex items-center gap-3">
            <input
              type="checkbox"
              checked={notificationsEnabled}
              onChange={(e) => setNotifications(e.target.checked)}
              className="w-4 h-4 rounded"
            />
            <span className="text-gray-700">Enable notifications</span>
          </label>

          <label className="flex items-center gap-3">
            <input
              type="checkbox"
              checked={autoScans}
              onChange={(e) => setAutoScans(e.target.checked)}
              className="w-4 h-4 rounded"
            />
            <span className="text-gray-700">Enable automatic scans</span>
          </label>
        </div>
      </div>

      {/* Provider Settings */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">LLM Provider</h2>

        {loading ? (
          <p className="text-gray-500">Loading providers...</p>
        ) : (
          <div className="space-y-3">
            {providers.map((provider) => (
              <div key={provider.providerName} className="flex items-center justify-between p-3 border border-gray-200 rounded-lg">
                <div>
                  <h3 className="font-semibold text-gray-900">{provider.providerName}</h3>
                  <p className="text-sm text-gray-600">{provider.modelName}</p>
                </div>

                {provider.isActive ? (
                  <span className="px-3 py-1 bg-success-100 text-success-700 rounded-full text-sm font-medium">
                    Active
                  </span>
                ) : provider.isAvailable ? (
                  <button
                    onClick={() => handleSwitchProvider(provider.providerName)}
                    className="px-3 py-1 bg-primary-600 text-white rounded-full text-sm font-medium hover:bg-primary-700"
                  >
                    Switch
                  </button>
                ) : (
                  <span className="px-3 py-1 bg-gray-100 text-gray-600 rounded-full text-sm font-medium">
                    Unavailable
                  </span>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* About */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">About</h2>
        <div className="space-y-2 text-sm text-gray-600">
          <p>Security AI Platform v1.0.0</p>
          <p>© 2024 Security Team. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
}
