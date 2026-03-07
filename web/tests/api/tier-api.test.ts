import { describe, it, expect, vi } from "vitest";
import { fetchTiers, updateTiers } from "@/components/api/tier-api";
import { Tier } from "@/components/model/types";

const mockFetch = vi.mocked(global.fetch);

function createMockTier(overrides: Partial<Tier> = {}): Tier {
	return {
		id: "tier-1",
		name: "S",
		score: 10,
		adjustedScore: 10,
		color: "#FF7F7F",
		...overrides,
	};
}

describe("fetchTiers", () => {
	it("throws when token is null", async () => {
		await expect(fetchTiers(null, "testuser", "anilist", "ANIME")).rejects.toThrow("No authentication token");
		expect(mockFetch).not.toHaveBeenCalled();
	});

	it("returns tiers and status on success", async () => {
		const mockTiers = [createMockTier(), createMockTier({ id: "tier-2", name: "A", score: 8 })];
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue(mockTiers),
			status: 200,
		} as unknown as Response);

		const result = await fetchTiers("my-token", "testuser", "anilist", "ANIME");

		expect(result).toEqual({ data: mockTiers, status: 200 });
		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining("/tiers/testuser/anilist/anime"),
			expect.objectContaining({
				method: "GET",
				headers: expect.objectContaining({
					Authorization: "Bearer my-token",
					"Content-Type": "application/json",
				}),
			})
		);
	});

	it("lowercases the type in the URL", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue([]),
			status: 200,
		} as unknown as Response);

		await fetchTiers("token", "user", "trakt", "MOVIES");

		expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/tiers/user/trakt/movies"), expect.any(Object));
	});

	it("returns null data when json parsing fails", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockRejectedValue(new Error("JSON parse error")),
			status: 200,
		} as unknown as Response);

		const result = await fetchTiers("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ data: null, status: 200 });
	});

	it("returns 500 error response on network failure", async () => {
		mockFetch.mockRejectedValueOnce(new Error("Network error"));

		const result = await fetchTiers("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ error: "Server unavailable", status: 500 });
	});

	it("returns non-200 status with data on error responses", async () => {
		const errorBody = { message: "Not found" };
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue(errorBody),
			status: 404,
		} as unknown as Response);

		const result = await fetchTiers("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ data: errorBody, status: 404 });
	});
});

describe("updateTiers", () => {
	it("throws when token is null", async () => {
		await expect(updateTiers(null, "testuser", "anilist", "anime", [createMockTier()])).rejects.toThrow("No authentication token");
		expect(mockFetch).not.toHaveBeenCalled();
	});

	it("returns data and status on success", async () => {
		const mockTiers = [createMockTier(), createMockTier({ id: "tier-2", name: "A", score: 8 })];
		const mockResponse = { message: "Tiers updated" };
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue(mockResponse),
			status: 200,
		} as unknown as Response);

		const result = await updateTiers("my-token", "testuser", "anilist", "anime", mockTiers);

		expect(result).toEqual({ data: mockResponse, status: 200 });
		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining("/tiers/testuser/anilist/anime"),
			expect.objectContaining({
				method: "POST",
				headers: expect.objectContaining({
					Authorization: "Bearer my-token",
					"Content-Type": "application/json",
				}),
				body: JSON.stringify(mockTiers),
			})
		);
	});

	it("returns null data when json parsing fails", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockRejectedValue(new Error("JSON parse error")),
			status: 200,
		} as unknown as Response);

		const result = await updateTiers("my-token", "testuser", "anilist", "anime", [createMockTier()]);

		expect(result).toEqual({ data: null, status: 200 });
	});

	it("returns 500 error response on network failure", async () => {
		mockFetch.mockRejectedValueOnce(new Error("Network error"));

		const result = await updateTiers("my-token", "testuser", "anilist", "anime", [createMockTier()]);

		expect(result).toEqual({ error: "Server unavailable", status: 500 });
	});

	it("serializes tiers array correctly in request body", async () => {
		const tiers = [
			createMockTier({ id: "s", name: "S", score: 10, color: "#FF0000" }),
			createMockTier({ id: "a", name: "A", score: 8, color: "#00FF00" }),
		];
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue({}),
			status: 200,
		} as unknown as Response);

		await updateTiers("token", "user", "trakt", "tvshows", tiers);

		expect(mockFetch).toHaveBeenCalledWith(
			expect.any(String),
			expect.objectContaining({
				body: JSON.stringify(tiers),
			})
		);
	});
});
