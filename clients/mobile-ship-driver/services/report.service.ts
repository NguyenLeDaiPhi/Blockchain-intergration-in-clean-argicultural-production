import { api } from './api';

export const reportService = {
  send(content: string) {
    return api.post('/driver/reports', { content });
  },
};
