import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { SafeAreaProvider, initialWindowMetrics } from "react-native-safe-area-context";

import { I18nProvider } from "@/lib/i18n";
import { ThemeProvider } from "@/lib/theme-provider";

export const unstable_settings = {
  anchor: "(tabs)",
};

const providerInitialMetrics = initialWindowMetrics
  ? {
      ...initialWindowMetrics,
      insets: {
        ...initialWindowMetrics.insets,
        top: Math.max(initialWindowMetrics.insets.top, 16),
        bottom: Math.max(initialWindowMetrics.insets.bottom, 12),
      },
    }
  : undefined;

export default function RootLayout() {
  return (
    <ThemeProvider>
      <SafeAreaProvider initialMetrics={providerInitialMetrics}>
        <GestureHandlerRootView style={{ flex: 1 }}>
          <I18nProvider>
            <Stack screenOptions={{ headerShown: false }}>
              <Stack.Screen name="(tabs)" />
            </Stack>
            <StatusBar style="auto" />
          </I18nProvider>
        </GestureHandlerRootView>
      </SafeAreaProvider>
    </ThemeProvider>
  );
}
