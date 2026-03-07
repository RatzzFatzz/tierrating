import { describe, it, expect } from "vitest";
import { getProviderByName } from "@/components/data-providers/data-provider";

describe("getProviderByName", () => {
	describe("valid providers", () => {
		it.each([
			["anilist-anime", "anilist", "anime"],
			["anilist-manga", "anilist", "manga"],
			["trakt-movies", "trakt", "movies"],
			["trakt-tvshows", "trakt", "tvshows"],
			["trakt-tvshows-seasons", "trakt", "tvshows-seasons"],
		])("returns correct provider for '%s'", (providerName, expectedService, expectedType) => {
			const provider = getProviderByName(providerName);
			expect(provider.getServiceName()).toBe(expectedService);
			expect(provider.getTypeName()).toBe(expectedType);
		});
	});

	describe("invalid providers", () => {
		it.each([
			["invalid-provider", "Invalid provider: invalid-provider"],
			["", "Invalid provider: "],
		])("throws error for '%s'", (providerName, expectedError) => {
			expect(() => getProviderByName(providerName)).toThrow(expectedError);
		});
	});
});
