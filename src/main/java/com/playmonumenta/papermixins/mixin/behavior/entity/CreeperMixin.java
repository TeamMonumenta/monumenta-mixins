package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Charged creepers no longer cause mob head drops.
 */
@Mixin(Creeper.class)
public class CreeperMixin {
	/**
	 * @author Flowey
	 * @reason Disable skull drop.
	 */
	@Inject(
		method = "canDropMobsSkull",
		at = @At("HEAD"),
		cancellable = true
	)
	public void canDropMobsSkull(CallbackInfoReturnable<Boolean> cir) {
		if(MonumentaMod.getConfig().behavior.disableChargedCreeperHeads) {
			cir.setReturnValue(false);
		}
	}
}
