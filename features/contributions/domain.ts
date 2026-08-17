export type ContributionLevel = 0 | 1 | 3 | 6 | 9;

export interface ContributionDay {
  date: string;
  count: number;
  weekday: number;
  week: number;
}

export type ContributionMap = Record<string, number>;

export const CONTRIBUTION_LEVELS: readonly ContributionLevel[] = [0, 1, 3, 6, 9];

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

export function isContributionLevel(value: number): value is ContributionLevel {
  return CONTRIBUTION_LEVELS.includes(value as ContributionLevel);
}

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

export function applyContributionUpdates(
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

export function createAllGreenContributions(
  days: ContributionDay[],
  year: number,
  intensity: ContributionLevel,
  today = new Date(),
): ContributionMap {
  const contributions: ContributionMap = {};

  for (const day of days) {
    if (!isContributionDateInYear(day.date, year)) continue;

    const [dayYear, month, dayOfMonth] = day.date.split("-").map(Number);
    const calendarDate = new Date(dayYear, month - 1, dayOfMonth);
    if (calendarDate <= today) contributions[day.date] = intensity;
  }

  return contributions;
}
