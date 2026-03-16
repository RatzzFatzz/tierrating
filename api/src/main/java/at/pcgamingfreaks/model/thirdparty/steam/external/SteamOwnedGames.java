package at.pcgamingfreaks.model.thirdparty.steam.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SteamOwnedGames {
	@JsonProperty("game_count")
	private int gameCount;
	private List<SteamGameInfo> games;
}
