package at.pcgamingfreaks.model.thirdparty.steam.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SteamGameInfo {
	@JsonProperty("appid")
	private long appId;
	private String name;
	@JsonProperty("playtime_forever")
	private long playtime;
	@JsonProperty("playtime_windows_forever")
	private long playtimeWindows;
	@JsonProperty("playtime_linux_forever")
	private long playtimeLinux;
	@JsonProperty("playtime_mac_forever")
	private long playtimeMac;
	@JsonProperty("playtime_deck_forever")
	private long playtimeSteamDeck;
	@JsonProperty("img_logo_url")
	private String coverLogoHash;
	@JsonProperty("img_icon_url")
	private String coverIconHash;
}
