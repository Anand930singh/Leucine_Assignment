import React, { useState } from 'react'
import '../style/auth.css'
import Signup from '../components/auth/SignUp'
import Login from '../components/auth/Login'

function Authentication() {
  const [showLogin, setShowLogin] = useState(true);

  const toggleAuth = () => {
    setShowLogin(!showLogin);
  };

  return (
    <div className="auth-page">
      {showLogin ? (
        <Login onToggle={toggleAuth} />
      ) : (
        <Signup onToggle={toggleAuth} />
      )}
    </div>
  )
}

export default Authentication