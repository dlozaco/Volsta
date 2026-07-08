import { Link } from "react-router-dom"
import { LogIn, UserPlus } from "lucide-react"
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

// Tus dos únicas rutas
const items = [
  { title: "Login", url: "/login", icon: LogIn },
  { title: "Sign Up", url: "/register", icon: UserPlus },
]

export function AppSidebar() {
    return (
        <Sidebar>
            <SidebarContent>
            <SidebarGroup>
            <SidebarGroupLabel className="text-lg font-bold mb-4 mt-2">
                <Link to={'/'}>
                    Home
                </Link>
            </SidebarGroupLabel>
            <SidebarGroupContent>
                <SidebarMenu className="flex-col gap-2">
                {items.map((item) => (
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
            </SidebarGroupContent>
            </SidebarGroup>
        </SidebarContent>
        </Sidebar>
    )
}