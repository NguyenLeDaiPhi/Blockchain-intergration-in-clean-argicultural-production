import { api } from './api';

export type DriverProfile = {
  id: string;
  name: string;
  phone: string;
  vehicle: string;
  licensePlate: string;
};

export const profileService = {
  // GET /api/driver/profile
  get(): Promise<DriverProfile> {
    return api.get('/driver/profile');
  },
};
