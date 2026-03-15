package com.playmonumenta.papermixins.mixin.behavior.player;

import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.paperapi.v1.event.SweepingEdgeParticleEvent;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements LivingEntity {
	@Shadow
	public abstract CraftHumanEntity getBukkitEntity();

	@Inject(method = "sweepAttack", at = @At("HEAD"), cancellable = true)
	private void sweepAttack(CallbackInfo ci) {
		CraftPlayer craftPlayer = (CraftPlayer) getBukkitEntity();
		Event event = new SweepingEdgeParticleEvent(craftPlayer);

		if (!event.callEvent()) {
			ci.cancel();
		}
	}

	@ModifyVariable(
		method = "attack",
		at = @At("STORE"),
		name = "k",
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHealth()F"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I")
		)
	)
	private int lessDamageIndicator(int value, @Local(name = "f5") float f5) {
		if (ConfigManager.getConfig().behavior.reduceDamageParticles) {
			return (int) (2 * Math.sqrt(f5));
		}
		return value;
	}
}
