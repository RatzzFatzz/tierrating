import { describe, it, expect } from "vitest";
import { assignTiersAndGroupEntriesByTier, groupBySingle } from "@/components/tierlist/tier-mapper";
import { Tier, TierlistEntry } from "@/components/model/types";

function createTier(overrides: Partial<Tier> = {}): Tier {
	return {
		id: "tier-s",
		name: "S",
		score: 10,
		adjustedScore: 10,
		color: "#FF7F7F",
		...overrides,
	};
}

function createEntry(overrides: Partial<TierlistEntry> = {}): TierlistEntry {
	return {
		id: "entry-1",
		score: 10,
		title: "Test Entry",
		cover: "https://example.com/cover.jpg",
		tier: createTier(),
		index: 0,
		...overrides,
	};
}

describe("assignTiersAndGroupEntriesByTier", () => {
	it("returns an empty map when tiers is empty", () => {
		const result = assignTiersAndGroupEntriesByTier([], [createEntry()]);
		expect(result.size).toBe(0);
	});

	it("returns map with empty arrays when items is empty", () => {
		const tiers = [createTier({ id: "s", score: 10 }), createTier({ id: "a", score: 8 })];
		const result = assignTiersAndGroupEntriesByTier(tiers, []);

		expect(result.size).toBe(2);
		expect(result.get("s")).toEqual([]);
		expect(result.get("a")).toEqual([]);
	});

	it("assigns a single item to the first matching tier", () => {
		const sTier = createTier({ id: "s", name: "S", score: 10 });
		const entry = createEntry({ id: "e1", score: 10, title: "Alpha" });

		const result = assignTiersAndGroupEntriesByTier([sTier], [entry]);

		expect(result.get("s")).toHaveLength(1);
		expect(result.get("s")![0].title).toBe("Alpha");
	});

	it("assigns items to different tiers based on score", () => {
		const sTier = createTier({ id: "s", name: "S", score: 10 });
		const aTier = createTier({ id: "a", name: "A", score: 8 });
		const entry1 = createEntry({ id: "e1", score: 10, title: "S-Item" });
		const entry2 = createEntry({ id: "e2", score: 8, title: "A-Item" });

		const result = assignTiersAndGroupEntriesByTier([sTier, aTier], [entry1, entry2]);

		expect(result.get("s")).toHaveLength(1);
		expect(result.get("a")).toHaveLength(1);
		expect(result.get("s")![0].title).toBe("S-Item");
		expect(result.get("a")![0].title).toBe("A-Item");
	});

	it("assigns multiple items to the same tier when scores match", () => {
		const sTier = createTier({ id: "s", score: 10 });
		const entry1 = createEntry({ id: "e1", score: 10, title: "Zebra" });
		const entry2 = createEntry({ id: "e2", score: 10, title: "Alpha" });

		const result = assignTiersAndGroupEntriesByTier([sTier], [entry1, entry2]);
		const sEntries = result.get("s")!;

		expect(sEntries).toHaveLength(2);
		expect(sEntries[0].title).toBe("Alpha");
		expect(sEntries[1].title).toBe("Zebra");
	});

	it("skips items whose score is below all tier thresholds", () => {
		const sTier = createTier({ id: "s", score: 10 });
		const entry = createEntry({ id: "e1", score: 2 });

		const result = assignTiersAndGroupEntriesByTier([sTier], [entry]);

		expect(result.get("s")).toHaveLength(0);
	});

	it("advances to the next tier when item score falls below current tier", () => {
		const sTier = createTier({ id: "s", score: 10 });
		const aTier = createTier({ id: "a", score: 8 });
		const bTier = createTier({ id: "b", score: 6 });
		const entry = createEntry({ id: "e1", score: 7, title: "B-Item" });

		const result = assignTiersAndGroupEntriesByTier([sTier, aTier, bTier], [entry]);

		expect(result.get("s")).toHaveLength(0);
		expect(result.get("a")).toHaveLength(0);
		expect(result.get("b")).toHaveLength(1);
		expect(result.get("b")![0].title).toBe("B-Item");
	});

	it("sets the tier reference on each assigned entry", () => {
		const sTier = createTier({ id: "s", score: 10, name: "S" });
		const entry = createEntry({ id: "e1", score: 10 });

		const result = assignTiersAndGroupEntriesByTier([sTier], [entry]);
		const assigned = result.get("s")![0];

		expect(assigned.tier).toBe(sTier);
	});

	it("resets position index when moving to the next tier", () => {
		const sTier = createTier({ id: "s", score: 10 });
		const aTier = createTier({ id: "a", score: 8 });
		const sEntry = createEntry({ id: "e1", score: 10, title: "S-Item" });
		const aEntry = createEntry({ id: "e2", score: 8, title: "A-Item" });

		const result = assignTiersAndGroupEntriesByTier([sTier, aTier], [sEntry, aEntry]);

		expect(result.get("s")![0].index).toBe(0);
		expect(result.get("a")![0].index).toBe(0);
	});

	it("sorts entries within a tier alphabetically by title", () => {
		const sTier = createTier({ id: "s", score: 10 });
		const entries = [
			createEntry({ id: "e3", score: 10, title: "Charlie" }),
			createEntry({ id: "e1", score: 10, title: "Alpha" }),
			createEntry({ id: "e2", score: 10, title: "Beta" }),
		];

		const result = assignTiersAndGroupEntriesByTier([sTier], entries);
		const sEntries = result.get("s")!;

		expect(sEntries.map((e) => e.title)).toEqual(["Alpha", "Beta", "Charlie"]);
	});

	it("handles the case where all items are assigned to the first tier", () => {
		const sTier = createTier({ id: "s", score: 1 });
		const entries = [
			createEntry({ id: "e1", score: 10, title: "High" }),
			createEntry({ id: "e2", score: 5, title: "Mid" }),
			createEntry({ id: "e3", score: 2, title: "Low" }),
		];

		const result = assignTiersAndGroupEntriesByTier([sTier], entries);

		expect(result.get("s")).toHaveLength(3);
	});
});

describe("groupBySingle", () => {
	it("returns an empty map for an empty array", () => {
		const result = groupBySingle([], (x: string) => x);
		expect(result.size).toBe(0);
	});

	it("works with single-element arrays", () => {
		const arr = [{ id: "x", value: 42 }];
		const result = groupBySingle(arr, (x) => x.id);

		expect(result.size).toBe(1);
		expect(result.get("x")).toEqual({ id: "x", value: 42 });
	});

	it.each([
		[
			"string keys",
			[
				{ id: "a", value: 1 },
				{ id: "b", value: 2 },
			],
			2,
			[
				["a", { id: "a", value: 1 }],
				["b", { id: "b", value: 2 }],
			],
		],
		[
			"numeric keys",
			[
				{ id: 1, name: "Alice" },
				{ id: 2, name: "Bob" },
			],
			2,
			[
				[1, { id: 1, name: "Alice" }],
				[2, { id: 2, name: "Bob" }],
			],
		],
		[
			"duplicate keys (last wins)",
			[
				{ id: "a", value: 1 },
				{ id: "a", value: 2 },
			],
			1,
			[["a", { id: "a", value: 2 }]],
		],
	])("maps elements by %s", (_description, arr, expectedSize, expectedEntries) => {
		const result = groupBySingle(arr, (x) => x.id);

		expect(result.size).toBe(expectedSize);
		for (const [key, value] of expectedEntries) {
			expect(result.get(key)).toEqual(value);
		}
	});
});
