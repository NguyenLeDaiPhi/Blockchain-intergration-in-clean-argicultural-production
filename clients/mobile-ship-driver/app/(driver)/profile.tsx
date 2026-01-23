import { authService } from '@/services/auth.service';
import { useRouter } from 'expo-router';
import { Pressable, Text, View } from 'react-native';

export default function ProfileScreen() {
  const router = useRouter();

  return (
    <View style={{ padding: 16 }}>
      <Text style={{ fontSize: 20, fontWeight: 'bold' }}>
        Driver Profile
      </Text>

      <Text style={{ marginTop: 8 }}>Username: driver</Text>

      <Pressable
        onPress={() => {
          authService.logout();
          router.replace('/login');
        }}
        style={{
          marginTop: 16,
          backgroundColor: 'red',
          padding: 14,
          borderRadius: 8,
        }}
      >
        <Text style={{ color: '#fff', textAlign: 'center' }}>
          LOGOUT
        </Text>
      </Pressable>
    </View>
  );
}
