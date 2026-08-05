import { http } from '../http'

// ---------- MATCHES ----------

export async function getAllMatches() {
  return http.get('/api/v1/matches', { auth: false })
}

export async function getMatch(id) {
  return http.get(`/api/v1/matches/${id}`, { auth: false })
}

export async function getMatchesByTeam(teamId) {
  return http.get(`/api/v1/matches/team/${teamId}`, { auth: false })
}

export async function getMatchSets(id) {
  return http.get(`/api/v1/matches/${id}/sets`, { auth: false })
}

export async function getMatchStats(id) {
  return http.get(`/api/v1/matches/${id}/stats`, { auth: false })
}

export async function createMatch(data) {
  return http.post('/api/v1/matches', data)
}

export async function rescheduleMatch(id, data) {
  return http.put(`/api/v1/matches/${id}`, data)
}

export async function deleteMatch(id) {
  return http.del(`/api/v1/matches/${id}`)
}

export async function addMatchSet(id, data) {
  return http.post(`/api/v1/matches/${id}/sets`, data)
}

export async function addSetParticipation(id, setId, data) {
  return http.post(`/api/v1/matches/${id}/sets/${setId}/participations`, data)
}

// ---------- NOTES ----------

export async function createNote(matchId, data) {
  return http.post(`/api/v1/matches/${matchId}/notes`, data)
}

export async function deleteNote(matchId, noteId) {
  return http.del(`/api/v1/matches/${matchId}/notes/${noteId}`)
}
