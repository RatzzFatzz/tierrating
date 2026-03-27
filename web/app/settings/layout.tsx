import { ProtectedRoute } from "@/contexts/route-accessibility";

export default function SettingsLayout({ children }: { children: React.ReactNode }) {
	return <ProtectedRoute>{children}</ProtectedRoute>;
}
