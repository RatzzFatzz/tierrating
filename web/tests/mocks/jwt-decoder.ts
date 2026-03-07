import { vi } from "vitest";

export const extractJwtData = vi.fn((token: string) => {
  if (!token || token === "invalid-token") {
    return null;
  }

  return {
    username: "test-user",
    isExpired: false,
    expiration: new Date(Date.now() + 3600000), // 1 hour from now
  };
});
