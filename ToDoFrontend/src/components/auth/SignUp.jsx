import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from 'axios';

export default function Signup({ onToggle }) {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await axios.post('http://localhost:4040/api/auth/register', formData);
      
      if (response.data.status === 201) {
        // Registration successful
        onToggle(); // Switch to login form
      } else {
        setError(response.data.message || 'Registration failed');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'An error occurred during registration');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="wrapper signUp">
      <div className="illustration">
        <img 
          src="https://images.unsplash.com/photo-1551434678-e076c223a692?q=80&w=1000" 
          alt="illustration" 
        />
      </div>
      <div className="form">
        <div className="heading">Create Account</div>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              placeholder="Choose a username"
              value={formData.username}
              onChange={handleChange}
              required
              minLength={3}
            />
          </div>
          <div>
            <label htmlFor="email">Email Address</label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="Enter your email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="Create a password"
              value={formData.password}
              onChange={handleChange}
              required
              minLength={6}
            />
          </div>
          <button type="submit" disabled={loading}>
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
          <h2 className="or">OR</h2>
        </form>
        <p className="auth-switch">
          Already have an account? <span onClick={onToggle} className="auth-link">Sign In</span>
        </p>
      </div>
    </div>
  );
}
