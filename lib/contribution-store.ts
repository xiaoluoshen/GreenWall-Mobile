import { useState, useCallback, useMemo, useRef } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

export type ContributionLevel = 0 | 1 | 3 | 6 | 9;

export interface ContributionDay {
  date: string; // YYYY-MM-DD
  count: number;
  weekday: number; // 0=Sun, 6=Sat
  week: number;
}

export type ContributionMap = Record<string, number>;

const STORAGE_KEY_PREFIX = "greenwall_contributions_";

// GitHub contribution colors
export const CONTRIBUTION_COLORS = {
  light: {
    0: "#ebedf0",
    1: "#9be9a8",
    3: "#40c463",
    6: "#30a14e",
    9: "#216e39",
  },
  dark: {
    0: "#161b22",
    1: "#0e4429",
    3: "#006d32",
    6: "#26a641",
    9: "#39d353",
  },
} as const;

export function getContributionColor(
  count: number,
  scheme: "light" | "dark"
): string {
  const colors = CONTRIBUTION_COLORS[scheme];
  if (count === 0) return colors[0];
  if (count <= 2) return colors[1];
  if (count <= 5) return colors[3];
  if (count <= 8) return colors[6];
  return colors[9];
}

export function getYearDays(year: number): ContributionDay[] {
  const days: ContributionDay[] = [];
  const start = new Date(year, 0, 1);
  const end = new Date(year, 11, 31);

  // Adjust start to the previous Sunday
  const startDay = start.getDay();
  const adjustedStart = new Date(start);
  adjustedStart.setDate(adjustedStart.getDate() - startDay);

  let week = 0;
  const current = new Date(adjustedStart);

  while (current <= end || current.getDay() !== 0) {
    if (current > end && current.getDay() === 0 && days.length > 0) break;

    const dateStr = formatDate(current);
    const weekday = current.getDay();

    if (weekday === 0 && days.length > 0) {
      week++;
    }

    days.push({
      date: dateStr,
      count: 0,
      weekday,
      week,
    });

    current.setDate(current.getDate() + 1);
  }

  return days;
}

export function formatDate(date: Date): string {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

export function formatDateDisplay(dateStr: string): string {
  const [y, m, d] = dateStr.split("-");
  return `${y}/${m}/${d}`;
}

export function useContributionStore(year: number) {
  const [contributions, setContributions] = useState<ContributionMap>({});
  const [history, setHistory] = useState<ContributionMap[]>([]);
  const [historyIndex, setHistoryIndex] = useState(-1);
  const [loaded, setLoaded] = useState(false);
  const loadedYearRef = useRef<number | null>(null);

  const storageKey = `${STORAGE_KEY_PREFIX}${year}`;

  const load = useCallback(async () => {
    if (loadedYearRef.current === year) return;
    try {
      const data = await AsyncStorage.getItem(storageKey);
      if (data) {
        const parsed = JSON.parse(data);
        setContributions(parsed);
        setHistory([parsed]);
        setHistoryIndex(0);
      } else {
        setContributions({});
        setHistory([{}]);
        setHistoryIndex(0);
      }
      loadedYearRef.current = year;
      setLoaded(true);
    } catch {
      setContributions({});
      setHistory([{}]);
      setHistoryIndex(0);
      setLoaded(true);
    }
  }, [storageKey, year]);

  const save = useCallback(
    async (data: ContributionMap) => {
      try {
        await AsyncStorage.setItem(storageKey, JSON.stringify(data));
      } catch {
        // silently fail
      }
    },
    [storageKey]
  );

  const pushHistory = useCallback(
    (newContributions: ContributionMap) => {
      const newHistory = history.slice(0, historyIndex + 1);
      newHistory.push({ ...newContributions });
      setHistory(newHistory);
      setHistoryIndex(newHistory.length - 1);
    },
    [history, historyIndex]
  );

  const setCell = useCallback(
    (date: string, count: number) => {
      setContributions((prev) => {
        const next = { ...prev };
        if (count === 0) {
          delete next[date];
        } else {
          next[date] = count;
        }
        save(next);
        return next;
      });
    },
    [save]
  );

  const setCells = useCallback(
    (cells: Record<string, number>) => {
      setContributions((prev) => {
        const next = { ...prev };
        for (const [date, count] of Object.entries(cells)) {
          if (count === 0) {
            delete next[date];
          } else {
            next[date] = count;
          }
        }
        pushHistory(next);
        save(next);
        return next;
      });
    },
    [save, pushHistory]
  );

  const commitBatch = useCallback(
    (cells: Record<string, number>) => {
      setContributions((prev) => {
        const next = { ...prev };
        for (const [date, count] of Object.entries(cells)) {
          if (count === 0) {
            delete next[date];
          } else {
            next[date] = count;
          }
        }
        pushHistory(next);
        save(next);
        return next;
      });
    },
    [save, pushHistory]
  );

  const allGreen = useCallback(
    (days: ContributionDay[], intensity: ContributionLevel) => {
      const next: ContributionMap = {};
      const today = new Date();
      for (const day of days) {
        const dayDate = new Date(day.date);
        if (dayDate <= today) {
          next[day.date] = intensity;
        }
      }
      setContributions(next);
      pushHistory(next);
      save(next);
    },
    [save, pushHistory]
  );

  const reset = useCallback(() => {
    setContributions({});
    pushHistory({});
    save({});
  }, [save, pushHistory]);

  const undo = useCallback(() => {
    if (historyIndex > 0) {
      const newIndex = historyIndex - 1;
      const prev = history[newIndex];
      setHistoryIndex(newIndex);
      setContributions(prev);
      save(prev);
    }
  }, [history, historyIndex, save]);

  const redo = useCallback(() => {
    if (historyIndex < history.length - 1) {
      const newIndex = historyIndex + 1;
      const next = history[newIndex];
      setHistoryIndex(newIndex);
      setContributions(next);
      save(next);
    }
  }, [history, historyIndex, save]);

  const canUndo = historyIndex > 0;
  const canRedo = historyIndex < history.length - 1;

  const totalContributions = useMemo(() => {
    return Object.values(contributions).reduce((sum, c) => sum + c, 0);
  }, [contributions]);

  return {
    contributions,
    loaded,
    load,
    setCell,
    setCells,
    commitBatch,
    allGreen,
    reset,
    undo,
    redo,
    canUndo,
    canRedo,
    totalContributions,
  };
}
