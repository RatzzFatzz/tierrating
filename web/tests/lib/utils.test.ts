import { describe, it, expect } from "vitest";
import { sortByName } from "@/components/tierlist/tier-mapper";

describe("sortByName", () => {
	it("should return -1 when first item comes before second alphabetically", () => {
		const a = { title: "Alpha" };
		const b = { title: "Beta" };

		expect(sortByName(a as any, b as any)).toBe(-1);
	});

	it("should return 1 when first item comes after second alphabetically", () => {
		const a = { title: "Zeta" };
		const b = { title: "Alpha" };

		expect(sortByName(a as any, b as any)).toBe(1);
	});

	it("should return 0 when items have same name", () => {
		const a = { title: "Same" };
		const b = { title: "Same" };

		expect(sortByName(a as any, b as any)).toBe(0);
	});

	it("should be case insensitive", () => {
		const a = { title: "alpha" };
		const b = { title: "BETA" };

		expect(sortByName(a as any, b as any)).toBe(-1);
	});

	it("should handle special characters", () => {
		const a = { title: "!First" };
		const b = { title: "Second" };

		expect(sortByName(a as any, b as any)).toBe(-1);
	});
});
