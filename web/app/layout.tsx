import type { Metadata } from "next";
import {Inter} from "next/font/google";
import "./globals.css";
import {AuthProvider} from "@/contexts/AuthContext";
import NavBar from "@/components/navbar";
import {ThemeProvider} from "@/components/theme-provider";
import Starfield from "react-starfield";

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: "Tier Rating",
  description: "Rate your media content in a tier list",
};

export default function Layout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="en" suppressHydrationWarning>
            <body className={inter.className}>
                <ThemeProvider attribute="class" defaultTheme="system" enableSystem disableTransitionOnChange>
                    <Starfield
                        starCount={500}
                        starColor={[255, 255, 255]}
                        speedFactor={0.05}
                        backgroundColor="black"
                    />
                    <AuthProvider>
                        <div className="min-h-screen flex flex-col">
                            <NavBar />
                            <main className="flex-grow">
                                <div className="pt-24"></div>
                                {children}
                            </main>
                        </div>
                    </AuthProvider>
                </ThemeProvider>
            </body>
        </html>
    )
}
