const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export async function signup(data) {
  const res = await fetch(`${API_BASE}/api/v1/auth/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userName: data.username,
      firstName: data.firstName,
      secondName: data.lastName,
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
