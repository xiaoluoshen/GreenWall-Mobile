import { describe, it, expect } from "vitest";
import { interpolate } from "../i18n";

describe("interpolate", () => {
  it("should replace single variable", () => {
    expect(interpolate("Hello {{name}}", { name: "World" })).toBe(
      "Hello World"
    );
  });

  it("should replace multiple variables", () => {
    expect(
      interpolate("{{count}} contributions in {{year}}", {
        count: 42,
        year: 2026,
      })
    ).toBe("42 contributions in 2026");
  });

  it("should replace all occurrences of same variable", () => {
    expect(
      interpolate("{{x}} and {{x}}", { x: "hello" })
    ).toBe("hello and hello");
  });

  it("should handle no variables", () => {
    expect(interpolate("No variables here", {})).toBe("No variables here");
  });

  it("should handle numeric values", () => {
    expect(interpolate("Count: {{n}}", { n: 100 })).toBe("Count: 100");
  });
});
