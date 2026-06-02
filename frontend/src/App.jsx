import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'
import { Route, Routes } from 'react-router-dom'
import SignIn from './auth/register/SignIn'

function App() {

  return (
    <Routes>
      <Route path='/register' element={<SignIn/>}/>
    </Routes>
  )
}

export default App
