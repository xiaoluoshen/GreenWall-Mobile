import { useCallback, useMemo, useRef, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

export type ContributionLevel = 0 | 1 | 3 | 6 | 9;

export interface ContributionDay {
  date: string;
  count: number;
  weekday: number;
  week: number;
}

export type ContributionMap = Record<string, number>;

interface ContributionStoreState {
  contributions: ContributionMap;
  history: ContributionMap[];
  historyIndex: number;
  loadedYear: number | null;
}

const STORAGE_KEY_PREFIX = "greenwall_contributions_";
const MAX_HISTORY_ENTRIES = 100;
const CONTRIBUTION_LEVELS: readonly ContributionLevel[] = [0, 1, 3, 6, 9];

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
  scheme: "light" | "dark",
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
  const adjustedStart = new Date(start);
  adjustedStart.setDate(adjustedStart.getDate() - start.getDay());

  let week = 0;
  const current = new Date(adjustedStart);

  while (current <= end || current.getDay() !== 0) {
    if (current > end && current.getDay() === 0 && days.length > 0) break;

    const weekday = current.getDay();
    if (weekday === 0 && days.length > 0) week += 1;

    days.push({
      date: formatDate(current),
      count: 0,
      weekday,
      week,
    });

    current.setDate(current.getDate() + 1);
  }

  return days;
}

