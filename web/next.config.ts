import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    images: {
        remotePatterns: [new URL("https://s4.anilist.co/file/anilistcdn/media/**/*.*")]
    },
    reactStrictMode: false,
    publicRuntimeConfig: {
        API_URL: process.env.API_URL || 'http://localhost:8080',
    },
    serverRuntimeConfig: {
        API_URL: process.env.API_URL || 'http://localhost:8080',
    },
    eslint: {
        // Warning: This allows production builds to successfully complete even if your project has ESLint errors.
        ignoreDuringBuilds: true,
    },
};

export default nextConfig;

