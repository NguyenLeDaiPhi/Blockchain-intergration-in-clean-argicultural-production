import { CameraView } from 'expo-camera';
import { useRouter } from 'expo-router';

export default function ScanScreen() {
  const router = useRouter();

  return (
    <CameraView
      style={{ flex: 1 }}
      barcodeScannerSettings={{ barcodeTypes: ['qr'] }}
      onBarcodeScanned={(res) => {
        console.log('QR:', res.data);
        router.back();
      }}
    />
  );
}