export function formatDate(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function formatDateDisplay(date: string): string {
  const [year, month, day] = date.split("-");
  return `${year}/${month}/${day}`;
}

export function isContributionDateInYear(date: string, year: number): boolean {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(date);
  if (!match || Number(match[1]) !== year) return false;

  const parsedDate = new Date(year, Number(match[2]) - 1, Number(match[3]));
  return formatDate(parsedDate) === date;
}

export function sanitizeContributionMap(value: unknown, year: number): ContributionMap {
  if (!value || typeof value !== "object" || Array.isArray(value)) return {};

  const sanitized: ContributionMap = {};
  for (const [date, count] of Object.entries(value as Record<string, unknown>)) {
    if (
      isContributionDateInYear(date, year) &&
      typeof count === "number" &&
      isContributionLevel(count) &&
      count > 0
    ) {
      sanitized[date] = count;
    }
  }

  return sanitized;
}

function isContributionLevel(value: number): value is ContributionLevel {
  return CONTRIBUTION_LEVELS.includes(value as ContributionLevel);
}

function applyCellUpdates(
  contributions: ContributionMap,
  updates: Record<string, number>,
  year: number,
): ContributionMap {
  let hasChanged = false;
  const nextContributions = { ...contributions };

  for (const [date, count] of Object.entries(updates)) {
    if (!isContributionDateInYear(date, year) || !isContributionLevel(count)) continue;

    if (count === 0) {
      if (date in nextContributions) {
        delete nextContributions[date];
        hasChanged = true;
      }
    } else if (nextContributions[date] !== count) {
      nextContributions[date] = count;
      hasChanged = true;
    }
  }

  return hasChanged ? nextContributions : contributions;
}

function contributionMapsEqual(left: ContributionMap, right: ContributionMap): boolean {
  const leftEntries = Object.entries(left);
  if (leftEntries.length !== Object.keys(right).length) return false;
  return leftEntries.every(([date, count]) => right[date] === count);
}

function commitSnapshot(
  state: ContributionStoreState,
  contributions: ContributionMap,
): ContributionStoreState {
  const currentHistorySnapshot = state.history[state.historyIndex] ?? {};
  if (contributionMapsEqual(currentHistorySnapshot, contributions)) {
    if (state.contributions === contributions) return state;
    return { ...state, contributions };
  }

  const history = state.history
    .slice(0, state.historyIndex + 1)
    .concat({ ...contributions })
    .slice(-MAX_HISTORY_ENTRIES);

  return {
    ...state,
    contributions,
    history,
    historyIndex: history.length - 1,
  };
}

export function useContributionStore(year: number) {
  const [state, setState] = useState<ContributionStoreState>({
    contributions: {},
    history: [{}],
    historyIndex: 0,
    loadedYear: null,
  });
  const loadRequestRef = useRef(0);
  const saveQueueRef = useRef(Promise.resolve());
  const storageKey = `${STORAGE_KEY_PREFIX}${year}`;

  const persist = useCallback(
    (contributions: ContributionMap) => {
      const serializedContributions = JSON.stringify(contributions);
      saveQueueRef.current = saveQueueRef.current
        .catch(() => undefined)
        .then(() => AsyncStorage.setItem(storageKey, serializedContributions))
        .catch((error: unknown) => {
          const message = error instanceof Error ? error.message : String(error);
          console.warn(`Unable to persist contributions for ${year}: ${message}`);
        });
    },
    [storageKey, year],
  );

  const load = useCallback(async () => {
    if (state.loadedYear === year) return;

    const requestId = loadRequestRef.current + 1;
    loadRequestRef.current = requestId;
    setState({
      contributions: {},
      history: [{}],
      historyIndex: 0,
      loadedYear: null,
    });

    try {
      const storedValue = await AsyncStorage.getItem(storageKey);
      const parsedValue: unknown = storedValue ? JSON.parse(storedValue) : {};
      const contributions = sanitizeContributionMap(parsedValue, year);

      if (loadRequestRef.current !== requestId) return;
      setState({
        contributions,
        history: [{ ...contributions }],
        historyIndex: 0,
        loadedYear: year,
      });
    } catch (error: unknown) {
      if (loadRequestRef.current !== requestId) return;
      const message = error instanceof Error ? error.message : String(error);
      console.warn(`Unable to load contributions for ${year}: ${message}`);
      setState({
        contributions: {},
        history: [{}],
        historyIndex: 0,
        loadedYear: year,
      });
    }
  }, [state.loadedYear, storageKey, year]);

  const setCell = useCallback(
    (date: string, count: number) => {
      setState((previousState) => {
        const contributions = applyCellUpdates(
          previousState.contributions,
          { [date]: count },
          year,
        );
        if (contributions === previousState.contributions) return previousState;
        return { ...previousState, contributions };
      });
    },
    [year],
  );

  const commitBatch = useCallback(
    (cells: Record<string, number>) => {
      setState((previousState) => {
        const contributions = applyCellUpdates(
          previousState.contributions,
          cells,
          year,
        );
        const nextState = commitSnapshot(previousState, contributions);
        if (nextState === previousState) return previousState;
        persist(contributions);
        return nextState;
      });
    },
    [persist, year],
  );

  const replaceContributions = useCallback(
    (nextContributions: ContributionMap) => {
      const sanitizedContributions = sanitizeContributionMap(nextContributions, year);
      setState((previousState) => {
        const nextState = commitSnapshot(previousState, sanitizedContributions);
        if (nextState === previousState) return previousState;
        persist(sanitizedContributions);
        return nextState;
      });
    },
    [persist, year],
  );

  const allGreen = useCallback(
    (days: ContributionDay[], intensity: ContributionLevel) => {
      const today = new Date();
      const contributions: ContributionMap = {};

      for (const day of days) {
        if (!isContributionDateInYear(day.date, year)) continue;
        const [dayYear, month, dayOfMonth] = day.date.split("-").map(Number);
        const date = new Date(dayYear, month - 1, dayOfMonth);
        if (date <= today) contributions[day.date] = intensity;
      }

      replaceContributions(contributions);
    },
    [replaceContributions, year],
  );

  const reset = useCallback(() => {
    replaceContributions({});
  }, [replaceContributions]);

  const undo = useCallback(() => {
    setState((previousState) => {
      if (previousState.historyIndex <= 0) return previousState;

      const historyIndex = previousState.historyIndex - 1;
      const contributions = previousState.history[historyIndex];
      persist(contributions);
      return { ...previousState, contributions, historyIndex };
    });
  }, [persist]);

  const redo = useCallback(() => {
    setState((previousState) => {
      if (previousState.historyIndex >= previousState.history.length - 1) {
        return previousState;
      }

      const historyIndex = previousState.historyIndex + 1;
      const contributions = previousState.history[historyIndex];
      persist(contributions);
      return { ...previousState, contributions, historyIndex };
    });
  }, [persist]);

  const totalContributions = useMemo(
    () => Object.values(state.contributions).reduce((sum, count) => sum + count, 0),
    [state.contributions],
  );

  return {
    contributions: state.contributions,
    loaded: state.loadedYear === year,
    load,
    setCell,
    commitBatch,
    allGreen,
    reset,
    undo,
    redo,
    canUndo: state.historyIndex > 0,
    canRedo: state.historyIndex < state.history.length - 1,
    totalContributions,
  };
}
