import React, { useEffect, useState } from 'react';
import { AlertCircle, TrendingUp, Activity, Zap } from 'lucide-react';
import { useScanStore } from '@store';
import apiService from '@services/api.service';

/**
 * Dashboard component showing overview statistics
 */
export default function Dashboard() {
  const { scans } = useScanStore();
  const [stats, setStats] = useState({
    totalScans: 0,
    activeScans: 0,
    vulnerabilities: 0,
    criticalIssues: 0,
  });

  useEffect(() => {
    const loadStats = async () => {
      try {
        const allScans = await apiService.getAllScans();
        setStats({
          totalScans: allScans.length,
          activeScans: allScans.filter((s) => s.status === 'IN_PROGRESS').length,
          vulnerabilities: allScans.reduce((sum, s) => sum + (s.vulnerabilitiesFound || 0), 0),
          criticalIssues: allScans.reduce((sum, s) => sum + (s.criticalIssues || 0), 0),
        });
      } catch (error) {
        console.error('Error loading dashboard stats:', error);
      }
    };

    loadStats();
  }, [scans]);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-2">Overview of your security scans and vulnerabilities</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total Scans */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Total Scans</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalScans}</p>
            </div>
            <Zap className="w-12 h-12 text-primary-600 opacity-20" />
          </div>
        </div>

        {/* Active Scans */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Active Scans</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{stats.activeScans}</p>
            </div>
            <Activity className="w-12 h-12 text-blue-600 opacity-20" />
          </div>
        </div>

        {/* Vulnerabilities */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Vulnerabilities</p>
              <p className="text-3xl font-bold text-gray-900 mt-2">{stats.vulnerabilities}</p>
            </div>
            <TrendingUp className="w-12 h-12 text-warning-600 opacity-20" />
          </div>
        </div>

        {/* Critical Issues */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm">Critical Issues</p>
              <p className="text-3xl font-bold text-danger-600 mt-2">{stats.criticalIssues}</p>
            </div>
            <AlertCircle className="w-12 h-12 text-danger-600 opacity-20" />
          </div>
        </div>
      </div>

      {/* Recent Scans */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">Recent Scans</h2>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="border-b border-gray-200">
              <tr>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Scan Name</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Target</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Status</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Progress</th>
              </tr>
            </thead>
            <tbody>
              {scans.length === 0 ? (
                <tr>
                  <td colSpan={4} className="text-center py-8 text-gray-500">
                    No scans yet. Start a new scan from the Chat interface.
                  </td>
                </tr>
              ) : (
                scans.slice(0, 5).map((scan) => (
                  <tr key={scan.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="py-3 px-4 text-gray-900">{scan.scanName}</td>
                    <td className="py-3 px-4 text-gray-600">{scan.target}</td>
                    <td className="py-3 px-4">
                      <span
                        className={`px-3 py-1 rounded-full text-sm font-medium ${
                          scan.status === 'COMPLETED'
                            ? 'bg-success-100 text-success-700'
                            : scan.status === 'IN_PROGRESS'
                            ? 'bg-blue-100 text-blue-700'
                            : 'bg-gray-100 text-gray-700'
                        }`}
                      >
                        {scan.status}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div
                          className="bg-primary-600 h-2 rounded-full"
                          style={{ width: `${scan.progress}%` }}
                        ></div>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
