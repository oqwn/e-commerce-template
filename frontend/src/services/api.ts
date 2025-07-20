const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080/api';

let authToken: string | null = null;

export const api = {
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
    
    if (authToken) {
      headers['Authorization'] = `Bearer ${authToken}`;
    }
    
    return headers;
  },

  get: async (endpoint: string) => {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: api.getHeaders(),
    });
    
    if (response.status === 401) {
      // Token might be expired, let the auth context handle it
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