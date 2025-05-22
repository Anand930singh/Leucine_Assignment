import axios from 'axios';

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'user_data';

export const setToken = (token) => {
  try {
    localStorage.setItem(TOKEN_KEY, token);
    ('Token stored successfully:', token);
  } catch (err) {
    console.error('Error storing token:', err);
  }
};

export const getToken = () => {
  try {
    const token = localStorage.getItem(TOKEN_KEY);
    return token;
  } catch (err) {
    console.error('Error getting token:', err);
    return null;
  }
};

export const removeToken = () => {
  try {
    localStorage.removeItem(TOKEN_KEY);
  } catch (err) {
    console.error('Error removing token:', err);
  }
};

export const setUserData = (userData) => {
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(userData));
  } catch (err) {
    console.error('Error storing user data:', err);
  }
};

export const getUserData = () => {
  try {
    const userData = localStorage.getItem(USER_KEY);
    const parsedData = userData ? JSON.parse(userData) : null;
    return parsedData;
  } catch (err) {
    console.error('Error getting user data:', err);
    return null;
  }
};

export const removeUserData = () => {
  try {
    localStorage.removeItem(USER_KEY);
  } catch (err) {
    console.error('Error removing user data:', err);
  }
};

export const isAuthenticated = () => {
  const token = getToken();
  const isAuth = !!token;
  return isAuth;
};

export const logout = () => {
  removeToken();
  removeUserData();
};

export const createAuthAxios = () => {
  const instance = axios.create({
    baseURL: 'https://leucine-assignment-elqt.onrender.com/api',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  });

  instance.interceptors.request.use(
    (config) => {
      const token = getToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        
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

  instance.interceptors.response.use(
    (response) => {
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