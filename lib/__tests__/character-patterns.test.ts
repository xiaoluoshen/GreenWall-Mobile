import { describe, it, expect } from "vitest";
import {
  UPPERCASE_PATTERNS,
  LOWERCASE_PATTERNS,
  NUMBER_PATTERNS,
  SYMBOL_PATTERNS,
  ALL_PATTERNS,
} from "../character-patterns";

describe("Character Patterns", () => {
  it("should have all 26 uppercase letters", () => {
    const keys = Object.keys(UPPERCASE_PATTERNS);
    expect(keys.length).toBe(26);
    for (let i = 65; i <= 90; i++) {
      expect(keys).toContain(String.fromCharCode(i));
    }
  });

  it("should have all 26 lowercase letters", () => {
    const keys = Object.keys(LOWERCASE_PATTERNS);
    expect(keys.length).toBe(26);
    for (let i = 97; i <= 122; i++) {
      expect(keys).toContain(String.fromCharCode(i));
    }
  });

  it("should have all 10 digits", () => {
    const keys = Object.keys(NUMBER_PATTERNS);
    expect(keys.length).toBe(10);
    for (let i = 0; i <= 9; i++) {
      expect(keys).toContain(String(i));
    }
  });

  it("should have symbol patterns", () => {
    const keys = Object.keys(SYMBOL_PATTERNS);
    expect(keys.length).toBeGreaterThan(0);
  });

  it("all patterns should have exactly 7 rows", () => {
    for (const [category, patterns] of Object.entries(ALL_PATTERNS)) {
      for (const [char, pattern] of Object.entries(patterns)) {
        expect(pattern.length).toBe(7);
      }
    }
  });

  it("all rows in a pattern should have consistent width", () => {
    for (const [category, patterns] of Object.entries(ALL_PATTERNS)) {
      for (const [char, pattern] of Object.entries(patterns)) {
        const width = pattern[0].length;
        for (const row of pattern) {
          expect(row.length).toBe(width);
        }
      }
    }
  });

  it("pattern values should be 0 or 1", () => {
    for (const [category, patterns] of Object.entries(ALL_PATTERNS)) {
      for (const [char, pattern] of Object.entries(patterns)) {
        for (const row of pattern) {
          for (const cell of row) {
            expect([0, 1]).toContain(cell);
          }
        }
      }
    }
  });
});
