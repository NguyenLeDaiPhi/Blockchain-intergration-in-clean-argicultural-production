import { authService } from '@/services/auth.service';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

export default function LoginScreen() {
  const router = useRouter();
  const [username, setUsername] = useState('driver');
  const [password, setPassword] = useState('123456');

  const login = async () => {
    const ok = await authService.login(username, password);
    if (ok) router.replace('/(driver)/shipments');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Driver Login</Text>

      <TextInput value={username} onChangeText={setUsername} style={styles.input} />
      <TextInput value={password} onChangeText={setPassword} secureTextEntry style={styles.input} />

      <Pressable style={styles.btn} onPress={login}>
        <Text style={styles.btnText}>Login</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20 },
  title: { fontSize: 22, textAlign: 'center', marginBottom: 20 },
  input: { borderWidth: 1, padding: 12, borderRadius: 8, marginBottom: 12 },
  btn: { backgroundColor: '#2ecc71', padding: 14, borderRadius: 8 },
  btnText: { color: '#fff', textAlign: 'center', fontWeight: '700' },
});
