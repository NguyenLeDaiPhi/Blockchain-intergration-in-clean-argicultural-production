import AsyncStorage from '@react-native-async-storage/async-storage';

const BASE_URL = 'http://localhost:8000/api'; 
// 👉 sau đổi thành KONG thật (VD: http://kong:8000/api)

async function request(
  method: string,
  url: string,
  body?: any
) {
  const token = await AsyncStorage.getItem('ACCESS_TOKEN');

  const res = await fetch(`${BASE_URL}${url}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || 'API Error');
  }

  return res.json();
}

export const api = {
  get: (url: string) => request('GET', url),
  post: (url: string, body?: any) => request('POST', url, body),
};
