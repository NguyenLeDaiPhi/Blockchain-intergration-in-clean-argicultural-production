import { api } from '../api';
import { shipmentService } from '../shipment.service';

jest.mock('../api');

describe('shipmentService', () => {
  it('getAll() gọi đúng endpoint', async () => {
    (api.get as jest.Mock).mockResolvedValue([]);

    const res = await shipmentService.getAll();

    expect(api.get).toHaveBeenCalledWith('/driver/shipments');
    expect(res).toEqual([]);
  });

  it('confirmDelivered() gọi đúng endpoint', async () => {
    (api.post as jest.Mock).mockResolvedValue({ status: 'DELIVERED' });

    await shipmentService.confirmDelivered('SHP001');

    expect(api.post).toHaveBeenCalledWith(
      '/driver/shipments/SHP001/deliver'
    );
  });
});
