import { Tabs } from "expo-router";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { Platform } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useTheme } from "react-native-paper";

export default function TabLayout() {
  const { colors } = useTheme();
  const insets = useSafeAreaInsets();
  const bottomPadding = Platform.OS === "web" ? 12 : Math.max(insets.bottom, 8);

  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: colors.onSecondaryContainer,
        tabBarInactiveTintColor: colors.onSurfaceVariant,
        tabBarActiveBackgroundColor: colors.secondaryContainer,
        tabBarStyle: {
          height: 72 + bottomPadding,
          paddingTop: 8,
          paddingBottom: bottomPadding,
          paddingHorizontal: 12,
          backgroundColor: colors.surface,
          borderTopColor: colors.outlineVariant,
          borderTopWidth: 1,
          elevation: 0,
        },
        tabBarItemStyle: { borderRadius: 18, marginHorizontal: 3 },
        tabBarLabelStyle: { fontSize: 12, fontWeight: "600", marginTop: 1 },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: "Canvas",
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons name="brush-variant" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="characters"
        options={{
          title: "Characters",
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons name="format-letter-case" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="settings"
        options={{
          title: "Settings",
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons name="cog-outline" size={size} color={color} />
          ),
        }}
      />
    </Tabs>
  );
}
