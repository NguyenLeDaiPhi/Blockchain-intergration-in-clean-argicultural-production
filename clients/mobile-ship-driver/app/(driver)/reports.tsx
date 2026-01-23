import { reportService } from '@/services/report.service';
import { useState } from 'react';
import { Pressable, Text, TextInput, View } from 'react-native';

export default function ReportsScreen() {
  const [content, setContent] = useState('');

  return (
    <View style={{ padding: 16 }}>
      <TextInput
        placeholder="Report content"
        value={content}
        onChangeText={setContent}
        style={{ borderWidth: 1, padding: 12, marginBottom: 12 }}
      />

      <Pressable onPress={() => reportService.send(content)}>
        <Text style={{ color: 'red' }}>Send Report</Text>
      </Pressable>
    </View>
  );
}
