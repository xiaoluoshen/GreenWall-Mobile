import React, { useState, useEffect, useCallback, useMemo } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  Alert,
  Modal,
  TextInput,
  ActivityIndicator,
  Platform,
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
  MCard,
  MLargeTitle,
  MButton,
  MSegmentedControl,
  MSwitch,
  MSectionLabel,
} from "@/components/miuix";
import {
  useContributionStore,
  getYearDays,
  type ContributionLevel,
  getContributionColor,
} from "@/lib/contribution-store";
import { useI18n, interpolate } from "@/lib/i18n";
import {
  getSavedToken,
  getSavedUser,
  createRepository,
  pushContributions,
  type GitHubUser,
} from "@/lib/github-api";

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

  const store = useContributionStore(year);

  // Create Repo modal state
  const [showRepoModal, setShowRepoModal] = useState(false);
  const [repoName, setRepoName] = useState(`greenwall-${year}`);
  const [repoDesc, setRepoDesc] = useState("Generated with GreenWall");
  const [repoPrivate, setRepoPrivate] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [progress, setProgress] = useState({ current: 0, total: 0 });

  useEffect(() => {
    store.load();
  }, [year]);

  const days = useMemo(() => getYearDays(year), [year]);

  const handleCellChange = useCallback(
    (date: string, count: number) => {
      store.setCell(date, count);
    },
    [store]
  );

  const handleBatchEnd = useCallback(
    (cells: Record<string, number>) => {
      store.commitBatch(cells);
    },
    [store]
  );

  const handleAllGreen = useCallback(() => {
    store.allGreen(days, intensity);
  }, [store, days, intensity]);

  const handleCreateRepo = useCallback(async () => {
    const token = await getSavedToken();
    if (!token) {
      Alert.alert(t.common.error, t.repo.loginRequired);
      return;
    }
    if (store.totalContributions === 0) {
      Alert.alert(t.common.error, t.repo.noContributions);
      return;
    }
    setShowRepoModal(true);
  }, [t, store.totalContributions]);

  const handleGenerateAndPush = useCallback(async () => {
    const token = await getSavedToken();
    const user = await getSavedUser();
    if (!token || !user) {
      Alert.alert(t.common.error, t.repo.loginRequired);
      return;
    }

    setGenerating(true);

    // Create repo
    const result = await createRepository(token, {
      name: repoName,
      description: repoDesc,
      isPrivate: repoPrivate,
    });

    if (!result.success) {
      setGenerating(false);
      Alert.alert(t.common.error, interpolate(t.repo.error, { message: result.message }));
      return;
    }

    // Push contributions
    const commits = Object.entries(store.contributions)
      .filter(([_, count]) => count > 0)
      .map(([date, count]) => ({ date, count }));

    const pushResult = await pushContributions(
      token,
      user.login,
      repoName,
      commits,
      (current, total) => setProgress({ current, total })
    );

    setGenerating(false);
    setShowRepoModal(false);

    if (pushResult.success) {
      Alert.alert(t.common.success, t.repo.success);
    } else {
      Alert.alert(t.common.error, pushResult.message);
    }
  }, [repoName, repoDesc, repoPrivate, store.contributions, t]);

  return (
    <ScreenContainer>
      <ScrollView
        style={{ flex: 1 }}
        contentContainerStyle={{ paddingBottom: 100 }}
        showsVerticalScrollIndicator={false}
      >
        {/* Title */}
        <MLargeTitle style={{ marginTop: 8 }}>{t.canvas.title}</MLargeTitle>

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
            count: store.totalContributions,
            year,
          })}
        </Text>

        {/* Calendar */}
        <MCard style={{ padding: 12, overflow: "hidden" }}>
          <ContributionCalendar
            year={year}
            contributions={store.contributions}
            tool={tool}
            intensity={intensity}
            onCellChange={handleCellChange}
            onBatchEnd={handleBatchEnd}
          />
          <ContributionLegend />
        </MCard>

        {/* Toolbar */}
        <MCard>
          {/* Pen / Eraser */}
          <MSegmentedControl
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
              onPress={store.reset}
              style={[styles.actionBtn, { backgroundColor: colors.background }]}
              activeOpacity={0.7}
            >
              <MaterialIcons name="refresh" size={18} color={colors.error} />
              <Text style={[styles.actionBtnText, { color: colors.foreground }]}>
                {t.canvas.reset}
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              onPress={store.undo}
              disabled={!store.canUndo}
              style={[
                styles.actionBtn,
                {
                  backgroundColor: colors.background,
                  opacity: store.canUndo ? 1 : 0.4,
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
              onPress={store.redo}
              disabled={!store.canRedo}
              style={[
                styles.actionBtn,
                {
                  backgroundColor: colors.background,
                  opacity: store.canRedo ? 1 : 0.4,
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
        </MCard>

        {/* Create Repo Button */}
        <View style={{ marginHorizontal: 16, marginTop: 12 }}>
          <MButton
            title={t.canvas.createRepo}
            onPress={handleCreateRepo}
            variant="primary"
          />
        </View>
      </ScrollView>

      {/* Create Repo Modal */}
      <Modal
        visible={showRepoModal}
        transparent
        animationType="fade"
        onRequestClose={() => !generating && setShowRepoModal(false)}
      >
        <View style={styles.modalOverlay}>
          <View
            style={[styles.modalContent, { backgroundColor: colors.surface }]}
          >
            <Text style={[styles.modalTitle, { color: colors.foreground }]}>
              {t.repo.title}
            </Text>

            <Text style={[styles.inputLabel, { color: colors.muted }]}>
              {t.repo.name}
            </Text>
            <TextInput
              value={repoName}
              onChangeText={setRepoName}
              placeholder={t.repo.namePlaceholder}
              placeholderTextColor={colors.muted}
              style={[
                styles.input,
                {
                  color: colors.foreground,
                  backgroundColor: colors.background,
                  borderColor: colors.border,
                },
              ]}
              editable={!generating}
            />

            <Text style={[styles.inputLabel, { color: colors.muted }]}>
              {t.repo.description}
            </Text>
            <TextInput
              value={repoDesc}
              onChangeText={setRepoDesc}
              placeholder={t.repo.descriptionPlaceholder}
              placeholderTextColor={colors.muted}
              style={[
                styles.input,
                {
                  color: colors.foreground,
                  backgroundColor: colors.background,
                  borderColor: colors.border,
                },
              ]}
              editable={!generating}
            />

            <View style={styles.switchRow}>
              <Text style={[styles.switchLabel, { color: colors.foreground }]}>
                {repoPrivate ? t.repo.private : t.repo.public}
              </Text>
              <MSwitch
                value={repoPrivate}
                onValueChange={setRepoPrivate}
              />
            </View>

            {generating && (
              <View style={styles.progressRow}>
                <ActivityIndicator color={colors.primary} />
                <Text style={[styles.progressText, { color: colors.muted }]}>
                  {t.repo.generating}{" "}
                  {progress.total > 0 &&
                    `(${progress.current}/${progress.total})`}
                </Text>
              </View>
            )}

            <View style={styles.modalButtons}>
              <MButton
                title={t.repo.cancel}
                variant="secondary"
                onPress={() => setShowRepoModal(false)}
                disabled={generating}
                style={{ flex: 1, marginRight: 8 }}
              />
              <MButton
                title={t.repo.confirm}
                variant="primary"
                onPress={handleGenerateAndPush}
                disabled={generating || !repoName.trim()}
                style={{ flex: 1, marginLeft: 8 }}
              />
            </View>
          </View>
        </View>
      </Modal>
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
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.5)",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },
  modalContent: {
    width: "100%",
    maxWidth: 420,
    borderRadius: 20,
    padding: 28,
  },
  modalTitle: {
    fontSize: 22,
    fontWeight: "700",
    marginBottom: 24,
  },
  inputLabel: {
    fontSize: 14,
    fontWeight: "600",
    marginBottom: 8,
    marginTop: 16,
  },
  input: {
    borderWidth: 1,
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 12,
    fontSize: 16,
  },
  switchRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginTop: 16,
  },
  switchLabel: {
    fontSize: 16,
  },
  progressRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginTop: 16,
  },
  progressText: {
    fontSize: 15,
    fontWeight: "500",
  },
  modalButtons: {
    flexDirection: "row",
    marginTop: 28,
    gap: 12,
  },
});
