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
  { title: "Sing Up", url: "/register", icon: UserPlus },
]

export function AppSidebar() {
    return (
        <Sidebar>
            <SidebarContent>
            <SidebarGroup>
            <SidebarGroupLabel>
                <Link to={'/'}>
                    Home
                </Link>
            </SidebarGroupLabel>
            <SidebarGroupContent>
                <SidebarMenu class='flex-row'>
                {items.map((item) => (
                    <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton asChild>
                        <Link to={item.url}>
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