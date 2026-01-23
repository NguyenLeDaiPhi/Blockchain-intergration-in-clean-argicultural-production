import AsyncStorage from '@react-native-async-storage/async-storage';
import { api } from './api';

const TOKEN_KEY = 'ACCESS_TOKEN';

export const authService = {
  async login(username: string, password: string) {
    const res = await api.post<{ token: string }>('/auth/login', {
      username,
      password,
    });

    await AsyncStorage.setItem(TOKEN_KEY, res.token);
    return true;
  },

  async logout() {
    await AsyncStorage.removeItem(TOKEN_KEY);
  },

  async isAuthenticated() {
    const token = await AsyncStorage.getItem(TOKEN_KEY);
    return !!token;
  },
};
