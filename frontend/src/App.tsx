import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from '@components/layout/MainLayout';
import Dashboard from '@components/dashboard/Dashboard';
import ChatInterface from '@components/chat/ChatInterface';
import ScanResults from '@components/scan/ScanResults';
import CVESearch from '@components/cve/CVESearch';
import Settings from '@components/settings/Settings';
import { useSettingsStore } from '@store';
import './index.css';

/**
 * Main App component with routing
 */
function App() {
  const theme = useSettingsStore((state) => state.theme);

  useEffect(() => {
    // Apply theme to document
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [theme]);

  return (
    <Router>
      <MainLayout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/chat" element={<ChatInterface />} />
          <Route path="/scans" element={<ScanResults />} />
          <Route path="/cve" element={<CVESearch />} />
          <Route path="/settings" element={<Settings />} />
        </Routes>
      </MainLayout>
    </Router>
  );
}

export default App;
