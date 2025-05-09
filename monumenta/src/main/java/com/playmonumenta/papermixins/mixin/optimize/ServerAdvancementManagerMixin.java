package com.playmonumenta.papermixins.mixin.optimize;

import com.playmonumenta.papermixins.duck.AdvancementTreeAccess;
import com.playmonumenta.papermixins.util.Util;
import java.util.Collection;
import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {
	@Shadow
	public Map<ResourceLocation, AdvancementHolder> advancements;

	@Shadow @Final private static Logger LOGGER;

	@Redirect(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;" +
			"Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/advancements/AdvancementTree;addAll(Ljava/util/Collection;)V"
		)
	)
	private void useFastAddAll(AdvancementTree instance, Collection<AdvancementHolder> advancements) {
		final var time = Util.profile(() -> ((AdvancementTreeAccess) instance).monumenta$addAllFast(this.advancements));
		LOGGER.info("Fast advancement insertion took {}ms", time);
	}
}
