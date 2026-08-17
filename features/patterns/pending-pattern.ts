import AsyncStorage from "@react-native-async-storage/async-storage";
import type { CharacterPattern } from "../../lib/character-patterns";

const PENDING_PATTERN_KEY = "greenwall_pending_pattern";

export interface PendingPattern {
  char: string;
  pattern: CharacterPattern;
}

export async function savePendingPattern(pattern: PendingPattern): Promise<void> {
  await AsyncStorage.setItem(PENDING_PATTERN_KEY, JSON.stringify(pattern));
}

export async function readPendingPattern(): Promise<PendingPattern | null> {
  const value = await AsyncStorage.getItem(PENDING_PATTERN_KEY);
  const pendingPattern = parsePendingPattern(value);

  if (value && !pendingPattern) await AsyncStorage.removeItem(PENDING_PATTERN_KEY);
  return pendingPattern;
}

export async function clearPendingPattern(): Promise<void> {
  await AsyncStorage.removeItem(PENDING_PATTERN_KEY);
}

export function parsePendingPattern(value: string | null): PendingPattern | null {
  if (!value) return null;

  try {
    const parsed: unknown = JSON.parse(value);
    return isPendingPattern(parsed) ? parsed : null;
  } catch {
    return null;
  }
}

function isPendingPattern(value: unknown): value is PendingPattern {
  if (!value || typeof value !== "object") return false;

  const candidate = value as Record<string, unknown>;
  return typeof candidate.char === "string" && isCharacterPattern(candidate.pattern);
}

function isCharacterPattern(value: unknown): value is CharacterPattern {
  return (
    Array.isArray(value) &&
    value.length > 0 &&
    value.length <= 7 &&
    value.every(
      (row) =>
        Array.isArray(row) &&
        row.length > 0 &&
        row.every((cell) => cell === 0 || cell === 1),
    )
  );
}
