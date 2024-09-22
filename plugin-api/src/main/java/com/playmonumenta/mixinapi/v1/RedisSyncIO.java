package com.playmonumenta.mixinapi.v1;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

public interface RedisSyncIO {
	/**
	 * Obtains an instance of the API.
	 *
	 * @return The API.
	 * @author Floweynt
	 * @since 1.0.1
	 */
	@NotNull
	static RedisSyncIO getInstance() {
		return MonumentaPaperAPI.getInstance().getRedisSyncIO();
	}

	JsonObject getPlayerScoresAsJson(String playerName, Scoreboard scoreboard);

	void savePlayer(Player player);

	String upgradePlayerAdvancements(String advancementsStr);
}
