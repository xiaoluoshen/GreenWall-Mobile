import { useCallback, useMemo, useRef, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {
  applyContributionUpdates,
  createAllGreenContributions,
  sanitizeContributionMap,
  type ContributionDay,
  type ContributionLevel,
  type ContributionMap,
} from "./domain";
import {
  commitContributionHistory,
  createContributionHistory,
  getCurrentContributionSnapshot,
  redoContributionHistory,
  undoContributionHistory,
  type ContributionHistory,
} from "./history";

const STORAGE_KEY_PREFIX = "greenwall_contributions_";

interface ContributionStoreState {
  contributions: ContributionMap;
  history: ContributionHistory;
  loadedYear: number | null;
}

function createInitialState(): ContributionStoreState {
  return {
    contributions: {},
    history: createContributionHistory(),
    loadedYear: null,
  };
}

export function useContributionStore(year: number) {
  const [state, setState] = useState<ContributionStoreState>(createInitialState);
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
    setState(createInitialState());

    try {
      const storedValue = await AsyncStorage.getItem(storageKey);
      const parsedValue: unknown = storedValue ? JSON.parse(storedValue) : {};
      const contributions = sanitizeContributionMap(parsedValue, year);

      if (loadRequestRef.current !== requestId) return;
      setState({
        contributions,
        history: createContributionHistory(contributions),
        loadedYear: year,
      });
    } catch (error: unknown) {
      if (loadRequestRef.current !== requestId) return;

      const message = error instanceof Error ? error.message : String(error);
      console.warn(`Unable to load contributions for ${year}: ${message}`);
      setState({ ...createInitialState(), loadedYear: year });
    }
  }, [state.loadedYear, storageKey, year]);

  const setCell = useCallback(
    (date: string, count: number) => {
      setState((previousState) => {
        const contributions = applyContributionUpdates(
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

  const commitContributions = useCallback(
    (nextContributions: ContributionMap) => {
      setState((previousState) => {
        const history = commitContributionHistory(
          previousState.history,
          nextContributions,
        );
        const historyChanged = history !== previousState.history;
        const contributionsChanged = nextContributions !== previousState.contributions;

        if (!historyChanged && !contributionsChanged) return previousState;
        persist(nextContributions);
        return { ...previousState, contributions: nextContributions, history };
      });
    },
    [persist],
  );

  const commitBatch = useCallback(
    (updates: Record<string, number>) => {
      setState((previousState) => {
        const contributions = applyContributionUpdates(
          previousState.contributions,
          updates,
          year,
        );
        const history = commitContributionHistory(previousState.history, contributions);
        const historyChanged = history !== previousState.history;
        const contributionsChanged = contributions !== previousState.contributions;

        if (!historyChanged && !contributionsChanged) return previousState;
        persist(contributions);
        return { ...previousState, contributions, history };
      });
    },
    [persist, year],
  );

  const replaceContributions = useCallback(
    (nextContributions: ContributionMap) => {
      commitContributions(sanitizeContributionMap(nextContributions, year));
    },
    [commitContributions, year],
  );

  const allGreen = useCallback(
    (days: ContributionDay[], intensity: ContributionLevel) => {
      replaceContributions(createAllGreenContributions(days, year, intensity));
    },
    [replaceContributions, year],
  );

  const reset = useCallback(() => {
    replaceContributions({});
  }, [replaceContributions]);

  const undo = useCallback(() => {
    setState((previousState) => {
      const history = undoContributionHistory(previousState.history);
      if (!history) return previousState;

      const contributions = getCurrentContributionSnapshot(history);
      persist(contributions);
      return { ...previousState, contributions, history };
    });
  }, [persist]);

  const redo = useCallback(() => {
    setState((previousState) => {
      const history = redoContributionHistory(previousState.history);
      if (!history) return previousState;

      const contributions = getCurrentContributionSnapshot(history);
      persist(contributions);
      return { ...previousState, contributions, history };
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
    canUndo: state.history.currentIndex > 0,
    canRedo: state.history.currentIndex < state.history.snapshots.length - 1,
    totalContributions,
  };
}
