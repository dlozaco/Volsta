import { Link } from "react-router-dom"
import { LogIn, UserPlus, LogOut, User, LayoutDashboard, CalendarDays, Shield } from "lucide-react"
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"
import { useAuth } from "../auth/AuthContext"
import { Button } from "@/components/ui/button"
import { useTranslation } from "react-i18next"

const authItems = [
  { title: "login", url: "/login", icon: LogIn },
  { title: "signUp", url: "/register", icon: UserPlus },
]

const navItems = [
  { title: "dashboard", url: "/dashboard", icon: LayoutDashboard },
  { title: "teams", url: "/teams", icon: Shield },
  { title: "matches", url: "/matches", icon: CalendarDays },
]

export function AppSidebar() {
  const { user, logout } = useAuth()
  const { t } = useTranslation('sidebar')

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel className="text-lg font-bold mb-4 mt-2">
            <Link to={"/"}>{t('home')}</Link>
          </SidebarGroupLabel>
          <SidebarGroupContent>
            {user ? (
              <div className="px-2">
                <div className="flex items-center px-2 py-1.5 mb-2">
                  <Button variant="ghost" className="w-full justify-start gap-2" asChild>
                    <Link to="/profile" className="flex items-center gap-2">
                      <User className="size-4" />
                      <span className="text-sm font-medium truncate">
                        {user.username}
                      </span>
                    </Link>
                  </Button>
                </div>
                <SidebarMenu className="flex-col gap-2">
                  {navItems.map((item) => (
                    <SidebarMenuItem key={item.title}>
                      <SidebarMenuButton asChild className="h-7 text-lg">
                        <Link to={item.url} className="flex items-center gap-2">
                          <item.icon />
                          <span>{t(item.title)}</span>
                        </Link>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  ))}
                </SidebarMenu>
                <Button
                  variant="ghost"
                  className="w-full justify-start gap-2"
                  onClick={logout}
                >
                  <LogOut className="size-4" />
                  <span>{t('logout')}</span>
                </Button>
              </div>
            ) : (
              <SidebarMenu className="flex-col gap-2">
                {authItems.map((item) => (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton asChild className="h-7 text-lg">
                      <Link to={item.url} className="flex items-center gap-2">
                        <item.icon />
                        <span>{item.title}</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                ))}
              </SidebarMenu>
            )}
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  )
}
