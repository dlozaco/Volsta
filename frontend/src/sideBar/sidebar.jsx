import { Link } from "react-router-dom"
import { LogIn, UserPlus, LogOut, User } from "lucide-react"
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

const authItems = [
  { title: "Login", url: "/login", icon: LogIn },
  { title: "Sign Up", url: "/register", icon: UserPlus },
]

export function AppSidebar() {
  const { user, logout } = useAuth()

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel className="text-lg font-bold mb-4 mt-2">
            <Link to={"/"}>Home</Link>
          </SidebarGroupLabel>
          <SidebarGroupContent>
            {user ? (
              <div className="px-2">
                <div className="flex items-center gap-2 px-2 py-1.5 mb-2">
                  <User className="size-4" />
                  <span className="text-sm font-medium truncate">
                    {user.username}
                  </span>
                </div>
                <Button
                  variant="ghost"
                  className="w-full justify-start gap-2"
                  onClick={logout}
                >
                  <LogOut className="size-4" />
                  <span>Logout</span>
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
