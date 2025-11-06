import React from 'react';
import { useScanStore } from '@store';

/**
 * Scan results component
 */
export default function ScanResults() {
  const { scans } = useScanStore();

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">Scan Results</h1>

      <div className="bg-white rounded-lg shadow overflow-auto">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
              <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Name</th>
              <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Target</th>
              <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Status</th>
              <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Vulnerabilities</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {scans.map((scan) => (
              <tr key={scan.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 text-sm text-gray-900">{scan.id}</td>
                <td className="px-6 py-4 text-sm text-gray-900">{scan.scanName}</td>
                <td className="px-6 py-4 text-sm text-gray-600">{scan.target}</td>
                <td className="px-6 py-4 text-sm">
                  <span className="px-2 py-1 bg-primary-100 text-primary-700 rounded text-xs font-medium">
                    {scan.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-sm text-gray-900">{scan.vulnerabilitiesFound}</td>
              </tr>
            ))}
          </tbody>
        </table>

        {scans.length === 0 && (
          <div className="text-center py-12">
            <p className="text-gray-500">No scans available. Start a new scan to see results.</p>
          </div>
        )}
      </div>
    </div>
  );
}
