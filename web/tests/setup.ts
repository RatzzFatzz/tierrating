import "@testing-library/jest-dom";
import { vi, beforeEach, afterEach } from "vitest";

// Mock localStorage for JWT token storage in AuthContext
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
  length: 0,
  key: vi.fn(),
};

vi.stubGlobal("localStorage", localStorageMock);

// Mock fetch globally for API calls in server actions
global.fetch = vi.fn();

// Reset mocks between tests to prevent state leakage
beforeEach(() => {
  vi.clearAllMocks();
  localStorageMock.getItem.mockReturnValue(null);
  localStorageMock.setItem.mockReturnValue(undefined);
  localStorageMock.removeItem.mockReturnValue(undefined);
  localStorageMock.clear.mockReturnValue(undefined);
});

// Clean up after each test to ensure test isolation
afterEach(() => {
  vi.restoreAllMocks();
});
