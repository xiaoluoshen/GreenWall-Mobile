import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { Appearance, useColorScheme as useSystemColorScheme } from "react-native";
import { PaperProvider } from "react-native-paper";

import type { ColorScheme } from "@/constants/theme";
import { materialDarkTheme, materialLightTheme } from "@/lib/material-theme";

type ThemeContextValue = {
  colorScheme: ColorScheme;
  setColorScheme: (scheme: ColorScheme) => void;
};

const ThemeContext = createContext<ThemeContextValue | null>(null);

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const systemScheme = useSystemColorScheme() ?? "light";
  const [colorScheme, setColorSchemeState] = useState<ColorScheme>(systemScheme);

  const applyScheme = useCallback((scheme: ColorScheme) => {
    Appearance.setColorScheme?.(scheme);
    if (typeof document !== "undefined") {
      document.documentElement.dataset.theme = scheme;
      document.documentElement.style.colorScheme = scheme;
    }
  }, []);

  const setColorScheme = useCallback(
    (scheme: ColorScheme) => {
      setColorSchemeState(scheme);
      applyScheme(scheme);
    },
    [applyScheme],
  );

  useEffect(() => {
    applyScheme(colorScheme);
  }, [applyScheme, colorScheme]);

  const value = useMemo(
    () => ({ colorScheme, setColorScheme }),
    [colorScheme, setColorScheme],
  );
  const materialTheme = colorScheme === "dark" ? materialDarkTheme : materialLightTheme;

  return (
    <ThemeContext.Provider value={value}>
      <PaperProvider theme={materialTheme}>{children}</PaperProvider>
    </ThemeContext.Provider>
  );
}

export function useThemeContext(): ThemeContextValue {
  const ctx = useContext(ThemeContext);
  if (!ctx) {
    throw new Error("useThemeContext must be used within ThemeProvider");
  }
  return ctx;
}
