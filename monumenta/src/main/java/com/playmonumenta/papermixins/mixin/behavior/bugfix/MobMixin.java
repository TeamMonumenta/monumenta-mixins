package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Shadow
	public abstract boolean isNoAi();

	@Inject(method = "inactiveTick", at = @At("HEAD"), cancellable = true)
	private void skipInactiveTick(CallbackInfo ci) {
		if (isNoAi() && ConfigManager.getConfig().behavior.fixInactiveTickNoAi) {
			ci.cancel();
		}
	}
}
