import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";

import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "@/components/app-sidebar"

import {
    NavigationMenu,
    NavigationMenuContent,
    NavigationMenuIndicator,
    NavigationMenuItem,
    NavigationMenuLink,
    NavigationMenuList,
    NavigationMenuTrigger,
    NavigationMenuViewport,
} from "@/components/ui/navigation-menu"
import Link from "next/link";


export default function Layout({ children }: { children: React.ReactNode }) {
    return (
        <html>
            <body>
                {/*<SidebarProvider>*/}
                    {/*<AppSidebar />*/}
                {/*<NavigationMenu className="justify-self-center self-center">*/}
                {/*    <NavigationMenuList>*/}
                {/*        <NavigationMenuItem>*/}
                {/*            <NavigationMenuLink>*/}
                {/*                <Link href="/">Home</Link>*/}
                {/*            </NavigationMenuLink>*/}
                {/*        </NavigationMenuItem>*/}
                {/*    </NavigationMenuList>*/}
                {/*</NavigationMenu>*/}
                <main>
                    {/*<SidebarTrigger />*/}
                    {children}
                </main>
                {/*</SidebarProvider>*/}
            </body>
        </html>
)
}

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

// export default function RootLayout({
//   children,
// }: Readonly<{
//   children: React.ReactNode;
// }>) {
//   return (
//     <html lang="en">
//         <body>
//
//             <nav>
//                 <h2>arst</h2>
//             </nav>
//             {children}
//         </body>
//     </html>
//   );
// }
