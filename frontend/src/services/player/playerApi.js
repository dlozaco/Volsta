import { http } from '../http'

// ---------- PLAYERS ----------

export async function getTeamPlayers(teamId) {
  return http.get(`/api/v1/teams/${teamId}/players`, { auth: false })
}

export async function createPlayer(teamId, data) {
  return http.post(`/api/v1/teams/${teamId}/players`, data)
}

export async function updatePlayer(id, data) {
  return http.put(`/api/v1/players/${id}`, data)
}

export async function softDeletePlayer(id) {
  return http.del(`/api/v1/players/${id}`)
}
