import { api } from './api';

export const shipmentService = {
  getAll() {
    return api.get('/driver/shipments');
  },

  getById(id: string) {
    return api.get(`/driver/shipments/${id}`);
  },

  scan(id: string, qrData: string) {
    return api.post(`/driver/shipments/${id}/scan`, { qrData });
  },

  confirmReceive(id: string) {
    return api.post(`/driver/shipments/${id}/receive`);
  },

  confirmDeliver(id: string) {
    return api.post(`/driver/shipments/${id}/deliver`);
  },
};

export const shipmentService = {
  getAll() {
    return api.get('/driver/shipments');
  },

  getById(id: string) {
    return api.get(`/driver/shipments/${id}`);
  },

  scan(id: string, qrData: string) {
    return api.post(`/driver/shipments/${id}/scan`, { qrData });
  },

  confirmReceive(id: string) {
    return api.post(`/driver/shipments/${id}/receive`);
  },

  confirmDeliver(id: string) {
    return api.post(`/driver/shipments/${id}/deliver`);
  },
};
