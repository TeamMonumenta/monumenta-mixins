package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0022-Monumenta-Disable-Elder-Guardian-Mining-Fatigue.patch
 * <p>
 * Disable elder guardian mining fatigue application.
 */
@Mixin(ElderGuardian.class)
public class ElderGuardianMixin extends Guardian {
	public ElderGuardianMixin(EntityType<? extends Guardian> type, Level world) {
		super(type, world);
	}

	/**
	 * @author Flowey
	 * @reason Remove mining fatigue logic
	 * There might be a more portable way of doing this...
	 * TODO: look for a more portable way.
	 */
	@Inject(
		method = "customServerAiStep",
		at = @At("HEAD"),
		cancellable = true
	)
	public void customServerAiStep(CallbackInfo ci) {
		if(ConfigManager.getConfig().behavior.disableGuardianMiningFatigue) {
			super.customServerAiStep();

			if (!this.hasRestriction()) {
				this.restrictTo(this.blockPosition(), 16);
			}

			ci.cancel();
		}
	}
}
