import { api } from './api';

export const scanService = {
  // POST /api/driver/scan
  scanQRCode(qrCode: string): Promise<{
    shipmentId: string;
    action: 'RECEIVE' | 'DELIVER';
  }> {
    return api.post('/driver/scan', { qrCode });
  },
};
