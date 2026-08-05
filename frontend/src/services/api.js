import { http } from './http'

// ---------- AUTH ----------

export async function signup(data) {
  return http.post('/api/v1/auth/signup', {
    userName: data.username,
    firstName: data.firstName,
    lastName: data.lastName,
    email: data.email,
    phoneNumber: data.phone,
    password: data.password,
    authority: 'manager',
  }, { auth: false })
}

export async function login(username, password) {
  return http.post('/api/v1/auth/login', { username, password }, { auth: false })
}

export async function getMe() {
  return http.get('/api/v1/auth/me')
}

export async function logout() {
  try {
    return await http.post('/api/v1/auth/logout')
  } finally {
    // Always clear the local session, even if the server is unreachable
  }
}

// ---------- PROFILE ----------

export async function getManagerProfile() {
  return http.get('/api/v1/profile/manager')
}

export async function updateManagerProfile(data) {
  return http.put('/api/v1/profile/manager', data)
}

export async function changePassword(currentPassword, newPassword) {
  return http.put('/api/v1/profile/password', { currentPassword, newPassword })
}
