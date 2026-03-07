import { describe, it, expect, vi, beforeEach } from "vitest";
import { AbstractDataProvider } from "@/components/data-providers/abstract-data-provider";
import { Tier, TierlistEntry } from "@/components/model/types";

vi.mock("@/components/api/tier-api", () => ({
	fetchTiers: vi.fn(),
}));

vi.mock("@/components/api/data-api", () => ({
	fetchData: vi.fn(),
	pullData: vi.fn(),
	updateData: vi.fn(),
}));

import { fetchTiers } from "@/components/api/tier-api";
import { fetchData, pullData, updateData } from "@/components/api/data-api";

const mockFetchTiers = vi.mocked(fetchTiers);
const mockFetchData = vi.mocked(fetchData);
const mockPullData = vi.mocked(pullData);
const mockUpdateData = vi.mocked(updateData);

class TestDataProvider extends AbstractDataProvider {
	getServiceName(): string {
		return "test-service";
	}

	getTypeName(): string {
		return "test-type";
	}
}

function createMockTier(overrides: Partial<Tier> = {}): Tier {
	return {
		id: "tier-1",
		name: "S",
		score: 90,
		adjustedScore: 95,
		color: "#ff0000",
		...overrides,
	};
}

function createMockEntry(overrides: Partial<TierlistEntry> = {}): TierlistEntry {
	return {
		id: "entry-1",
		score: 95,
		title: "Test Entry",
		cover: "https://example.com/cover.jpg",
		tier: createMockTier(),
		index: 0,
		...overrides,
	};
}

describe("AbstractDataProvider", () => {
	let provider: TestDataProvider;
	const mockLogout = vi.fn();

	beforeEach(() => {
		vi.clearAllMocks();
		provider = new TestDataProvider();
	});

	describe("fetchData", () => {
		it("should return data on successful fetch", async () => {
			const mockEntries = [createMockEntry()];
			mockFetchData.mockResolvedValue({
				status: 200,
				data: mockEntries,
			});

			const result = await provider.fetchData("token", "user", mockLogout);

			expect(result).toEqual(mockEntries);
			expect(mockFetchData).toHaveBeenCalledWith("token", "user", "test-service", "test-type");
		});

		it.each([
			[401, "Session expired"],
			[403, "Session expired"],
		])("should call logout and throw on %i", async (status, expectedError) => {
			mockFetchData.mockResolvedValue({ status });

			await expect(provider.fetchData("token", "user", mockLogout)).rejects.toThrow(expectedError);
			expect(mockLogout).toHaveBeenCalled();
		});

		it("should throw on 404", async () => {
			mockFetchData.mockResolvedValue({ status: 404 });

			await expect(provider.fetchData("token", "user", mockLogout)).rejects.toThrow("User not found");
		});

		it("should throw on API error", async () => {
			mockFetchData.mockResolvedValue({
				status: 500,
				error: "Server error",
			});

			await expect(provider.fetchData("token", "user", mockLogout)).rejects.toThrow("API error: 500");
		});

		it("should throw on faulty response", async () => {
			mockFetchData.mockResolvedValue({ status: 200 });

			await expect(provider.fetchData("token", "user", mockLogout)).rejects.toThrow("Faulty response");
		});
	});

	describe("fetchTierlist", () => {
		it("should return tiers on successful fetch", async () => {
			const mockTiers = [createMockTier()];
			mockFetchTiers.mockResolvedValue({
				status: 200,
				data: mockTiers,
			});

			const result = await provider.fetchTierlist("token", "user", mockLogout);

			expect(result).toEqual(mockTiers);
			expect(mockFetchTiers).toHaveBeenCalledWith("token", "user", "test-service", "test-type");
		});

		it.each([
			[401, "Session expired"],
			[403, "Session expired"],
		])("should call logout and throw on %i", async (status, expectedError) => {
			mockFetchTiers.mockResolvedValue({ status });

			await expect(provider.fetchTierlist("token", "user", mockLogout)).rejects.toThrow(expectedError);
			expect(mockLogout).toHaveBeenCalled();
		});

		it("should return default tiers on 404", async () => {
			mockFetchTiers.mockResolvedValue({ status: 404 });

			const result = await provider.fetchTierlist("token", "user", mockLogout);

			expect(result).toBeDefined();
			expect(result.length).toBeGreaterThan(0);
		});

		it("should throw on API error", async () => {
			mockFetchTiers.mockResolvedValue({
				status: 500,
				error: "Server error",
			});

			await expect(provider.fetchTierlist("token", "user", mockLogout)).rejects.toThrow("API error status: 500");
		});

		it("should throw on faulty response", async () => {
			mockFetchTiers.mockResolvedValue({ status: 200 });

			await expect(provider.fetchTierlist("token", "user", mockLogout)).rejects.toThrow("Faulty response");
		});
	});

	describe("updateData", () => {
		it("should succeed on 200 response", async () => {
			mockUpdateData.mockResolvedValue({ status: 200 });

			await provider.updateData("entry-1", 95, "token", "user", mockLogout);

			expect(mockUpdateData).toHaveBeenCalledWith("entry-1", 95, "test-service", "test-type", "token", "user");
		});

		it.each([
			[401, "Session expired or unauthorized"],
			[403, "Session expired or unauthorized"],
		])("should call logout and throw on %i", async (status, expectedError) => {
			mockUpdateData.mockResolvedValue({ status });

			await expect(provider.updateData("entry-1", 95, "token", "user", mockLogout)).rejects.toThrow(expectedError);
			expect(mockLogout).toHaveBeenCalled();
		});

		it("should throw on non-200 with message", async () => {
			mockUpdateData.mockResolvedValue({
				status: 400,
				data: { message: "Bad request" },
			});

			await expect(provider.updateData("entry-1", 95, "token", "user", mockLogout)).rejects.toThrow("Bad request");
		});

		it("should throw on non-200 without message", async () => {
			mockUpdateData.mockResolvedValue({ status: 500 });

			await expect(provider.updateData("entry-1", 95, "token", "user", mockLogout)).rejects.toThrow("API error: 500");
		});
	});

	describe("pullData", () => {
		it("should succeed on 200 response", async () => {
			mockPullData.mockResolvedValue({ status: 200 });

			await provider.pullData("token", "user", mockLogout);

			expect(mockPullData).toHaveBeenCalledWith("token", "user", "test-service", "test-type");
		});

		it.each([
			[401, "Session expired"],
			[403, "Session expired"],
		])("should call logout and throw on %i", async (status, expectedError) => {
			mockPullData.mockResolvedValue({ status });

			await expect(provider.pullData("token", "user", mockLogout)).rejects.toThrow(expectedError);
			expect(mockLogout).toHaveBeenCalled();
		});

		it("should throw on non-200 with message", async () => {
			mockPullData.mockResolvedValue({
				status: 400,
				data: { message: "Bad request" },
			});

			await expect(provider.pullData("token", "user", mockLogout)).rejects.toThrow("Bad request");
		});

		it("should throw on non-200 without message", async () => {
			mockPullData.mockResolvedValue({ status: 500 });

			await expect(provider.pullData("token", "user", mockLogout)).rejects.toThrow("API error: 500");
		});
	});
});
