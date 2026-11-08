interface Window {
  main?: () => void
}

interface ImportMetaEnv {
  readonly BASE_URL: string
  readonly DEV: boolean
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
