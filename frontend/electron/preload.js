const { contextBridge, ipcMain } = require('electron');

contextBridge.exposeInMainWorld('electron', {
  ipcRenderer: {
    send: (channel, args) => {
      // Allowed channels
      const validChannels = ['app:minimize', 'app:maximize', 'app:close'];
      if (validChannels.includes(channel)) {
        // Send to main
      }
    },
    receive: (channel, func) => {
      // Allowed channels
      const validChannels = ['app:notification'];
      if (validChannels.includes(channel)) {
        // Setup listener
      }
    },
  },
  app: {
    getName: () => 'Security AI Platform',
    getVersion: () => '1.0.0',
  },
});
