import '../styles.css'

const loading = document.querySelector<HTMLElement>('#loading')!
const error = document.querySelector<HTMLElement>('#error')!
const language = window.navigator.language.toLowerCase()
const copy = language.startsWith('ca')
  ? { loading: 'Carregant Nyam Nyam…', error: 'No s’ha pogut iniciar Nyam Nyam.' }
  : language.startsWith('es')
    ? { loading: 'Cargando Nyam Nyam…', error: 'No se ha podido iniciar Nyam Nyam.' }
    : { loading: 'Loading Nyam Nyam…', error: 'Nyam Nyam could not start.' }

loading.textContent = copy.loading
error.textContent = copy.error

window.addEventListener('nyam-game-ready', () => {
  loading.hidden = true
}, { once: true })

window.addEventListener('nyam-game-error', () => {
  loading.hidden = true
  error.hidden = false
}, { once: true })

const runtime = document.createElement('script')
runtime.src = `${import.meta.env.BASE_URL}nyamnyam.js`
runtime.addEventListener('load', () => {
  if (typeof window.main !== 'function') {
    loading.hidden = true
    error.hidden = false
    return
  }

  try {
    window.main()
  } catch (cause) {
    if (import.meta.env.DEV) console.error(cause)
    loading.hidden = true
    error.hidden = false
  }
}, { once: true })
runtime.addEventListener('error', () => {
  loading.hidden = true
  error.hidden = false
}, { once: true })
document.body.append(runtime)
