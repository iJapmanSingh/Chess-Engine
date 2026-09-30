import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// In dev, /api calls are forwarded to the Spring Boot server, so no CORS setup is needed locally.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: { '/api': 'http://localhost:8080' },
  },
});
