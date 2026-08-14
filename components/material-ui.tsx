import { View, StyleSheet, type TextStyle, type ViewStyle } from "react-native";
import {
  Button,
  Divider,
  SegmentedButtons,
  Surface,
  Switch,
  Text,
  TouchableRipple,
  useTheme,
} from "react-native-paper";

export function MaterialCard({ children, style }: { children: React.ReactNode; style?: ViewStyle }) {
  const { colors } = useTheme();
  return (
    <Surface elevation={1} style={[styles.card, { backgroundColor: colors.elevation.level1 }, style]}>
      {children}
    </Surface>
  );
}

export function MaterialSectionLabel({ children, style }: { children: string; style?: TextStyle }) {
  const { colors } = useTheme();
  return <Text variant="titleSmall" style={[styles.sectionLabel, { color: colors.primary }, style]}>{children}</Text>;
}

export function MaterialLargeTitle({ children, style }: { children: string; style?: TextStyle }) {
  const { colors } = useTheme();
  return <Text variant="headlineLarge" style={[styles.largeTitle, { color: colors.onBackground }, style]}>{children}</Text>;
}

export function MaterialListItem({
  title,
  subtitle,
  rightText,
  showArrow = true,
  onPress,
  leftIcon,
  rightElement,
}: {
  title: string;
  subtitle?: string;
  rightText?: string;
  showArrow?: boolean;
  onPress?: () => void;
  leftIcon?: React.ReactNode;
  rightElement?: React.ReactNode;
}) {
  const { colors } = useTheme();
  return (
    <TouchableRipple onPress={onPress} disabled={!onPress} borderless style={styles.listItem}>
      <View style={styles.listItemContent}>
        {leftIcon ? <View style={styles.listIcon}>{leftIcon}</View> : null}
        <View style={styles.listText}>
          <Text variant="bodyLarge" style={{ color: colors.onSurface }}>{title}</Text>
          {subtitle ? <Text variant="bodyMedium" style={{ color: colors.onSurfaceVariant }}>{subtitle}</Text> : null}
        </View>
        {rightText ? <Text variant="bodyMedium" style={{ color: colors.onSurfaceVariant }}>{rightText}</Text> : null}
        {rightElement}
        {showArrow && onPress ? <Text variant="titleLarge" style={{ color: colors.onSurfaceVariant }}>›</Text> : null}
      </View>
    </TouchableRipple>
  );
}

export function MaterialButton({
  title,
  onPress,
  variant = "primary",
  disabled = false,
  style,
  compact = false,
}: {
  title: string;
  onPress?: () => void;
  variant?: "primary" | "secondary" | "text";
  disabled?: boolean;
  style?: ViewStyle;
  compact?: boolean;
}) {
  const mode = variant === "primary" ? "contained" : variant === "secondary" ? "outlined" : "text";
  return <Button mode={mode} onPress={onPress} disabled={disabled} compact={compact} style={style} contentStyle={compact ? undefined : styles.buttonContent}>{title}</Button>;
}

export function MaterialSegmentedControl({
  options,
  selectedIndex,
  onSelect,
  style,
}: {
  options: string[];
  selectedIndex: number;
  onSelect: (index: number) => void;
  style?: ViewStyle;
}) {
  const value = String(selectedIndex);
  return <SegmentedButtons value={value} onValueChange={(next) => onSelect(Number(next))} buttons={options.map((label, index) => ({ value: String(index), label }))} style={style} />;
}

export function MaterialSwitch({ value, onValueChange }: { value: boolean; onValueChange: (value: boolean) => void }) {
  return <Switch value={value} onValueChange={onValueChange} />;
}

export function MaterialDivider({ style }: { style?: ViewStyle }) {
  return <Divider style={style} />;
}

const styles = StyleSheet.create({
  card: { borderRadius: 16, marginHorizontal: 16, marginVertical: 8, overflow: "hidden", padding: 16 },
  sectionLabel: { marginLeft: 20, marginTop: 24, marginBottom: 8, fontWeight: "700" },
  largeTitle: { marginLeft: 20, marginBottom: 12, fontWeight: "700" },
  listItem: { minHeight: 60 },
  listItemContent: { alignItems: "center", flexDirection: "row", gap: 12, minHeight: 60, paddingHorizontal: 16, paddingVertical: 10 },
  listIcon: { alignItems: "center", width: 28 },
  listText: { flex: 1, gap: 2 },
  buttonContent: { minHeight: 44 },
});
