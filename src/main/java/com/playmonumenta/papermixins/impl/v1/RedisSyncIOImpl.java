package com.playmonumenta.papermixins.impl.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.playmonumenta.mixinapi.v1.RedisSyncIO;
import java.io.StringReader;
import net.minecraft.SharedConstants;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixTypes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class RedisSyncIOImpl implements RedisSyncIO {
	private static final RedisSyncIOImpl INSTANCE = new RedisSyncIOImpl();

	public static RedisSyncIOImpl getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonObject getPlayerScoresAsJson(String playerName, Scoreboard scoreboard) {
		final var playerScoreMap = ((CraftScoreboard) scoreboard).getHandle().playerScores.get(playerName);
		JsonObject data = new JsonObject();

		if (playerScoreMap == null) {
			return data;
		}

		for (final var entry : playerScoreMap.listRawScores().entrySet()) {
			data.addProperty(entry.getKey().getName(), entry.getValue().value());
		}

		return data;
	}

	@Override
	public void savePlayer(Player player) {
		PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
		playerList.save(((CraftPlayer) player).getHandle());
	}

	@Override
	public String upgradePlayerAdvancements(String advancementsStr) {
		JsonReader jsonreader = new JsonReader(new StringReader(advancementsStr));
		jsonreader.setLenient(false);
		Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonreader));

		if (dynamic.get("DataVersion").asNumber().result().isEmpty()) {
			dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
		}

		DataFixer dataFixer = ((CraftServer) Bukkit.getServer()).getHandle().getServer().getFixerUpper();
		dynamic = DataFixTypes.ADVANCEMENTS.update(dataFixer, dynamic, dynamic.get("DataVersion").asInt(0),
			SharedConstants.getCurrentVersion().dataVersion().getVersion());
		dynamic = dynamic.remove("DataVersion");

		JsonElement element = dynamic.getValue();
		element.getAsJsonObject().addProperty("DataVersion",
			SharedConstants.getCurrentVersion().dataVersion().getVersion());
		return PlayerAdvancements.GSON.toJson(element);
	}
}
