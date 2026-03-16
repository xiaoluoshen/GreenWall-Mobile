import React, { useMemo, useRef, useCallback } from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Dimensions,
  Platform,
} from "react-native";
import {
  Gesture,
  GestureDetector,
  GestureHandlerRootView,
} from "react-native-gesture-handler";
import { useColors } from "@/hooks/use-colors";
import { useColorScheme } from "@/hooks/use-color-scheme";
import {
  getYearDays,
  getContributionColor,
  type ContributionMap,
  type ContributionLevel,
} from "@/lib/contribution-store";
import { useI18n } from "@/lib/i18n";

const CELL_SIZE = 14;
const CELL_GAP = 3;
const CELL_TOTAL = CELL_SIZE + CELL_GAP;
// Removed static labels to use i18n from props or context

interface ContributionCalendarProps {
  year: number;
  contributions: ContributionMap;
  tool: "pen" | "eraser";
  intensity: ContributionLevel;
  onCellChange?: (date: string, count: number) => void;
  onBatchEnd?: (cells: Record<string, number>) => void;
}

export function ContributionCalendar({
  year,
  contributions,
  tool,
  intensity,
  onCellChange,
  onBatchEnd,
}: ContributionCalendarProps) {
  const colors = useColors();
  const colorScheme = useColorScheme() ?? "light";
  const scheme = colorScheme === "dark" ? "dark" : "light";

  const days = useMemo(() => getYearDays(year), [year]);
  const maxWeek = useMemo(
    () => Math.max(...days.map((d) => d.week)),
    [days]
  );

  // Track cells changed during a gesture
  const batchRef = useRef<Record<string, number>>({});
  const lastCellRef = useRef<string | null>(null);

  // Calculate month label positions
  const monthPositions = useMemo(() => {
    const positions: { month: number; x: number }[] = [];
    let lastMonth = -1;
    for (const day of days) {
      const month = parseInt(day.date.split("-")[1]) - 1;
      if (month !== lastMonth) {
        positions.push({ month, x: day.week * CELL_TOTAL });
        lastMonth = month;
      }
    }
    return positions;
  }, [days]);

  // Build a lookup: week -> weekday -> day
  const grid = useMemo(() => {
    const g: Record<number, Record<number, typeof days[0]>> = {};
    for (const day of days) {
      if (!g[day.week]) g[day.week] = {};
      g[day.week][day.weekday] = day;
    }
    return g;
  }, [days]);

  const dayLabelWidth = 28;
  const calendarWidth = (maxWeek + 1) * CELL_TOTAL + dayLabelWidth;
  const { t } = useI18n();

  const getCellAtPosition = useCallback(
    (x: number, y: number) => {
      const week = Math.floor((x - dayLabelWidth) / CELL_TOTAL);
      const weekday = Math.floor(y / CELL_TOTAL);
      if (week < 0 || week > maxWeek || weekday < 0 || weekday > 6)
        return null;
      return grid[week]?.[weekday] ?? null;
    },
    [grid, maxWeek, dayLabelWidth]
  );

  const handleCellTouch = useCallback(
    (x: number, y: number) => {
      const cell = getCellAtPosition(x, y);
      if (!cell) return;
      if (lastCellRef.current === cell.date) return;
      lastCellRef.current = cell.date;

      const count = tool === "pen" ? intensity : 0;
      batchRef.current[cell.date] = count;
      onCellChange?.(cell.date, count);
    },
    [getCellAtPosition, tool, intensity, onCellChange]
  );

  const panGesture = Gesture.Pan()
    .runOnJS(true)
    .onBegin((e) => {
      batchRef.current = {};
      lastCellRef.current = null;
      handleCellTouch(e.x, e.y);
    })
    .onUpdate((e) => {
      handleCellTouch(e.x, e.y);
    })
    .onEnd(() => {
      if (Object.keys(batchRef.current).length > 0) {
        onBatchEnd?.(batchRef.current);
      }
      batchRef.current = {};
      lastCellRef.current = null;
    });

  const tapGesture = Gesture.Tap()
    .runOnJS(true)
    .onEnd((e) => {
      const cell = getCellAtPosition(e.x, e.y);
      if (!cell) return;
      const count = tool === "pen" ? intensity : 0;
      onCellChange?.(cell.date, count);
      onBatchEnd?.({ [cell.date]: count });
    });

  const gesture = Gesture.Race(panGesture, tapGesture);

  return (
    <ScrollView
      horizontal
      showsHorizontalScrollIndicator={false}
      contentContainerStyle={{ paddingHorizontal: 4 }}
    >
      <View>
        {/* Month labels */}
        <View style={[styles.monthRow, { marginLeft: dayLabelWidth }]}>
          {monthPositions.map(({ month, x }) => (
              <Text
                key={`month-${month}`}
                style={[
                  styles.monthLabel,
                  { color: colors.muted, left: x },
                ]}
              >
                {t.calendar.months[month]}
              </Text>
          ))}
        </View>

        <View style={{ flexDirection: "row" }}>
          {/* Day labels */}
          <View style={{ width: dayLabelWidth, marginTop: 2 }}>
            {t.calendar.weeks.map((label, i) => (
              <View
                key={`day-${i}`}
                style={{
                  height: CELL_TOTAL,
                  justifyContent: "center",
                }}
              >
                <Text
                  style={[styles.dayLabel, { color: colors.muted }]}
                >
                  {label}
                </Text>
              </View>
            ))}
          </View>

          {/* Grid */}
          <GestureDetector gesture={gesture}>
            <View
              style={{
                width: (maxWeek + 1) * CELL_TOTAL,
                height: 7 * CELL_TOTAL,
              }}
            >
              {Array.from({ length: maxWeek + 1 }, (_, week) => (
                <View
                  key={`week-${week}`}
                  style={[
                    styles.weekColumn,
                    { left: week * CELL_TOTAL },
                  ]}
                >
                  {Array.from({ length: 7 }, (_, weekday) => {
                    const day = grid[week]?.[weekday];
                    if (!day) {
                      return (
                        <View
                          key={`empty-${week}-${weekday}`}
                          style={[
                            styles.cell,
                            { backgroundColor: "transparent" },
                          ]}
                        />
                      );
                    }
                    const count = contributions[day.date] || 0;
                    const cellColor = getContributionColor(count, scheme);
                    return (
                      <View
                        key={day.date}
                        style={[
                          styles.cell,
                          {
                            backgroundColor: cellColor,
                            top: weekday * CELL_TOTAL,
                          },
                        ]}
                      />
                    );
                  })}
                </View>
              ))}
            </View>
          </GestureDetector>
        </View>
      </View>
    </ScrollView>
  );
}

