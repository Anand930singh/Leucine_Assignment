import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { setToken, setUserData } from '../../utils/auth';

export default function Login({ onToggle }) {
	const navigate = useNavigate();
	const [formData, setFormData] = useState({
		userNameOrEmail: '',
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
			const response = await axios.post('http://localhost:4040/api/auth/login', formData);
			console.log('Login response:', response.data);
			
			if (response.data.status === 200) {
				// Store token
				setToken(response.data.data.token);
				console.log('Token stored:', response.data.data.token);
				
				// Store user data
				const userData = {
					username: formData.userNameOrEmail,
					email: formData.userNameOrEmail.includes('@') ? formData.userNameOrEmail : null
				};
				setUserData(userData);
				console.log('User data stored:', userData);
				
				// Navigate to home
				console.log('Navigating to home...');
				navigate('/home', { replace: true });
			} else {
				setError(response.data.message || 'Login failed');
			}
		} catch (err) {
			console.error('Login error:', err);
			setError(err.response?.data?.message || 'An error occurred during login');
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="wrapper login">
			<div className="illustration">
				<img 
					src="https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1000" 
					alt="illustration" 
				/>
			</div>
			<div className="form">
				<div className="heading">Welcome Back</div>
				{error && <div className="error-message">{error}</div>}
				<form onSubmit={handleSubmit}>
					<div>
						<label htmlFor="userNameOrEmail">Username or Email</label>
						<input
							type="text"
							id="userNameOrEmail"
							name="userNameOrEmail"
							placeholder="Enter your username or email"
							value={formData.userNameOrEmail}
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
							placeholder="Enter your password"
							value={formData.password}
							onChange={handleChange}
							required
						/>
					</div>
					<button type="submit" disabled={loading}>
						{loading ? 'Signing In...' : 'Sign In'}
					</button>
					<h2 className="or">OR</h2>
				</form>
				<p className="auth-switch">
					Don't have an account? <span onClick={onToggle} className="auth-link">Sign Up</span>
				</p>
			</div>
		</div>
	);
}
