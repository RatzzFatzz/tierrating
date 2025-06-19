import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    images: {
        remotePatterns: [new URL("https://s4.anilist.co/file/anilistcdn/media/**/*.*")]
    },
    reactStrictMode: false
};

export default nextConfig;

