import { ProtectedRoute } from "@/contexts/route-accessibility";

export default function AuthLayout({ children }: { children: React.ReactNode }) {
	return <ProtectedRoute>{children}</ProtectedRoute>;
}
