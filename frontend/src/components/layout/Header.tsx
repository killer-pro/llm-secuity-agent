import React, { useState } from 'react';
import { Bell, User, LogOut, Menu } from 'lucide-react';

/**
 * Header component with notifications and user menu
 */
export default function Header() {
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [notifications, setNotifications] = useState(0);

  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      {/* Left side - Title */}
      <div className="flex items-center gap-3">
        <Menu className="w-6 h-6 text-gray-400 cursor-pointer hover:text-gray-600" />
        <h2 className="text-xl font-semibold text-gray-800">Security AI Platform</h2>
      </div>

      {/* Right side - Notifications and User */}
      <div className="flex items-center gap-4">
        {/* Notifications */}
        <div className="relative">
          <button className="relative p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
            <Bell className="w-6 h-6" />
            {notifications > 0 && (
              <span className="absolute top-0 right-0 w-5 h-5 bg-danger-600 text-white text-xs rounded-full flex items-center justify-center">
                {notifications}
              </span>
            )}
          </button>
        </div>

        {/* User Menu */}
        <div className="relative">
          <button
            onClick={() => setShowUserMenu(!showUserMenu)}
            className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <div className="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center text-white text-sm font-semibold">
              SA
            </div>
            <span className="text-sm font-medium text-gray-700">Admin</span>
          </button>

          {/* Dropdown Menu */}
          {showUserMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
              <div className="px-4 py-3 border-b border-gray-100">
                <p className="text-sm font-medium text-gray-900">Admin User</p>
                <p className="text-xs text-gray-500">admin@example.com</p>
              </div>

              <div className="py-2">
                <button className="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2">
                  <User className="w-4 h-4" />
                  Profile
                </button>

                <button className="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2 border-t border-gray-100">
                  <LogOut className="w-4 h-4" />
                  Logout
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
