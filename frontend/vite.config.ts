import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  assetsInclude: ['**/*.gltf', '**/*.glb'],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/ws-endpoint': {
        target: 'ws://localhost:8080',
        ws: true,
      },
    },
  },
});
