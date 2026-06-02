import { Route, Routes } from 'react-router-dom'
import SignUp from './auth/register/SignUp'
import SignIn from './auth/login/SignIn'

function App() {

  return (
    <Routes>
      <Route path='/register' element={<SignUp/>}/>
      <Route path='/login' element={<SignIn/>}/>
    </Routes>
  )
}

export default App
