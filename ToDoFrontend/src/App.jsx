import { useState } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Authentication from './page/Authentication'
import Home from './page/Home'
import ProtectedRoute from './components/ProtectedRoute'

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Authentication/>} />
        <Route 
          path="/home" 
          element={
            <ProtectedRoute>
              <Home/>
            </ProtectedRoute>
          } 
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App
