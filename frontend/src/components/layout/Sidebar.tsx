import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  Home,
  MessageSquare,
  Zap,
  Search,
  Settings as SettingsIcon,
  Shield,
} from 'lucide-react';

/**
 * Sidebar navigation component
 */
export default function Sidebar() {
  const location = useLocation();

  const navItems = [
    { path: '/', label: 'Dashboard', icon: Home },
    { path: '/chat', label: 'Chat Agent', icon: MessageSquare },
    { path: '/scans', label: 'Scans', icon: Zap },
    { path: '/cve', label: 'CVE Search', icon: Search },
    { path: '/settings', label: 'Settings', icon: SettingsIcon },
  ];

  return (
    <div className="w-64 bg-gray-900 text-white flex flex-col">
      {/* Logo */}
      <div className="p-6 border-b border-gray-800 flex items-center gap-3">
        <Shield className="w-8 h-8 text-primary-500" />
        <div>
          <h1 className="text-xl font-bold">Security AI</h1>
          <p className="text-xs text-gray-400">Platform v1.0</p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4 space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;

          return (
            <Link
              key={item.path}
              to={item.path}
              className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                isActive
                  ? 'bg-primary-600 text-white'
                  : 'text-gray-300 hover:bg-gray-800'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>

      {/* Footer */}
      <div className="p-4 border-t border-gray-800 text-xs text-gray-400">
        <p>© 2024 Security AI Platform</p>
        <p>All rights reserved</p>
      </div>
    </div>
  );
}
