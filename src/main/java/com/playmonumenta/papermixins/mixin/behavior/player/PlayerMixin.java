package com.playmonumenta.papermixins.mixin.behavior.player;

import com.playmonumenta.papermixins.paperapi.v1.event.SweepingEdgeParticleEvent;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
