import { test, expect } from "@playwright/test";

test.describe("Signup page", () => {
	test("can navigate to signup from login", async ({ page }) => {
		await page.goto("/login");
		const signupLink = page.getByRole("link", { name: /sign up/i });
		await expect(signupLink).toBeVisible();
		await signupLink.click();
		await expect(page).toHaveURL(/\/signup/);
	});

	test("signup form has required fields", async ({ page }) => {
		// Navigate via login to avoid hydration issues with direct navigation
		await page.goto("/login");
		await page.getByRole("link", { name: /sign up/i }).click();
		await expect(page).toHaveURL(/\/signup/);

		// Verify signup-specific field (E-Mail) is present
		await expect(page.getByLabel(/e-?mail/i)).toBeVisible({ timeout: 10000 });
		await expect(page.getByLabel(/username/i)).toBeVisible();
		await expect(page.getByRole("button", { name: /create account/i })).toBeVisible();
	});

	test("can navigate back to login from signup", async ({ page }) => {
		await page.goto("/login");
		await page.getByRole("link", { name: /sign up/i }).click();
		await expect(page).toHaveURL(/\/signup/);

		const loginLink = page.getByRole("link", { name: /sign in/i });
		await expect(loginLink).toBeVisible();
		await loginLink.click();
		await expect(page).toHaveURL(/\/login/);
	});
});
