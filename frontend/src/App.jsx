import { Route, Routes } from 'react-router-dom'
import SignUp from './auth/register/SignUp'
import SignIn from './auth/login/SignIn'
import { SidebarProvider, SidebarTrigger } from '#components/ui/sidebar'
import { AppSidebar } from './sideBar/sidebar'
import Home from './homepage/Home'

function App() {

  return (
    <SidebarProvider defaultOpen={false}>
      <AppSidebar />

      <main className="flex-1 w-full p-4 overflow-x-hidden">

      <SidebarTrigger className="mb-4 size-4"/>

        <Routes>
          <Route path='/' element={<Home/>}/>
          <Route path='/register' element={<SignUp/>}/>
          <Route path='/login' element={<SignIn/>}/>
        </Routes>

      </main>

    </SidebarProvider>

  )
}

export default App
