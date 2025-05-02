package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.paperapi.v1.event.IronGolemHealEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0035-Monumenta-Remove-randomness-from-iron-golem-attacks.patch
 * <p>
 * Iron golem damage should be consistent.
 */
@Mixin(IronGolem.class)
public abstract class IronGolemMixin {
	@ModifyArg(
		method = "doHurtTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
		),
		index = 1
	)
	private float modifyAttackDamage(float amount, @Local(ordinal = 0) float f) {
		return ConfigManager.getConfig().behavior.disableGolemAttackRandomness ? f : amount;
	}

	@Inject(
			method = "mobInteract",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/IronGolem;heal(F)V"),
			cancellable = true
	)
	private void onHeal(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		IronGolemHealEvent event = new IronGolemHealEvent((CraftPlayer) (player.getBukkitEntity()), (org.bukkit.entity.IronGolem) ((IronGolem) ((Object) this)).getBukkitEntity());
		event.callEvent();
		if (event.isCancelled()) {
			cir.setReturnValue(InteractionResult.FAIL);
		}
	}

}
