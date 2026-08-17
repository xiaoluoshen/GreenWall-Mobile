import type { CharacterPattern } from "./character-patterns";
import type { ContributionDay, ContributionLevel } from "../features/contributions";

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
