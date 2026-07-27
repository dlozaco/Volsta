const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export async function signup(data) {
  const res = await fetch(`${API_BASE}/api/v1/auth/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userName: data.username,
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      phoneNumber: data.phone,
      password: data.password,
      authority: 'manager',
    }),
  })

  const body = await res.json()

  if (!res.ok) {
    const error = new Error(body.message || 'Registration failed')
    error.status = res.status
    error.fieldErrors = body.fieldErrors || {}
    throw error
  }

  return body
}

export async function login(username, password) {
  const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  })

  const body = await res.json()

  if (!res.ok) {
    const error = new Error(body.message || 'Login failed')
    error.status = res.status
    throw error
  }

  return body
}

export async function getMe(token) {
  const res = await fetch(`${API_BASE}/api/v1/auth/me`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  })

  const body = await res.json()

  if (!res.ok) {
    const error = new Error(body.message || 'Failed to get user')
    error.status = res.status
    throw error
  }

  return body
}

export async function getTeamPageByName(name, token) {
  const res = await fetch(`${API_BASE}/api/v1/teams/name/${encodeURIComponent(name)}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  })

  const body = await res.json()

  if (!res.ok) {
    const error = new Error(body.message || 'Team not found')
    error.status = res.status
    throw error
  }

  return body
}

export async function getManagerProfile(token) {
  const res = await fetch(`${API_BASE}/api/v1/profile/manager`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  })

  const body = await res.json()

  if (!res.ok) {
    const error = new Error(body.message || 'Failed to get profile')
    error.status = res.status
    throw error
  }

  return body
}

export async function updateManagerProfile(token, data) {
  const res = await fetch(`${API_BASE}/api/v1/profile/manager`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(data),
  })

  const body = await res.json()

  if (!res.ok) {
    const error = new Error(body.message || 'Failed to update profile')
    error.status = res.status
    throw error
  }

  return body
}
