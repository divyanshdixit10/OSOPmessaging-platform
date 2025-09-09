import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
  },
  withCredentials: false // Disable sending cookies
});

// Function to get token from localStorage
const getAuthToken = () => {
  return localStorage.getItem('token');
};

// Add request interceptor for authentication and debugging
api.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = getAuthToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add tenant ID header if available
    const store = JSON.parse(localStorage.getItem('osop-messaging-store') || '{}');
    if (store.state?.currentTenant?.id) {
      config.headers['X-Tenant-ID'] = store.state.currentTenant.id.toString();
    }
    
    // Don't set Content-Type for FormData - axios will set it automatically with boundary
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    
    // Debug logging in development
    if (process.env.NODE_ENV === 'development') {
      console.log('API Request:', {
        url: config.url,
        method: config.method,
        data: config.data instanceof FormData 
          ? 'FormData (see browser network tab for details)'
          : config.data,
        headers: config.headers
      });
    }
    
    return config;
  },
  (error) => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for debugging and auth handling
api.interceptors.response.use(
  (response) => {
    // Debug logging in development
    if (process.env.NODE_ENV === 'development') {
      console.log('API Response:', response.data);
    }
    return response;
  },
  (error) => {
    // Debug logging in development
    if (process.env.NODE_ENV === 'development') {
      console.error('Response Error:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status,
        config: error.config
      });
    }
    
    // Handle authentication errors
    if (error.response?.status === 401) {
      // Token expired or invalid - clear auth data and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('osop-messaging-store');
      
      // Only redirect if not already on login page to avoid redirect loops
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    
    // Customize error message based on the error type
    if (error.response) {
      // Server responded with a status code outside of 2xx range
      const errorMessage = error.response.data?.message || error.response.data || 'Server error occurred';
      throw new Error(errorMessage);
    } else if (error.request) {
      // Request was made but no response received
      throw new Error('No response received from server. Please check if the backend server is running.');
    } else {
      // Error in request configuration
      throw new Error('Error in request configuration: ' + error.message);
    }
  }
);

export default api; 