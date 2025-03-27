package com.playmonumenta.papermixins.debug;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class DebugMapGenerator {
	private static class ChunkStateMapRenderer extends MapRenderer {
		private static final Color UNLOADED = new Color(81, 81, 81);
		private static final Color EMPTY = new Color(94, 40, 114);
		private static final Color OTHER = new Color(92, 219, 213);
		private static final Color DIRTY = new Color(250, 238, 77);
		private static final Color CLEAN = new Color(127, 204, 25);
		private static final Color MUST_NOT_SAVE = new Color(220, 0, 0);
		private static final Color LOADING_COLOR = new Color(160, 160, 255);
		private static final int CHUNKS_TO_RENDER = 16;
		private static final int CHUNK_WIDTH = 128 / CHUNKS_TO_RENDER;
		private static final int CHUNK_PX_FACTOR = 16 / CHUNK_WIDTH;

		private ChunkStateMapRenderer() {
			super(true);
		}

		private static @NotNull Color getColor(ChunkAccess chunk) {
			Color primaryColor;

			if (chunk == null) {
				primaryColor = UNLOADED;
			} else if (chunk instanceof EmptyLevelChunk) {
				primaryColor = EMPTY;
			} else if (chunk instanceof LevelChunk levelChunk) {
				if (levelChunk.mustNotSave) {
					primaryColor = MUST_NOT_SAVE;
				} else if (chunk.isUnsaved()) primaryColor = DIRTY;
				else {
					primaryColor = CLEAN;
				}
			} else {
				primaryColor = OTHER;
			}
			return primaryColor;
		}

		@Override
		public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
			ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();

			for (int dcx = -8, px = 0; dcx < 8; dcx++, px += 128 / 16) {
				for (int dcz = -8, py = 0; dcz < 8; dcz++, py += 128 / 16) {
					final var chunk = level.getChunkIfLoadedImmediately(
						player.getChunk().getX() + dcx,
						player.getChunk().getZ() + dcz
					);

					final var color = getColor(chunk);

					for (int dpx = 0; dpx < 8; dpx++) {
						for (int dpy = 0; dpy < 8; dpy++) {
							if (dpx == 0 || dpy == 0) {
								canvas.setPixelColor(px + dpx, py + dpy, Color.BLACK);
							} else {
								canvas.setPixelColor(px + dpx, py + dpy, color);
							}
						}
					}

					if (dcx == 0 && dcz == 0) {
						// TODO: what? why?
						final var cursors = new MapCursorCollection();
						// wtf
						int rotRaw = (int) (((((player.getYaw() + 360d) / 360d) + (8 / 360d)) * 16) % 16);

						int chunkRelX = (int) player.getLocation().getX() - player.getChunk().getX() * 16;
						int chunkRelZ = (int) player.getLocation().getZ() - player.getChunk().getZ() * 16;

						int curX = px + chunkRelX / CHUNK_PX_FACTOR;
						int curY = py + chunkRelZ / CHUNK_PX_FACTOR;

						cursors.addCursor(curX * 2 - 128, curY * 2 - 128, (byte) rotRaw);
						canvas.setCursors(cursors);
					}
				}
			}

			int updateTick = (Bukkit.getCurrentTick() / 20) % 4;

			for (int px = 0; px < 4 * 4 + 2; px++) {
				for (int py = 0; py < 4 + 2; py++) {
					canvas.setPixelColor(px, py, Color.WHITE);
				}
			}

			for (int px = updateTick * 4 + 1; px < updateTick * 4 + 5; px++) {
				for (int py = 1; py < 5; py++) {
					canvas.setPixelColor(px, py, LOADING_COLOR);
				}
			}
		}
	}

	private static final Map<UUID, MapView> MAP_VIEW = new HashMap<>();
	private static final ChunkStateMapRenderer CHUNK_STATE_MAP_RENDERER = new ChunkStateMapRenderer();

	public static ItemStack createDebugMap(World world) {
		final var map = MAP_VIEW.computeIfAbsent(world.getUID(), ignored -> {
			final var view = Objects.requireNonNull(Bukkit.createMap(world));
			view.getRenderers().forEach(view::removeRenderer);
			view.addRenderer(CHUNK_STATE_MAP_RENDERER);
			return view;
		});

		final var item = new ItemStack(Material.FILLED_MAP);
		MapMeta meta = (MapMeta) item.getItemMeta();
		meta.setMapView(map);
		item.setItemMeta(meta);
		return item;
	}
}
