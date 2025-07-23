const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080/api';

let authToken: string | null = null;

const api = {
  setAuthToken: (token: string) => {
    authToken = token;
  },

  clearAuthToken: () => {
    authToken = null;
  },

  getHeaders: () => {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    
    // Use explicit authToken first, fallback to localStorage
    const token = authToken || localStorage.getItem('accessToken');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    return headers;
  },

  get: async (endpoint: string) => {
    const headers = api.getHeaders();
    console.log(`GET ${endpoint} - Headers:`, headers);
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers,
      credentials: 'include',
    });
    
    console.log(`GET ${endpoint} - Response status:`, response.status);
    
    if (response.status === 401) {
      throw new Error('Unauthorized');
    }
    
    // Handle empty response body
    const text = await response.text();
    const data = text ? JSON.parse(text) : {};
    
    console.log(`GET ${endpoint} - Response data:`, data);
    
    if (!response.ok) {
      throw { response: { data, status: response.status } };
    }
    
    return data;
  },

  post: async (endpoint: string, body: any) => {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'POST',
      headers: api.getHeaders(),
      body: JSON.stringify(body),
      credentials: 'include',
    });
    
    if (response.status === 401) {
      throw new Error('Unauthorized');
    }
    
    // Handle empty response body
    const text = await response.text();
    const data = text ? JSON.parse(text) : {};
    
    if (!response.ok) {
      throw { response: { data, status: response.status } };
    }
    
    return data;
  },

  put: async (endpoint: string, body: any) => {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'PUT',
      headers: api.getHeaders(),
      body: JSON.stringify(body),
      credentials: 'include',
    });
    
    if (response.status === 401) {
      throw new Error('Unauthorized');
    }
    
    // Handle empty response body
    const text = await response.text();
    const data = text ? JSON.parse(text) : {};
    
    if (!response.ok) {
      throw { response: { data, status: response.status } };
    }
    
    return data;
  },

  delete: async (endpoint: string) => {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'DELETE',
      headers: api.getHeaders(),
      credentials: 'include',
    });
    
    if (response.status === 401) {
      throw new Error('Unauthorized');
    }
    
    // Handle empty response body
    const text = await response.text();
    const data = text ? JSON.parse(text) : {};
    
    if (!response.ok) {
      throw { response: { data, status: response.status } };
    }
    
    return data;
  },

  upload: async (endpoint: string, formData: FormData) => {
    const headers: Record<string, string> = {};
    
    // Get token from localStorage or authToken
    const token = authToken || localStorage.getItem('accessToken');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'POST',
      headers,
      body: formData,
      credentials: 'include',
    });
    
    if (response.status === 401) {
      throw new Error('Unauthorized');
    }
    
    // Handle empty response body
    const text = await response.text();
    const data = text ? JSON.parse(text) : {};
    
    if (!response.ok) {
      throw { response: { data, status: response.status } };
    }
    
    return data;
  },

  health: () => api.get('/health'),
};

export default api;
export { api };