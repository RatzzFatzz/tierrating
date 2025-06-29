import type { Metadata } from "next";
import {Geist, Geist_Mono, Inter} from "next/font/google";
import "./globals.css";
import {AuthProvider} from "@/contexts/AuthContext";
import NavBar from "@/components/navbar";
import {ThemeProvider} from "@/components/theme-provider";

const inter = Inter({ subsets: ['latin'] })

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Tier Rating",
  description: "Rate your media content in a tier list",
};

export default function Layout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="en" suppressHydrationWarning>
            <body className={inter.className}>
                <ThemeProvider attribute="class" defaultTheme="system" enableSystem disableTransitionOnChange>
                    <AuthProvider>
                        <div className="min-h-screen flex flex-col">
                            <NavBar />
                            <main className="flex-grow">
                                <div className="pt-14"></div>
                                {children}
                            </main>
                        </div>
                    </AuthProvider>
                </ThemeProvider>
            </body>
        </html>
    )
}
