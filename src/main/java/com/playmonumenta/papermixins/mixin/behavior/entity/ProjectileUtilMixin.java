package com.playmonumenta.papermixins.mixin.behavior.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Only allow shooting from main hand.
 */
@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
	@Inject(
		method = "getWeaponHoldingHand",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void returnMainhandIfBow(LivingEntity entity, Item item,
											CallbackInfoReturnable<InteractionHand> cir) {
		if (Items.BOW == item) {
			cir.setReturnValue(InteractionHand.MAIN_HAND);
			cir.cancel();
		}
	}
}
