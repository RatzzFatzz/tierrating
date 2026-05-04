import { ProtectedRoute } from "@/contexts/route-accessibility";

export default function ProfileLayout({ children }: { children: React.ReactNode }) {
	return <ProtectedRoute>{children}</ProtectedRoute>;
}
