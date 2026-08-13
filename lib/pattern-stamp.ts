import type { CharacterPattern } from "./character-patterns";
import type { ContributionDay, ContributionLevel } from "./contribution-store";

export const PENDING_PATTERN_KEY = "greenwall_pending_pattern";

export type PendingPattern = {
  char: string;
  pattern: CharacterPattern;
};

/**
 * Parses a pattern payload persisted by the character picker.
 * Invalid or malformed data is ignored instead of reaching the drawing store.
 */
export function parsePendingPattern(value: string | null): PendingPattern | null {
  if (!value) return null;

  try {
    const parsed: unknown = JSON.parse(value);
    if (!isPendingPattern(parsed)) return null;
    return parsed;
  } catch {
    return null;
  }
}

/**
 * Converts a seven-row character matrix into a single undoable contribution update.
 * The character is centered horizontally in the calendar and blank cells clear the
 * corresponding area so a previously drawn pattern cannot bleed through.
 */
export function createPatternStamp(
  days: ContributionDay[],
  pattern: CharacterPattern,
  intensity: ContributionLevel = 9,
): Record<string, number> {
  if (!isCharacterPattern(pattern) || days.length === 0) return {};

  const maxWeek = Math.max(...days.map((day) => day.week));
  const patternWidth = Math.max(...pattern.map((row) => row.length));
  const startWeek = Math.max(0, Math.floor((maxWeek + 1 - patternWidth) / 2));
  const daysByGridPosition = new Map(
    days.map((day) => [`${day.week}:${day.weekday}`, day]),
  );
  const cells: Record<string, number> = {};

  for (let weekday = 0; weekday < pattern.length; weekday += 1) {
    for (let column = 0; column < patternWidth; column += 1) {
      const day = daysByGridPosition.get(`${startWeek + column}:${weekday}`);
      if (!day) continue;
      cells[day.date] = pattern[weekday]?.[column] ? intensity : 0;
    }
  }

  return cells;
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
