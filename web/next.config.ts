import type { NextConfig } from "next";

const nextConfig: NextConfig = {
	images: {
		remotePatterns: [new URL("https://s4.anilist.co/file/anilistcdn/media/**/*.*"), new URL("https://image.tmdb.org/t/p/**/*.*")],
	},
	reactStrictMode: false,
	output: "standalone",
	async rewrites() {
		return [
			{
				source: "/api/:path*",
				destination: `${process.env.API_URL || "http://localhost:8080"}/api/:path*`,
			},
		];
	},
};

export default nextConfig;
