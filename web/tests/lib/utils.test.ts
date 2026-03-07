import { describe, it, expect } from "vitest";
import { sortByName } from "@/components/tierlist/tier-mapper";

describe("sortByName", () => {
	it.each([
		// [titleA, titleB, expectedResult, description]
		["Alpha", "Beta", -1, "first item comes before second alphabetically"],
		["Zeta", "Alpha", 1, "first item comes after second alphabetically"],
		["Same", "Same", 0, "items have same name"],
		["alpha", "BETA", -1, "case insensitive comparison"],
		["!First", "Second", -1, "special characters"],
	])("returns %i when %s vs %s (%s)", (titleA, titleB, expected, _description) => {
		const a = { title: titleA };
		const b = { title: titleB };

		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		expect(sortByName(a as any, b as any)).toBe(expected);
	});
});
