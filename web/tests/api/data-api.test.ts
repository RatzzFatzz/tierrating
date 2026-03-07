import { describe, it, expect, vi } from "vitest";
import { fetchData, updateData, pullData } from "@/components/api/data-api";

const mockFetch = vi.mocked(global.fetch);

describe("fetchData", () => {
	it("throws when token is null", async () => {
		await expect(fetchData(null, "user", "anilist", "anime")).rejects.toThrow("No authentication token");
		expect(mockFetch).not.toHaveBeenCalled();
	});

	it("returns data and status on success", async () => {
		const mockData = [{ id: "1", title: "Test", score: 9 }];
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue(mockData),
			status: 200,
		} as unknown as Response);

		const result = await fetchData("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ data: mockData, status: 200 });
		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining("/data/fetch/testuser/anilist/anime"),
			expect.objectContaining({
				method: "GET",
				headers: expect.objectContaining({
					Authorization: "Bearer my-token",
					"Content-Type": "application/json",
				}),
			})
		);
	});

	it("returns null data when json parsing fails", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockRejectedValue(new Error("JSON parse error")),
			status: 200,
		} as unknown as Response);

		const result = await fetchData("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ data: null, status: 200 });
	});

	it("returns 500 error response on network failure", async () => {
		mockFetch.mockRejectedValueOnce(new Error("Network error"));

		const result = await fetchData("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ error: "Server unavailable", status: 500 });
	});

	it("returns correct status on non-200 response", async () => {
		const errorBody = { message: "Unauthorized" };
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue(errorBody),
			status: 401,
		} as unknown as Response);

		const result = await fetchData("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ data: errorBody, status: 401 });
	});
});

describe("updateData", () => {
	it("throws when token is null", async () => {
		await expect(updateData("entry-1", 9, "anilist", "anime", null, "testuser")).rejects.toThrow("No authentication token");
		expect(mockFetch).not.toHaveBeenCalled();
	});

	it("returns data and status on success", async () => {
		const mockResponse = { message: "Updated successfully" };
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue(mockResponse),
			status: 200,
		} as unknown as Response);

		const result = await updateData("entry-42", 8, "anilist", "anime", "my-token", "testuser");

		expect(result).toEqual({ data: mockResponse, status: 200 });
		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining("/data/update"),
			expect.objectContaining({
				method: "POST",
				headers: expect.objectContaining({
					Authorization: "Bearer my-token",
					"Content-Type": "application/json",
				}),
				body: JSON.stringify({
					id: "entry-42",
					score: 8,
					username: "testuser",
					service: "anilist",
					type: "anime",
				}),
			})
		);
	});

	it("returns null data when json parsing fails", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockRejectedValue(new Error("JSON parse error")),
			status: 200,
		} as unknown as Response);

		const result = await updateData("entry-1", 9, "anilist", "anime", "my-token", "testuser");

		expect(result).toEqual({ data: null, status: 200 });
	});

	it("returns 500 error response on network failure", async () => {
		mockFetch.mockRejectedValueOnce(new Error("Network error"));

		const result = await updateData("entry-1", 9, "anilist", "anime", "my-token", "testuser");

		expect(result).toEqual({ error: "Server unavailable", status: 500 });
	});

	it("passes all parameters correctly in request body", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue({}),
			status: 200,
		} as unknown as Response);

		await updateData("entry-xyz", 7, "trakt", "movies", "token-abc", "alice");

		expect(mockFetch).toHaveBeenCalledWith(
			expect.any(String),
			expect.objectContaining({
				body: JSON.stringify({
					id: "entry-xyz",
					score: 7,
					username: "alice",
					service: "trakt",
					type: "movies",
				}),
			})
		);
	});
});

describe("pullData", () => {
	it("throws when token is null", async () => {
		await expect(pullData(null, "testuser", "anilist", "anime")).rejects.toThrow("No authentication token");
		expect(mockFetch).not.toHaveBeenCalled();
	});

	it("returns data and status on success", async () => {
		const mockResponse = { message: "Pull completed" };
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue(mockResponse),
			status: 200,
		} as unknown as Response);

		const result = await pullData("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ data: mockResponse, status: 200 });
		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining("/data/pull/testuser/anilist/anime"),
			expect.objectContaining({
				method: "GET",
				headers: expect.objectContaining({
					Authorization: "Bearer my-token",
					"Content-Type": "application/json",
				}),
			})
		);
	});

	it("returns null data when json parsing fails", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockRejectedValue(new Error("JSON parse error")),
			status: 200,
		} as unknown as Response);

		const result = await pullData("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ data: null, status: 200 });
	});

	it("returns 500 error response on network failure", async () => {
		mockFetch.mockRejectedValueOnce(new Error("Network error"));

		const result = await pullData("my-token", "testuser", "anilist", "anime");

		expect(result).toEqual({ error: "Server unavailable", status: 500 });
	});

	it("includes username, service and type in URL path", async () => {
		mockFetch.mockResolvedValueOnce({
			json: vi.fn().mockResolvedValue({}),
			status: 200,
		} as unknown as Response);

		await pullData("token", "alice", "trakt", "tvshows");

		expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/data/pull/alice/trakt/tvshows"), expect.any(Object));
	});
});
