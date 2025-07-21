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
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: api.getHeaders(),
      credentials: 'include',
    });
    
    if (response.status === 401) {
      throw new Error('Unauthorized');
    }
    
    const data = await response.json();
    
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
    
    const data = await response.json();
    
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
    
    const data = await response.json();
    
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
    
    const data = await response.json();
    
    if (!response.ok) {
      throw { response: { data, status: response.status } };
    }
    
    return data;
  },

  health: () => api.get('/health'),
};

export default api;
export { api };