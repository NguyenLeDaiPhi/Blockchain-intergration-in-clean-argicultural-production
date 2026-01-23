import { shipmentService } from '@/services/shipment.service';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { Pressable, Text, View } from 'react-native';

export default function ShipmentDetail() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [shipment, setShipment] = useState<any>();
  const router = useRouter();

  useEffect(() => {
    shipmentService.getById(id!).then(setShipment);
  }, [id]);

  if (!shipment) return null;

  return (
    <View style={{ padding: 16 }}>
      <Text>ID: {shipment.id}</Text>
      <Text>Status: {shipment.status}</Text>

      <Pressable onPress={() => router.push('/(driver)/scan')}>
        <Text style={{ marginTop: 20, color: 'blue' }}>Scan QR</Text>
      </Pressable>

      <Pressable onPress={() => shipmentService.confirmDeliver(shipment.id)}>
        <Text style={{ marginTop: 20, color: 'green' }}>Confirm Delivered</Text>
      </Pressable>
    </View>
  );
}
