import { Route, Routes, Navigate } from 'react-router-dom'
import SignUp from './auth/register/SignUp'
import SignIn from './auth/login/SignIn'
import { SidebarProvider, SidebarTrigger } from '#components/ui/sidebar'
import { AppSidebar } from './sideBar/sidebar'
import Home from './homepage/Home'
import TeamPage from './manager/teamEdit/TeamPage'
import { AuthProvider, useAuth } from './auth/AuthContext'

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth()

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  return children
}

function PublicRoute({ children }) {
  const { user, loading } = useAuth()

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>
  }

  if (user) {
    return <Navigate to="/" replace />
  }

  return children
}

function App() {
  return (
    <AuthProvider>
      <SidebarProvider defaultOpen={false}>
        <AppSidebar />

        <main className="flex-1 w-full p-4 overflow-x-hidden">
          <SidebarTrigger className="mb-4 size-4" />

          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<PublicRoute><SignUp /></PublicRoute>} />
            <Route path="/login" element={<PublicRoute><SignIn /></PublicRoute>} />
            <Route path="/teams/:name" element={<TeamPage />} />
          </Routes>

        </main>

      </SidebarProvider>
    </AuthProvider>
  )
}

export default App
