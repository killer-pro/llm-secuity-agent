import React, { useState } from 'react';
import { Search } from 'lucide-react';
import apiService from '@services/api.service';

/**
 * CVE search component
 */
export default function CVESearch() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;

    setLoading(true);
    try {
      const response = await apiService.searchCVEs(query);
      setResults(response.results || []);
    } catch (error) {
      console.error('Search error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">CVE Search</h1>

      {/* Search Form */}
      <div className="bg-white rounded-lg shadow p-6">
        <form onSubmit={handleSearch} className="flex gap-2">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search for CVEs (e.g., Apache, Windows, MySQL)..."
            className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary-600"
          />
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:bg-gray-400 flex items-center gap-2"
          >
            <Search className="w-4 h-4" />
            Search
          </button>
        </form>
      </div>

      {/* Results */}
      <div className="bg-white rounded-lg shadow p-6">
        {loading ? (
          <p className="text-center text-gray-500">Searching...</p>
        ) : results.length === 0 ? (
          <p className="text-center text-gray-500">
            {query ? 'No results found' : 'Enter a search query to begin'}
          </p>
        ) : (
          <div className="space-y-4">
            {results.map((result, index) => (
              <div key={index} className="border border-gray-200 rounded-lg p-4">
                <h3 className="font-semibold text-gray-900">{result.cveId}</h3>
                <p className="text-gray-600 text-sm mt-1">{result.description}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
