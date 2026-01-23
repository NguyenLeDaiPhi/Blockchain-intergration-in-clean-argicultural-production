import { authService } from '@/services/auth.service';
import { Slot, useRouter, useSegments } from 'expo-router';
import { useEffect } from 'react';

export default function RootLayout() {
  const router = useRouter();
  const segments = useSegments();

  useEffect(() => {
    const check = async () => {
      const loggedIn = await authService.isAuthenticated();
      const inAuth = segments[0] === '(auth)';

      if (!loggedIn && !inAuth) {
        router.replace('/(auth)/login');
      }

      if (loggedIn && inAuth) {
        router.replace('/(driver)/shipments');
      }
    };

    check();
  }, [segments]);

  return <Slot />;
}
