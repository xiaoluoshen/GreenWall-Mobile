import React, { useState, useEffect, useMemo, useCallback } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from "react-native";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { ScreenContainer } from "@/components/screen-container";
import { useColors } from "@/hooks/use-colors";
import { useColorScheme } from "@/hooks/use-color-scheme";
import {
  ContributionCalendar,
  ContributionLegend,
} from "@/components/contribution-calendar";
import {
  MaterialButton,
  MaterialCard,
  MaterialLargeTitle,
  MaterialSegmentedControl,
} from "@/components/material-ui";
import {
  useContributionStore,
  getYearDays,
  type ContributionLevel,
  getContributionColor,
} from "@/features/contributions";
import { useI18n, interpolate } from "@/lib/i18n";
import {
  createRepository,
  getSavedToken,
  getSavedUser,
  publishContributions,
} from "@/features/github";
import { RepositoryPublishModal } from "@/features/canvas/components/repository-publish-modal";
import { usePendingPatternStamp } from "@/features/canvas/hooks/use-pending-pattern-stamp";

const CURRENT_YEAR = new Date().getFullYear();
const YEARS = Array.from({ length: 10 }, (_, i) => CURRENT_YEAR - i);
const INTENSITIES: ContributionLevel[] = [1, 3, 6, 9];

