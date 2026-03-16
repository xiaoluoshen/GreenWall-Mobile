import React from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  type ViewStyle,
  type TextStyle,
} from "react-native";
import { useColors } from "@/hooks/use-colors";

// MIUIX Card - 16dp corner radius, surface background
export function MCard({
  children,
  style,
  className,
}: {
  children: React.ReactNode;
  style?: ViewStyle;
  className?: string;
}) {
  const colors = useColors();
  return (
    <View
      className={className}
      style={[
        {
          backgroundColor: colors.surface,
          borderRadius: 16,
          padding: 16,
          marginHorizontal: 16,
          marginVertical: 8,
        },
        style,
      ]}
    >
      {children}
    </View>
  );
}

// MIUIX Section Label - small colored text
export function MSectionLabel({
  children,
  style,
}: {
  children: string;
  style?: TextStyle;
}) {
  const colors = useColors();
  return (
    <Text
      style={[
        {
          fontSize: 14,
          fontWeight: "600",
          color: colors.muted,
          marginLeft: 32,
          marginTop: 24,
          marginBottom: 8,
          textTransform: "uppercase",
          letterSpacing: 0.8,
        },
        style,
      ]}
    >
      {children}
    </Text>
  );
}

// MIUIX Large Title
export function MLargeTitle({
  children,
  style,
}: {
  children: string;
  style?: TextStyle;
}) {
  const colors = useColors();
  return (
    <Text
      style={[
        {
          fontSize: 32,
          fontWeight: "700",
          color: colors.foreground,
          marginLeft: 20,
          marginBottom: 12,
        },
        style,
      ]}
    >
      {children}
    </Text>
  );
}

// MIUIX List Item with arrow
export function MListItem({
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
  const colors = useColors();
  return (
    <TouchableOpacity
      onPress={onPress}
      activeOpacity={0.6}
      style={styles.listItem}
      disabled={!onPress}
    >
      {leftIcon && <View style={styles.listItemIcon}>{leftIcon}</View>}
      <View style={{ flex: 1 }}>
        <Text style={[styles.listItemTitle, { color: colors.foreground }]}>
          {title}
        </Text>
        {subtitle && (
          <Text style={[styles.listItemSubtitle, { color: colors.muted }]}>
            {subtitle}
          </Text>
        )}
      </View>
      {rightText && (
        <Text style={[styles.listItemRight, { color: colors.muted }]}>
          {rightText}
        </Text>
      )}
      {rightElement}
      {showArrow && onPress && (
        <Text style={[styles.listItemArrow, { color: colors.muted }]}>
          {">"}
        </Text>
      )}
    </TouchableOpacity>
  );
}

// MIUIX Button - 20dp corner radius
export function MButton({
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
  const colors = useColors();
  const bgColor =
    variant === "primary"
      ? colors.primary
      : variant === "secondary"
        ? colors.surface
        : "transparent";
  const textColor =
    variant === "primary"
      ? "#ffffff"
      : colors.foreground;

  return (
    <TouchableOpacity
      onPress={onPress}
      activeOpacity={0.7}
      disabled={disabled}
      style={[
        {
          backgroundColor: bgColor,
          borderRadius: 20,
          paddingHorizontal: compact ? 16 : 24,
          paddingVertical: compact ? 8 : 12,
          alignItems: "center",
          justifyContent: "center",
          opacity: disabled ? 0.5 : 1,
        },
        variant === "secondary" && {
          borderWidth: 1,
          borderColor: colors.border,
        },
        style,
      ]}
    >
      <Text
        style={{
          fontSize: compact ? 15 : 17,
          fontWeight: "600",
          color: textColor,
        }}
      >
        {title}
      </Text>
    </TouchableOpacity>
  );
}

// MIUIX Segmented Control (pill-shaped tabs)
export function MSegmentedControl({
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
  const colors = useColors();
  return (
    <View
      style={[
        {
          flexDirection: "row",
          backgroundColor: colors.background,
          borderRadius: 12,
          padding: 3,
        },
        style,
      ]}
    >
      {options.map((option, index) => (
        <TouchableOpacity
          key={option}
          onPress={() => onSelect(index)}
          activeOpacity={0.7}
          style={{
            flex: 1,
            paddingVertical: 10,
            paddingHorizontal: 14,
            borderRadius: 10,
            alignItems: "center",
            backgroundColor:
              index === selectedIndex ? colors.surface : "transparent",
          }}
        >
          <Text
            style={{
              fontSize: 15,
              fontWeight: index === selectedIndex ? "600" : "500",
              color:
                index === selectedIndex ? colors.foreground : colors.muted,
            }}
          >
            {option}
          </Text>
        </TouchableOpacity>
      ))}
    </View>
  );
}

// MIUIX Switch
export function MSwitch({
  value,
  onValueChange,
}: {
  value: boolean;
  onValueChange: (val: boolean) => void;
}) {
  const colors = useColors();
  return (
    <TouchableOpacity
      onPress={() => onValueChange(!value)}
      activeOpacity={0.8}
      style={[
        styles.switchTrack,
        {
          backgroundColor: value ? colors.primary : colors.border,
        },
      ]}
    >
      <View
        style={[
          styles.switchThumb,
          {
            backgroundColor: "#ffffff",
            transform: [{ translateX: value ? 20 : 2 }],
          },
        ]}
      />
    </TouchableOpacity>
  );
}

// MIUIX Divider
export function MDivider({ style }: { style?: ViewStyle }) {
  const colors = useColors();
  return (
    <View
      style={[
        {
          height: StyleSheet.hairlineWidth,
          backgroundColor: colors.border,
          marginHorizontal: 16,
        },
        style,
      ]}
    />
  );
}

const styles = StyleSheet.create({
  listItem: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 16,
    paddingHorizontal: 16,
    minHeight: 52,
  },
  listItemIcon: {
    marginRight: 14,
    width: 24,
    alignItems: "center",
  },
  listItemTitle: {
    fontSize: 17,
    fontWeight: "500",
  },
  listItemSubtitle: {
    fontSize: 14,
    marginTop: 4,
    fontWeight: "400",
  },
  listItemRight: {
    fontSize: 15,
    marginRight: 6,
    fontWeight: "400",
  },
  listItemArrow: {
    fontSize: 16,
    marginLeft: 4,
    opacity: 0.4,
  },
  switchTrack: {
    width: 52,
    height: 32,
    borderRadius: 16,
    justifyContent: "center",
  },
  switchThumb: {
    width: 28,
    height: 28,
    borderRadius: 14,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 3,
  },
});
