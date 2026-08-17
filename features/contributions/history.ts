import type { ContributionMap } from "./domain";

export const MAX_HISTORY_ENTRIES = 100;

export interface ContributionHistory {
  snapshots: ContributionMap[];
  currentIndex: number;
}

export function createContributionHistory(
  initialContributions: ContributionMap = {},
): ContributionHistory {
  return {
    snapshots: [{ ...initialContributions }],
    currentIndex: 0,
  };
}

export function contributionMapsEqual(
  left: ContributionMap,
  right: ContributionMap,
): boolean {
  const leftEntries = Object.entries(left);
  if (leftEntries.length !== Object.keys(right).length) return false;
  return leftEntries.every(([date, count]) => right[date] === count);
}

export function commitContributionHistory(
  history: ContributionHistory,
  contributions: ContributionMap,
): ContributionHistory {
  const currentSnapshot = history.snapshots[history.currentIndex] ?? {};
  if (contributionMapsEqual(currentSnapshot, contributions)) return history;

  const snapshots = history.snapshots
    .slice(0, history.currentIndex + 1)
    .concat({ ...contributions })
    .slice(-MAX_HISTORY_ENTRIES);

  return {
    snapshots,
    currentIndex: snapshots.length - 1,
  };
}

export function undoContributionHistory(
  history: ContributionHistory,
): ContributionHistory | null {
  if (history.currentIndex <= 0) return null;
  return { ...history, currentIndex: history.currentIndex - 1 };
}

export function redoContributionHistory(
  history: ContributionHistory,
): ContributionHistory | null {
  if (history.currentIndex >= history.snapshots.length - 1) return null;
  return { ...history, currentIndex: history.currentIndex + 1 };
}

export function getCurrentContributionSnapshot(
  history: ContributionHistory,
): ContributionMap {
  return history.snapshots[history.currentIndex] ?? {};
}