export default function CanvasScreen() {
  const colors = useColors();
  const colorScheme = useColorScheme() ?? "light";
  const scheme = colorScheme === "dark" ? "dark" : "light";
  const { t } = useI18n();

  const [year, setYear] = useState(CURRENT_YEAR);
  const [tool, setTool] = useState<"pen" | "eraser">("pen");
  const [intensity, setIntensity] = useState<ContributionLevel>(9);

  const {
    contributions,
    loaded,
    load,
    setCell,
    commitBatch,
    allGreen,
    reset,
    undo,
    redo,
    canUndo,
    canRedo,
    totalContributions,
  } = useContributionStore(year);

  // Create Repo modal state
  const [showRepoModal, setShowRepoModal] = useState(false);
  const [repoName, setRepoName] = useState(`greenwall-${year}`);
  const [repoDesc, setRepoDesc] = useState("Generated with GreenWall");
  const [repoPrivate, setRepoPrivate] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [progress, setProgress] = useState({ current: 0, total: 0 });

  useEffect(() => {
    void load();
  }, [load]);

  const days = useMemo(() => getYearDays(year), [year]);

  usePendingPatternStamp({
    days,
    isReady: loaded,
    commitBatch,
  });

  const handleCellChange = useCallback(
    (date: string, count: number) => {
      setCell(date, count);
    },
    [setCell],
  );

  const handleBatchEnd = useCallback(
    (cells: Record<string, number>) => {
      commitBatch(cells);
    },
    [commitBatch],
  );

  const handleAllGreen = useCallback(() => {
    allGreen(days, intensity);
  }, [allGreen, days, intensity]);

  const handleCreateRepo = useCallback(async () => {
    const token = await getSavedToken();
    if (!token) {
      Alert.alert(t.common.error, t.repo.loginRequired);
      return;
    }
    if (totalContributions === 0) {
      Alert.alert(t.common.error, t.repo.noContributions);
      return;
    }
    setShowRepoModal(true);
  }, [t, totalContributions]);

  const handleGenerateAndPush = useCallback(async () => {
    const token = await getSavedToken();
    const user = await getSavedUser();
    if (!token || !user) {
      Alert.alert(t.common.error, t.repo.loginRequired);
      return;
    }

    setGenerating(true);
    try {
      const result = await createRepository(token, {
        name: repoName,
        description: repoDesc,
        isPrivate: repoPrivate,
      });

      if (!result.success) {
        Alert.alert(
          t.common.error,
          interpolate(t.repo.error, { message: result.message }),
        );
        return;
      }

      const commits = Object.entries(contributions)
        .filter(([_, count]) => count > 0)
        .map(([date, count]) => ({ date, count }));
      const pushResult = await publishContributions(
        token,
        user.login,
        repoName,
        commits,
        (current, total) => setProgress({ current, total }),
      );

      if (pushResult.success) {
        setShowRepoModal(false);
        Alert.alert(t.common.success, t.repo.success);
      } else {
        Alert.alert(t.common.error, pushResult.message);
      }
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      Alert.alert(t.common.error, interpolate(t.repo.error, { message }));
    } finally {
      setGenerating(false);
    }
  }, [repoName, repoDesc, repoPrivate, contributions, t]);

  return (
    <ScreenContainer>
      <ScrollView
        style={{ flex: 1 }}
        contentContainerStyle={{ paddingBottom: 100 }}
        showsVerticalScrollIndicator={false}
      >
        {/* Title */}
        <MaterialLargeTitle style={{ marginTop: 8 }}>{t.canvas.title}</MaterialLargeTitle>

        {/* Year Selector */}
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.yearRow}
        >
          {YEARS.map((y) => (
            <TouchableOpacity
              key={y}
              onPress={() => setYear(y)}
              activeOpacity={0.7}
              style={[
                styles.yearPill,
                {
                  backgroundColor:
                    y === year ? colors.primary : colors.surface,
                  borderColor: y === year ? colors.primary : colors.border,
                },
              ]}
            >
              <Text
                style={[
                  styles.yearText,
                  {
                    color: y === year ? "#ffffff" : colors.foreground,
                  },
                ]}
              >
                {y}
              </Text>
            </TouchableOpacity>
          ))}
        </ScrollView>

        {/* Contribution Stats */}
        <Text style={[styles.statsText, { color: colors.muted }]}>
          {interpolate(t.canvas.contributions, {
            count: totalContributions,
            year,
          })}
        </Text>

        {/* Calendar */}
        <MaterialCard style={{ padding: 12, overflow: "hidden" }}>
          <ContributionCalendar
            year={year}
            contributions={contributions}
            tool={tool}
            intensity={intensity}
            onCellChange={handleCellChange}
            onBatchEnd={handleBatchEnd}
          />
          <ContributionLegend />
        </MaterialCard>

        {/* Toolbar */}
        <MaterialCard>
          {/* Pen / Eraser */}
          <MaterialSegmentedControl
            options={[t.canvas.pen, t.canvas.eraser]}
            selectedIndex={tool === "pen" ? 0 : 1}
            onSelect={(i) => setTool(i === 0 ? "pen" : "eraser")}
          />

          {/* Intensity */}
          {tool === "pen" && (
            <View style={styles.intensitySection}>
              <Text style={[styles.sectionLabel, { color: colors.muted }]}>
                {t.canvas.intensity}
              </Text>
              <View style={styles.intensityRow}>
                {INTENSITIES.map((level) => (
                  <TouchableOpacity
                    key={level}
                    onPress={() => setIntensity(level)}
                    activeOpacity={0.7}
                    style={[
                      styles.intensityButton,
                      {
                        backgroundColor: getContributionColor(level, scheme),
                        borderWidth: level === intensity ? 2 : 0,
                        borderColor: colors.foreground,
                      },
                    ]}
                  >
                    <Text
                      style={[
                        styles.intensityText,
                        {
                          color:
                            level >= 6
                              ? "#ffffff"
                              : scheme === "dark"
                                ? "#ffffff"
                                : "#1f2328",
                        },
                      ]}
                    >
                      {level}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>
          )}

          {/* Action Buttons */}
          <View style={styles.actionRow}>
            <TouchableOpacity
              onPress={handleAllGreen}
              style={[styles.actionBtn, { backgroundColor: colors.background }]}
              activeOpacity={0.7}
            >
              <MaterialIcons name="grid-on" size={18} color={colors.primary} />
              <Text style={[styles.actionBtnText, { color: colors.foreground }]}>
                {t.canvas.allGreen}
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              onPress={reset}
              style={[styles.actionBtn, { backgroundColor: colors.background }]}
              activeOpacity={0.7}
            >
              <MaterialIcons name="refresh" size={18} color={colors.error} />
              <Text style={[styles.actionBtnText, { color: colors.foreground }]}>
                {t.canvas.reset}
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              onPress={undo}
              disabled={!canUndo}
              style={[
                styles.actionBtn,
                {
                  backgroundColor: colors.background,
                  opacity: canUndo ? 1 : 0.4,
                },
              ]}
              activeOpacity={0.7}
            >
              <MaterialIcons name="undo" size={18} color={colors.foreground} />
              <Text style={[styles.actionBtnText, { color: colors.foreground }]}>
                {t.canvas.undo}
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              onPress={redo}
              disabled={!canRedo}
              style={[
                styles.actionBtn,
                {
                  backgroundColor: colors.background,
                  opacity: canRedo ? 1 : 0.4,
                },
              ]}
              activeOpacity={0.7}
            >
              <MaterialIcons name="redo" size={18} color={colors.foreground} />
              <Text style={[styles.actionBtnText, { color: colors.foreground }]}>
                {t.canvas.redo}
              </Text>
            </TouchableOpacity>
          </View>
        </MaterialCard>

        {/* Create Repo Button */}
        <View style={{ marginHorizontal: 16, marginTop: 12 }}>
          <MaterialButton
            title={t.canvas.createRepo}
            onPress={handleCreateRepo}
            variant="primary"
          />
        </View>
      </ScrollView>

      <RepositoryPublishModal
        visible={showRepoModal}
        repositoryName={repoName}
        description={repoDesc}
        isPrivate={repoPrivate}
        isSubmitting={generating}
        progress={progress}
        onRepositoryNameChange={setRepoName}
        onDescriptionChange={setRepoDesc}
        onPrivacyChange={setRepoPrivate}
        onCancel={() => setShowRepoModal(false)}
        onSubmit={handleGenerateAndPush}
      />
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  yearRow: {
    paddingHorizontal: 16,
    gap: 8,
    paddingVertical: 8,
  },
  yearPill: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    borderWidth: 1,
  },
  yearText: {
    fontSize: 14,
    fontWeight: "600",
  },
  statsText: {
    fontSize: 13,
    textAlign: "center",
    marginVertical: 8,
    marginHorizontal: 16,
  },
  intensitySection: {
    marginTop: 16,
  },
  sectionLabel: {
    fontSize: 13,
    fontWeight: "500",
    marginBottom: 8,
  },
  intensityRow: {
    flexDirection: "row",
    gap: 10,
  },
  intensityButton: {
    width: 40,
    height: 40,
    borderRadius: 8,
    alignItems: "center",
    justifyContent: "center",
  },
  intensityText: {
    fontSize: 14,
    fontWeight: "600",
  },
  actionRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginTop: 16,
    justifyContent: "space-between",
  },
  actionBtn: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderRadius: 12,
    gap: 6,
    flex: 1,
    minWidth: "48%",
    justifyContent: "center",
  },
  actionBtnText: {
    fontSize: 13,
    fontWeight: "500",
  },
});
