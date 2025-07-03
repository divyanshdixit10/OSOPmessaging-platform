import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  headers: {
    'Accept': 'application/json',
  },
  withCredentials: false // Disable sending cookies
});

// Add request interceptor for debugging
api.interceptors.request.use(
  (config) => {
    // Don't set Content-Type for FormData - axios will set it automatically with boundary
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    
    console.log('API Request:', {
      url: config.url,
      method: config.method,
      data: config.data instanceof FormData 
        ? 'FormData (see browser network tab for details)'
        : config.data,
      headers: config.headers
    });
    return config;
  },
  (error) => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for debugging
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.data);
    return response;
  },
  (error) => {
    console.error('Response Error:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      config: error.config
    });
    
    // Customize error message based on the error type
    if (error.response) {
      // Server responded with a status code outside of 2xx range
      const errorMessage = error.response.data?.message || error.response.data || 'Server error occurred';
      throw new Error(`Failed to send message: ${errorMessage}`);
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