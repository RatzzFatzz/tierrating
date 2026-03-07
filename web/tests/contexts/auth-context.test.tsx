import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AuthProvider, useAuth } from "@/components/contexts/auth-context";

vi.mock("@/components/auth/jwt-decoder", () => import("../mocks/jwt-decoder"));
vi.mock("@/components/api/user-api", () => ({
  refreshToken: vi.fn().mockResolvedValue({ data: { token: "refreshed-token" } }),
}));
vi.mock("next/navigation", () => ({
  useRouter: () => ({
    push: vi.fn(),
  }),
}));

function TestComponent() {
  const { login, logout, isAuthenticated, user, token } = useAuth();

  return (
    <div>
      <span data-testid="auth-status">
        {isAuthenticated ? "authenticated" : "not-authenticated"}
      </span>
      <span data-testid="user">{user || "no-user"}</span>
      <span data-testid="token">{token || "no-token"}</span>
      <button onClick={() => login("test-jwt-token")} data-testid="login-btn">
        Login
      </button>
      <button onClick={logout} data-testid="logout-btn">
        Logout
      </button>
    </div>
  );
}

describe("AuthContext", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it("starts unauthenticated", () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId("auth-status")).toHaveTextContent("not-authenticated");
    expect(screen.getByTestId("user")).toHaveTextContent("no-user");
    expect(screen.getByTestId("token")).toHaveTextContent("no-token");
  });

  it("login updates auth state and stores token", async () => {
    const user = userEvent.setup();
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await user.click(screen.getByTestId("login-btn"));

    await waitFor(() => {
      expect(screen.getByTestId("auth-status")).toHaveTextContent("authenticated");
    });

    expect(localStorage.setItem).toHaveBeenCalledWith("authToken", "test-jwt-token");
  });

  it("logout clears auth state and removes token", async () => {
    const user = userEvent.setup();
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await user.click(screen.getByTestId("login-btn"));
    await waitFor(() => {
      expect(screen.getByTestId("auth-status")).toHaveTextContent("authenticated");
    });

    await user.click(screen.getByTestId("logout-btn"));

    await waitFor(() => {
      expect(screen.getByTestId("auth-status")).toHaveTextContent("not-authenticated");
    });

    expect(localStorage.removeItem).toHaveBeenCalledWith("authToken");
  });

  it("restores auth state from localStorage on mount", async () => {
    const storedToken = "stored-jwt-token";
    vi.mocked(localStorage.getItem).mockReturnValue(storedToken);

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId("auth-status")).toHaveTextContent("authenticated");
    });

    expect(localStorage.getItem).toHaveBeenCalledWith("authToken");
  });
});
