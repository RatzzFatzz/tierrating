import { ProtectedRoute } from "@/components/contexts/route-accessibility";
import TierListPage from "@/components/tierlist/tier-list-page";

export default function SteamGames() {
	return (
		<ProtectedRoute>
			<TierListPage title={"Steam Games Tier List"} provider={"steam-games"} />
		</ProtectedRoute>
	);
}
