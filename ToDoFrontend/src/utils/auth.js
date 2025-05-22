import axios from 'axios';

// Token storage keys
const TOKEN_KEY = 'auth_token';
const USER_KEY = 'user_data';

// Save token to localStorage
export const setToken = (token) => {
  try {
    localStorage.setItem(TOKEN_KEY, token);
    console.log('Token stored successfully:', token);
  } catch (err) {
    console.error('Error storing token:', err);
  }
};

// Get token from localStorage
export const getToken = () => {
  try {
    const token = localStorage.getItem(TOKEN_KEY);
    console.log('Token retrieved:', token ? 'exists' : 'not found');
    return token;
  } catch (err) {
    console.error('Error getting token:', err);
    return null;
  }
};

// Remove token from localStorage
export const removeToken = () => {
  try {
    localStorage.removeItem(TOKEN_KEY);
    console.log('Token removed successfully');
  } catch (err) {
    console.error('Error removing token:', err);
  }
};

// Save user data
export const setUserData = (userData) => {
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(userData));
    console.log('User data stored successfully:', userData);
  } catch (err) {
    console.error('Error storing user data:', err);
  }
};

// Get user data
export const getUserData = () => {
  try {
    const userData = localStorage.getItem(USER_KEY);
    const parsedData = userData ? JSON.parse(userData) : null;
    console.log('User data retrieved:', parsedData);
    return parsedData;
  } catch (err) {
    console.error('Error getting user data:', err);
    return null;
  }
};

// Remove user data
export const removeUserData = () => {
  try {
    localStorage.removeItem(USER_KEY);
    console.log('User data removed successfully');
  } catch (err) {
    console.error('Error removing user data:', err);
  }
};

// Check if user is authenticated
export const isAuthenticated = () => {
  const token = getToken();
  const isAuth = !!token;
  console.log('Authentication status:', isAuth);
  return isAuth;
};

// Logout user
export const logout = () => {
  removeToken();
  removeUserData();
  console.log('User logged out successfully');
};

// Create axios instance with auth header
export const createAuthAxios = () => {
  const instance = axios.create({
    baseURL: 'http://localhost:4040/api',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  });

  // Add token to requests
  instance.interceptors.request.use(
    (config) => {
      const token = getToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log('Request config:', {
          url: config.url,
          method: config.method,
          headers: config.headers,
          data: config.data
        });
      } else {
        console.warn('No token found for request');
      }
      return config;
    },
    (error) => {
      console.error('Request interceptor error:', error);
      return Promise.reject(error);
    }
  );

  // Handle token expiration
  instance.interceptors.response.use(
    (response) => {
      console.log('Response received:', {
        status: response.status,
        statusText: response.statusText,
        headers: response.headers,
        data: response.data
      });
      return response;
    },
    (error) => {
      console.error('Response error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        headers: error.response?.headers
      });
      if (error.response?.status === 401) {
        console.log('Unauthorized, logging out...');
        logout();
        window.location.href = '/';
      }
      return Promise.reject(error);
    }
  );

  return instance;
}; 