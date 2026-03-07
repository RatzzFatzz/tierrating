import { test, expect } from "@playwright/test";

test.describe("Login page", () => {
	test("displays login form", async ({ page }) => {
		await page.goto("/login");
		await expect(page.getByText("Login")).toBeVisible();
		await expect(page.getByLabel(/username/i)).toBeVisible();
		await expect(page.getByLabel(/password/i)).toBeVisible();
		await expect(page.getByRole("button", { name: /sign in/i })).toBeVisible();
	});

	test("has link to signup page", async ({ page }) => {
		await page.goto("/login");
		const signupLink = page.getByRole("link", { name: /sign up/i });
		await expect(signupLink).toBeVisible();
		await signupLink.click();
		await expect(page).toHaveURL(/\/signup/);
	});
});
