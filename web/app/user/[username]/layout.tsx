import { ProtectedRoute } from "@/components/contexts/route-accessibility";

export default function ProfileLayout({ children }: { children: React.ReactNode }) {
	return <ProtectedRoute>{children}</ProtectedRoute>;
}