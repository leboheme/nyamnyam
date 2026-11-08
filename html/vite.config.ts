import { defineConfig } from 'vite'

export default defineConfig({
  root: 'webapp',
  base: './',
  publicDir: '../build/web-runtime',
  build: {
    assetsDir: 'shell',
    emptyOutDir: true,
    outDir: '../build/web'
  }
})
