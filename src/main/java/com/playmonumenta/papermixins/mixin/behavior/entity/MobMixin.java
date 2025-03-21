package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0034-Monumenta-Fix-passengers-breaking-controlling-mob-AI.patch
 * <p>
 * Fix passengers breaking/controlling mob AI.
 */
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
	protected MobMixin(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
	}

	/**
	 * @author Flowey
	 * @reason Remove passenger AI checks.
	 */
	@Inject(
		method = "getControllingPassenger",
		at = @At("HEAD"),
		cancellable = true
	)
	public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
		if(ConfigManager.getConfig().behavior.disableControllingPassenger) {
			cir.setReturnValue(null);
		}
	}
}
