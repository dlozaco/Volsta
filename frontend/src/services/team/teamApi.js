import { http } from '../http'

// ---------- TEAMS ----------

export async function getAllTeams() {
  const data = await http.get('/api/v1/teams', { auth: false })
  // The backend returns a Spring `Page<TeamResponse>` for this endpoint.
  // Normalize to an array: prefer `content` when present, otherwise assume the
  // response is already an array. Return an empty array as a safe default.
  if (Array.isArray(data)) return data
  return data?.content ?? []
}

export async function getTeamById(id) {
  return http.get(`/api/v1/teams/${id}`, { auth: false })
}

export async function getTeamByName(name) {
  return http.get(`/api/v1/teams/name/${encodeURIComponent(name)}`, { auth: false })
}

export async function getMyTeams() {
  const data = await http.get('/api/v1/teams/my', { auth: true })
  if (Array.isArray(data)) return data
  return data?.content ?? []
}

export async function createTeam(data) {
  return http.post('/api/v1/teams', data, { auth: true })
}

export async function updateTeam(id, data) {
  return http.put(`/api/v1/teams/${id}`, data, { auth: true })
}

export async function deleteTeam(id) {
  return http.del(`/api/v1/teams/${id}`, { auth: true })
}