// Legend component
export function ContributionLegend() {
  const colors = useColors();
  const colorScheme = useColorScheme() ?? "light";
  const scheme = colorScheme === "dark" ? "dark" : "light";
  const { t } = useI18n();

  const levels = [0, 1, 3, 6, 9];

  return (
    <View style={styles.legendContainer}>
      <Text style={[styles.legendText, { color: colors.muted }]}>{t.canvas.legendLess}</Text>
      {levels.map((level) => (
        <View
          key={level}
          style={[
            styles.legendCell,
            { backgroundColor: getContributionColor(level, scheme) },
          ]}
        />
      ))}
      <Text style={[styles.legendText, { color: colors.muted }]}>{t.canvas.legendMore}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  monthRow: {
    height: 20,
    position: "relative",
    marginBottom: 6,
  },
  monthLabel: {
    fontSize: 12,
    fontWeight: "500",
    position: "absolute",
    top: 2,
  },
  dayLabel: {
    fontSize: 11,
    fontWeight: "500",
    textAlign: "right",
    paddingRight: 6,
  },
  weekColumn: {
    position: "absolute",
    top: 0,
  },
  cell: {
    width: CELL_SIZE,
    height: CELL_SIZE,
    borderRadius: 3,
    position: "absolute",
  },
  legendContainer: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
    paddingVertical: 12,
    marginTop: 8,
  },
  legendCell: {
    width: 14,
    height: 14,
    borderRadius: 3,
  },
  legendText: {
    fontSize: 12,
    fontWeight: "500",
    marginHorizontal: 6,
  },
});
