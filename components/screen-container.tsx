import { View, type ViewProps } from "react-native";
import { SafeAreaView, type Edge } from "react-native-safe-area-context";
import { useTheme } from "react-native-paper";

export interface ScreenContainerProps extends ViewProps {
  edges?: Edge[];
}

export function ScreenContainer({
  children,
  edges = ["top", "left", "right"],
  style,
  ...props
}: ScreenContainerProps) {
  const { colors } = useTheme();

  return (
    <View style={[{ flex: 1, backgroundColor: colors.background }, style]} {...props}>
      <SafeAreaView edges={edges} style={{ flex: 1 }}>
        {children}
      </SafeAreaView>
    </View>
  );
}
