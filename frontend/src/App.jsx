import { Route, Routes } from 'react-router-dom'
import SignUp from './auth/login/SignUp'
import SignIn from './auth/register/SignIn'

function App() {

  return (
    <Routes>
      <Route path='/register' element={<SignIn/>}/>
      <Route path='/login' element={<SignUp/>}/>
    </Routes>
  )
}

export default App
