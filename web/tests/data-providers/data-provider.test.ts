import { describe, it, expect } from "vitest";
import { getProviderByName } from "@/components/data-providers/data-provider";

describe("getProviderByName", () => {
  it("returns AniList anime provider for 'anilist-anime'", () => {
    const provider = getProviderByName("anilist-anime");
    expect(provider.getServiceName()).toBe("anilist");
    expect(provider.getTypeName()).toBe("anime");
  });

  it("returns AniList manga provider for 'anilist-manga'", () => {
    const provider = getProviderByName("anilist-manga");
    expect(provider.getServiceName()).toBe("anilist");
    expect(provider.getTypeName()).toBe("manga");
  });

  it("returns Trakt movies provider for 'trakt-movies'", () => {
    const provider = getProviderByName("trakt-movies");
    expect(provider.getServiceName()).toBe("trakt");
    expect(provider.getTypeName()).toBe("movies");
  });

  it("returns Trakt TV shows provider for 'trakt-tvshows'", () => {
    const provider = getProviderByName("trakt-tvshows");
    expect(provider.getServiceName()).toBe("trakt");
    expect(provider.getTypeName()).toBe("tvshows");
  });

  it("returns Trakt TV shows seasons provider for 'trakt-tvshows-seasons'", () => {
    const provider = getProviderByName("trakt-tvshows-seasons");
    expect(provider.getServiceName()).toBe("trakt");
    expect(provider.getTypeName()).toBe("tvshows-seasons");
  });

  it("throws error for invalid provider name", () => {
    expect(() => getProviderByName("invalid-provider")).toThrow("Invalid provider: invalid-provider");
  });

  it("throws error for empty provider name", () => {
    expect(() => getProviderByName("")).toThrow("Invalid provider: ");
  });
});
