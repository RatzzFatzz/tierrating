import { NextRequest, NextResponse } from "next/server";

export function proxy(req: NextRequest) {
	const apiUrl = process.env.API_URL || "http://localhost:8080";
	const destination = new URL(req.nextUrl.pathname, apiUrl);

	// Dynamically rewrite the request at runtime
	return NextResponse.rewrite(destination);
}

// Optional: only apply to API routes
export const config = {
	matcher: "/api/:path*",
};
