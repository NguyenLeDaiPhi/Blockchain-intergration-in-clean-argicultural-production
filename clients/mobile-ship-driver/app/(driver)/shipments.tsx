import { shipmentService } from '@/services/shipment.service';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { FlatList, Pressable, Text, View } from 'react-native';

export default function ShipmentsScreen() {
  const [data, setData] = useState<any[]>([]);
  const router = useRouter();

  useEffect(() => {
    shipmentService.getAll().then(setData);
  }, []);

  return (
    <FlatList
      data={data}
      keyExtractor={(i) => i.id}
      renderItem={({ item }) => (
        <Pressable onPress={() => router.push(`/(driver)/shipments/${item.id}`)}>
          <View style={{ padding: 16, borderBottomWidth: 1 }}>
            <Text>{item.id}</Text>
            <Text>Status: {item.status}</Text>
          </View>
        </Pressable>
      )}
    />
  );
}
