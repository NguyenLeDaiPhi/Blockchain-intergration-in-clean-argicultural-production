import { authService } from '@/services/auth.service';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import {
    Alert,
    Pressable,
    StyleSheet,
    Text,
    TextInput,
    View,
} from 'react-native';

export default function LoginScreen() {
  const router = useRouter();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const onLogin = async () => {
    if (!username || !password) {
      Alert.alert('Lỗi', 'Vui lòng nhập username và password');
      return;
    }

    try {
      setLoading(true);

      // ✅ LOGIN MOCK – KHÔNG GỌI API
      await authService.login(username, password);

      // ✅ ĐI VÀO DRIVER FLOW
      router.replace('/shipments');
    } catch (err: any) {
      Alert.alert('Login failed', err.message || 'Đăng nhập thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Ship Driver Login</Text>

      <TextInput
        placeholder="Username"
        value={username}
        onChangeText={setUsername}
        autoCapitalize="none"
        style={styles.input}
      />

      <TextInput
        placeholder="Password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
        style={styles.input}
      />

      <Pressable
        onPress={onLogin}
        disabled={loading}
        style={({ pressed }) => [
          styles.button,
          pressed && { opacity: 0.8 },
        ]}
      >
        <Text style={styles.buttonText}>
          {loading ? 'LOGGING IN...' : 'LOGIN'}
        </Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: 'center',
  },
  title: {
    fontSize: 26,
    fontWeight: 'bold',
    marginBottom: 24,
    textAlign: 'center',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 10,
    padding: 14,
    marginBottom: 14,
    fontSize: 16,
  },
  button: {
    marginTop: 8,
    padding: 16,
    backgroundColor: '#1677ff',
    borderRadius: 10,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
    textAlign: 'center',
  },
});
