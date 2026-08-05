const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token) {
  if (token) {
    localStorage.setItem('token', token)
  } else {
    localStorage.removeItem('token')
  }
}

function onUnauthorized() {
  setToken(null)
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

async function request(path, { method = 'GET', body, auth = true } = {}) {
  const headers = { 'Content-Type': 'application/json' }

  if (auth) {
    const token = getToken()
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
  }

  let res
  try {
    res = await fetch(`${API_BASE}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    const error = new Error('Could not reach the server')
    error.status = 0
    throw error
  }

  if (res.status === 401) {
    onUnauthorized()
  }

  const text = await res.text()
  const data = text ? JSON.parse(text) : null

  if (!res.ok) {
    const error = new Error(data?.message || 'Request failed')
    error.status = res.status
    error.fieldErrors = data?.fieldErrors || {}
    throw error
  }

  return data
}

export const http = {
  get: (path, opts) => request(path, { ...opts, method: 'GET' }),
  post: (path, body, opts) => request(path, { ...opts, method: 'POST', body }),
  put: (path, body, opts) => request(path, { ...opts, method: 'PUT', body }),
  del: (path, opts) => request(path, { ...opts, method: 'DELETE' }),
}
