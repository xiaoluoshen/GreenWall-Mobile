import { describe, it, expect } from "vitest";
import {
  getYearDays,
  getContributionColor,
  formatDate,
  formatDateDisplay,
  isContributionDateInYear,
  sanitizeContributionMap,
  CONTRIBUTION_COLORS,
} from "../contribution-store";

describe("getYearDays", () => {
  it("should return days for a given year", () => {
    const days = getYearDays(2026);
    expect(days.length).toBeGreaterThan(0);
    // Should cover all days of the year
    const yearDays = days.filter((d) => d.date.startsWith("2026-"));
    expect(yearDays.length).toBe(365);
  });

  it("should handle leap year", () => {
    const days = getYearDays(2024);
    const yearDays = days.filter((d) => d.date.startsWith("2024-"));
    expect(yearDays.length).toBe(366);
  });

  it("should have weekday 0-6", () => {
    const days = getYearDays(2026);
    for (const day of days) {
      expect(day.weekday).toBeGreaterThanOrEqual(0);
      expect(day.weekday).toBeLessThanOrEqual(6);
    }
  });

  it("should have incrementing weeks", () => {
    const days = getYearDays(2026);
    let lastWeek = -1;
    for (const day of days) {
      expect(day.week).toBeGreaterThanOrEqual(lastWeek);
      if (day.week > lastWeek) lastWeek = day.week;
    }
  });
});

describe("getContributionColor", () => {
  it("should return level 0 color for count 0", () => {
    expect(getContributionColor(0, "light")).toBe(CONTRIBUTION_COLORS.light[0]);
    expect(getContributionColor(0, "dark")).toBe(CONTRIBUTION_COLORS.dark[0]);
  });

  it("should return level 1 color for count 1-2", () => {
    expect(getContributionColor(1, "light")).toBe(CONTRIBUTION_COLORS.light[1]);
    expect(getContributionColor(2, "light")).toBe(CONTRIBUTION_COLORS.light[1]);
  });

  it("should return level 3 color for count 3-5", () => {
    expect(getContributionColor(3, "light")).toBe(CONTRIBUTION_COLORS.light[3]);
    expect(getContributionColor(5, "light")).toBe(CONTRIBUTION_COLORS.light[3]);
  });

  it("should return level 6 color for count 6-8", () => {
    expect(getContributionColor(6, "light")).toBe(CONTRIBUTION_COLORS.light[6]);
    expect(getContributionColor(8, "light")).toBe(CONTRIBUTION_COLORS.light[6]);
  });

  it("should return level 9 color for count >= 9", () => {
    expect(getContributionColor(9, "light")).toBe(CONTRIBUTION_COLORS.light[9]);
    expect(getContributionColor(15, "light")).toBe(CONTRIBUTION_COLORS.light[9]);
  });
});

describe("formatDate", () => {
  it("should format date as YYYY-MM-DD", () => {
    const date = new Date(2026, 0, 1);
    expect(formatDate(date)).toBe("2026-01-01");
  });

  it("should pad month and day", () => {
    const date = new Date(2026, 2, 5);
    expect(formatDate(date)).toBe("2026-03-05");
  });
});

describe("formatDateDisplay", () => {
  it("should format date string as YYYY/MM/DD", () => {
    expect(formatDateDisplay("2026-01-15")).toBe("2026/01/15");
  });
});

describe("isContributionDateInYear", () => {
  it("accepts valid dates in the selected year", () => {
    expect(isContributionDateInYear("2024-02-29", 2024)).toBe(true);
    expect(isContributionDateInYear("2026-12-31", 2026)).toBe(true);
  });

  it("rejects dates outside the selected year and invalid calendar dates", () => {
    expect(isContributionDateInYear("2025-12-31", 2026)).toBe(false);
    expect(isContributionDateInYear("2026-02-29", 2026)).toBe(false);
    expect(isContributionDateInYear("not-a-date", 2026)).toBe(false);
  });
});

describe("sanitizeContributionMap", () => {
  it("keeps only valid positive contribution levels in the selected year", () => {
    const result = sanitizeContributionMap(
      {
        "2026-01-01": 1,
        "2026-01-02": 9,
        "2025-12-31": 6,
        "2026-02-29": 3,
        "2026-01-03": 2,
        "2026-01-04": 0,
        "2026-01-05": "6",
      },
      2026,
    );

    expect(result).toEqual({
      "2026-01-01": 1,
      "2026-01-02": 9,
    });
  });

  it("returns an empty map for malformed persisted values", () => {
    expect(sanitizeContributionMap(null, 2026)).toEqual({});
    expect(sanitizeContributionMap(["2026-01-01"], 2026)).toEqual({});
  });
});
