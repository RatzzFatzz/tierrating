import type { Metadata } from "next";
import {Geist, Geist_Mono, Inter} from "next/font/google";
import "./globals.css";
import {AuthProvider} from "@/contexts/AuthContext";
import NavBar from "@/components/navbar";

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
        <html lang="en" className="dark">
            <body className={inter.className}>
                <AuthProvider>
                    <div className="min-h-screen flex flex-col">
                        <NavBar />
                        <main className="flex-grow">
                            {children}
                        </main>
                    </div>
                </AuthProvider>
            </body>
        </html>
    )
}
