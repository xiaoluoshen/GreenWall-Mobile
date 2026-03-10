import React, { useState, useMemo, useCallback } from "react";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from "react-native";
import { ScreenContainer } from "@/components/screen-container";
import { useColors } from "@/hooks/use-colors";
import { useColorScheme } from "@/hooks/use-color-scheme";
import {
  MCard,
  MLargeTitle,
  MSegmentedControl,
  MButton,
} from "@/components/miuix";
import {
  ALL_PATTERNS,
  type PatternCategory,
  type CharacterPattern,
} from "@/lib/character-patterns";
import {
  getContributionColor,
  type ContributionLevel,
} from "@/lib/contribution-store";
import { useI18n } from "@/lib/i18n";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { router } from "expo-router";

const CATEGORIES: PatternCategory[] = [
  "uppercase",
  "lowercase",
  "numbers",
  "symbols",
];

const MINI_CELL = 4;
const MINI_GAP = 1;
const PREVIEW_CELL = 10;
const PREVIEW_GAP = 2;

export default function CharactersScreen() {
  const colors = useColors();
  const colorScheme = useColorScheme() ?? "light";
  const scheme = colorScheme === "dark" ? "dark" : "light";
  const { t } = useI18n();

  const [categoryIndex, setCategoryIndex] = useState(0);
  const [selectedChar, setSelectedChar] = useState<string | null>(null);

  const category = CATEGORIES[categoryIndex];
  const patterns = ALL_PATTERNS[category];
  const charKeys = useMemo(() => Object.keys(patterns), [category]);

  const categoryLabels = [
    t.characters.uppercase,
    t.characters.lowercase,
    t.characters.numbers,
    t.characters.symbols,
  ];

  const selectedPattern = selectedChar ? patterns[selectedChar] : null;

  const handlePlaceOnCanvas = useCallback(async () => {
    if (!selectedChar || !selectedPattern) return;

    // Store the pattern in AsyncStorage for the canvas to pick up
    await AsyncStorage.setItem(
      "greenwall_pending_pattern",
      JSON.stringify({
        char: selectedChar,
        pattern: selectedPattern,
      })
    );

    // Navigate to canvas tab
    router.navigate("/(tabs)");
    Alert.alert(
      t.characters.preview,
      t.characters.tapToPlace
    );
  }, [selectedChar, selectedPattern, t]);

  const renderCharItem = useCallback(
    ({ item }: { item: string }) => {
      const pattern = patterns[item];
      const isSelected = item === selectedChar;
      return (
        <TouchableOpacity
          onPress={() => setSelectedChar(item)}
          activeOpacity={0.7}
          style={[
            styles.charItem,
            {
              backgroundColor: isSelected ? colors.primary + "20" : colors.surface,
              borderColor: isSelected ? colors.primary : colors.border,
              borderWidth: isSelected ? 2 : 1,
            },
          ]}
        >
          {/* Mini pattern preview */}
          <View style={styles.miniPreview}>
            {pattern.map((row, ri) => (
              <View key={ri} style={{ flexDirection: "row" }}>
                {row.map((cell, ci) => (
                  <View
                    key={ci}
                    style={{
                      width: MINI_CELL,
                      height: MINI_CELL,
                      marginRight: MINI_GAP,
                      marginBottom: MINI_GAP,
                      backgroundColor: cell
                        ? getContributionColor(9, scheme)
                        : getContributionColor(0, scheme),
                      borderRadius: 1,
                    }}
                  />
                ))}
              </View>
            ))}
          </View>
          <Text
            style={[
              styles.charLabel,
              {
                color: isSelected ? colors.primary : colors.foreground,
                fontWeight: isSelected ? "700" : "500",
              },
            ]}
          >
            {item}
          </Text>
        </TouchableOpacity>
      );
    },
    [patterns, selectedChar, colors, scheme]
  );

  return (
    <ScreenContainer>
      {/* Title */}
      <MLargeTitle style={{ marginTop: 8 }}>{t.characters.title}</MLargeTitle>

      {/* Category Tabs */}
      <View style={{ marginHorizontal: 16, marginBottom: 12 }}>
        <MSegmentedControl
          options={categoryLabels}
          selectedIndex={categoryIndex}
          onSelect={setCategoryIndex}
        />
      </View>

      {/* Character Grid */}
      <FlatList
        data={charKeys}
        renderItem={renderCharItem}
        keyExtractor={(item) => `${category}-${item}`}
        numColumns={4}
        contentContainerStyle={styles.gridContainer}
        columnWrapperStyle={styles.gridRow}
        ListFooterComponent={
          selectedPattern ? (
            <View>
              {/* Preview */}
              <MCard>
                <Text
                  style={[
                    styles.previewTitle,
                    { color: colors.foreground },
                  ]}
                >
                  {t.characters.preview}: {selectedChar}
                </Text>
                <View style={styles.previewGrid}>
                  {selectedPattern.map((row, ri) => (
                    <View key={ri} style={{ flexDirection: "row" }}>
                      {row.map((cell, ci) => (
                        <View
                          key={ci}
                          style={{
                            width: PREVIEW_CELL,
                            height: PREVIEW_CELL,
                            marginRight: PREVIEW_GAP,
                            marginBottom: PREVIEW_GAP,
                            backgroundColor: cell
                              ? getContributionColor(9, scheme)
                              : getContributionColor(0, scheme),
                            borderRadius: 2,
                          }}
                        />
                      ))}
                    </View>
                  ))}
                </View>
              </MCard>

              {/* Place Button */}
              <View style={{ marginHorizontal: 16, marginTop: 12 }}>
                <MButton
                  title={t.characters.tapToPlace}
                  onPress={handlePlaceOnCanvas}
                  variant="primary"
                />
              </View>
            </View>
          ) : null
        }
      />
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  gridContainer: {
    paddingHorizontal: 16,
    paddingBottom: 100,
  },
  gridRow: {
    gap: 10,
    marginBottom: 10,
  },
  charItem: {
    flex: 1,
    maxWidth: "25%",
    aspectRatio: 1,
    borderRadius: 12,
    alignItems: "center",
    justifyContent: "center",
    padding: 8,
  },
  miniPreview: {
    alignItems: "center",
    marginBottom: 4,
  },
  charLabel: {
    fontSize: 14,
    marginTop: 2,
  },
  previewTitle: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: 12,
  },
  previewGrid: {
    alignItems: "center",
  },
});
