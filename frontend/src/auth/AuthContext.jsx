/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { login as apiLogin, getMe, logout as apiLogout } from '../services/api'
import { getToken, setToken } from '../services/http'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [token, setTokenState] = useState(() => getToken())
  const [loading, setLoading] = useState(() => Boolean(getToken()))

  useEffect(() => {
    if (!token) return
    getMe()
      .then(data => setUser(data))
      .catch(() => {
        setToken(null)
        setTokenState(null)
      })
      .finally(() => setLoading(false))
  }, [token])

  const login = async (username, password) => {
    const data = await apiLogin(username, password)
    setToken(data.token)
    setTokenState(data.token)
    setUser(data)
    return data
  }

  const logout = useCallback(async () => {
    try {
      await apiLogout()
    } catch {
      // Ignore network errors on logout; the local session is cleared anyway
    } finally {
      setToken(null)
      setTokenState(null)
      setUser(null)
      // Replace the current history entry so the browser Back button
      // cannot return to an authenticated page after logging out (US-USER-04).
      if (window.location.pathname !== '/login') {
        window.location.replace('/login')
      }
    }
  }, [])

  const isManager = user?.authority === 'MANAGER'
  const isAdmin = user?.authority === 'ADMIN'

  return (
    <AuthContext.Provider value={{ user, token, login, logout, loading, isManager, isAdmin }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
