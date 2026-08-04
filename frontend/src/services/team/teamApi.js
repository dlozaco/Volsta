const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export async function getAllTeams(){
    const res = await fetch(`${API_BASE}/api/v1/teams`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })

    const body = await res.json()

    if(!res.ok){
        const error = new Error(body.message || 'Failed to get all teams')
        error.status = res.status
        throw error
    }

    return body
}


export async function getTeamPageByName(name, token) {
  const headers = { 'Content-Type': 'application/json' }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  const res = await fetch(`${API_BASE}/api/v1/teams/name/${encodeURIComponent(name)}`, {
    method: 'GET',
    headers,
  })

  const body = await res.json()

  if (!res.ok) {
    const error = new Error(body.message || 'Team not found')
    error.status = res.status
    throw error
  }

  return body
}