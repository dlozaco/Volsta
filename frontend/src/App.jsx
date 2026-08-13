import { Route, Routes, Navigate } from 'react-router-dom'
import SignUp from './auth/register/SignUp'
import SignIn from './auth/login/SignIn'
import { SidebarProvider, SidebarTrigger } from '#components/ui/sidebar'
import { AppSidebar } from './sideBar/sidebar'
import Home from './homepage/Home'
import TeamPage from './manager/teamEdit/TeamPage'
import TeamCreate from './manager/teamCreate/TeamCreate'
import Dashboard from './manager/dashboard/Dashboard'
import ManagerProfile from './manager/ManagerProfile'
import Matches from './public/Matches'
import MatchDetail from './public/MatchDetail'
import MatchCreate from './manager/matchCreate/MatchCreate'
import { AuthProvider, useAuth } from './auth/AuthContext'
import Teams from './public/Teams'
import TeamStats from './manager/teamStats/TeamStats'
import { LanguageSwitcher } from './components/LanguageSwitcher'

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
          <div className="flex items-center justify-between mb-4">
            <SidebarTrigger className="size-4" />
            <LanguageSwitcher />
          </div>

          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<PublicRoute><SignUp /></PublicRoute>} />
            <Route path="/login" element={<PublicRoute><SignIn /></PublicRoute>} />
            <Route path="/teams" element={<Teams />} />
            <Route path="/teams/new" element={<ProtectedRoute><TeamCreate /></ProtectedRoute>} />
            <Route path="/teams/my" element={<ProtectedRoute><Teams mode="mine" /></ProtectedRoute>} />
            <Route path="/teams/:name/stats" element={<TeamStats />} />
            <Route path="/teams/:name" element={<TeamPage />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/matches" element={<Matches />} />
            <Route path="/matches/new" element={<ProtectedRoute><MatchCreate /></ProtectedRoute>} />
            <Route path="/matches/:id" element={<MatchDetail />} />
            <Route path="/profile" element={<ProtectedRoute><ManagerProfile /></ProtectedRoute>} />
          </Routes>

        </main>

      </SidebarProvider>
    </AuthProvider>
  )
}

export default App
