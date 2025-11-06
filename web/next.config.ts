import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    images: {
        remotePatterns: [new URL("https://s4.anilist.co/file/anilistcdn/media/**/*.*")]
    },
    reactStrictMode: false,
    eslint: {
        // Warning: This allows production builds to successfully complete even if your project has ESLint errors.
        ignoreDuringBuilds: true,
    },
    output: "standalone"
};

export default nextConfig;

