package com.playmonumenta.papermixins.mixin.behavior.player;

import net.minecraft.world.entity.player.Player;
import org.bukkit.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements LivingEntity {
	@Inject(method = "sweepAttack", at = @At("HEAD"), cancellable = true)
	private void sweepAttack(CallbackInfo ci) {
		ci.cancel();
	}
}
