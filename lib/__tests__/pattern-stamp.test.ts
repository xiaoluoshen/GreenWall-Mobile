import { describe, expect, it } from "vitest";
import type { ContributionDay } from "../contribution-store";
import { createPatternStamp, parsePendingPattern } from "../pattern-stamp";

const days: ContributionDay[] = Array.from({ length: 21 }, (_, index) => {
  const week = Math.floor(index / 7);
  const weekday = index % 7;
  return {
    date: `2026-01-${String(index + 1).padStart(2, "0")}`,
    count: 0,
    week,
    weekday,
  };
});

describe("parsePendingPattern", () => {
  it("accepts a valid persisted character pattern", () => {
    expect(
      parsePendingPattern('{"char":"A","pattern":[[1,0],[0,1]]}'),
    ).toEqual({ char: "A", pattern: [[1, 0], [0, 1]] });
  });

  it("rejects malformed and invalid persisted values", () => {
    expect(parsePendingPattern("not-json")).toBeNull();
    expect(parsePendingPattern('{"char":"A","pattern":[[2]]}')).toBeNull();
    expect(parsePendingPattern(null)).toBeNull();
  });
});

describe("createPatternStamp", () => {
  it("centers a pattern and clears blank cells inside its footprint", () => {
    const stamp = createPatternStamp(
      days,
      [
        [1, 0],
        [0, 1],
      ],
      6,
    );

    expect(stamp).toEqual({
      "2026-01-01": 6,
      "2026-01-08": 0,
      "2026-01-02": 0,
      "2026-01-09": 6,
    });
  });

  it("returns no cells for an empty day grid", () => {
    expect(createPatternStamp([], [[1]])).toEqual({});
  });
});
