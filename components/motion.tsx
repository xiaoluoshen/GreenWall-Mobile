import { AccessibilityInfo, Animated, Easing, type StyleProp, type ViewStyle } from "react-native";
import { useEffect, useRef, useState } from "react";

export function useReducedMotion() {
  const [reducedMotion, setReducedMotion] = useState(false);

  useEffect(() => {
    AccessibilityInfo.isReduceMotionEnabled().then(setReducedMotion).catch(() => undefined);
    const subscription = AccessibilityInfo.addEventListener("reduceMotionChanged", setReducedMotion);
    return () => subscription.remove();
  }, []);

  return reducedMotion;
}

export function MotionFadeIn({
  children,
  style,
  delay = 0,
}: {
  children: React.ReactNode;
  style?: StyleProp<ViewStyle>;
  delay?: number;
}) {
  const reducedMotion = useReducedMotion();
  const opacity = useRef(new Animated.Value(reducedMotion ? 1 : 0)).current;
  const translateY = useRef(new Animated.Value(reducedMotion ? 0 : 8)).current;

  useEffect(() => {
    if (reducedMotion) {
      opacity.setValue(1);
      translateY.setValue(0);
      return;
    }

    Animated.parallel([
      Animated.timing(opacity, {
        toValue: 1,
        duration: 220,
        delay,
        easing: Easing.out(Easing.cubic),
        useNativeDriver: true,
      }),
      Animated.timing(translateY, {
        toValue: 0,
        duration: 220,
        delay,
        easing: Easing.out(Easing.cubic),
        useNativeDriver: true,
      }),
    ]).start();
  }, [delay, opacity, reducedMotion, translateY]);

  return (
    <Animated.View style={[style, { opacity, transform: [{ translateY }] }]}>
      {children}
    </Animated.View>
  );
}

export function usePressScale(disabled = false) {
  const reducedMotion = useReducedMotion();
  const scale = useRef(new Animated.Value(1)).current;

  const animate = (toValue: number) => {
    if (reducedMotion || disabled) return;
    Animated.spring(scale, {
      toValue,
      damping: 18,
      stiffness: 260,
      mass: 0.45,
      useNativeDriver: true,
    }).start();
  };

  return {
    style: { transform: [{ scale }] },
    onPressIn: () => animate(0.98),
    onPressOut: () => animate(1),
  };
}
