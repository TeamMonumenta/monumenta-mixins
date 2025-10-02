package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	/**
	 * @author ashphyx
	 * @reason Prevent mobs pushing each other from bypassing Knockback Resistance.
	 * Note that this might not actually work. The method is actively being called, but I still get pushed.
	 * Potentially there is another, mysterious, method being called that still performs the pushing.
	 */
	@Inject(
		method = "push(DDDLnet/minecraft/world/entity/Entity;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
		),
		cancellable = true
	)
	public void disablePushing(double deltaX, double deltaY, double deltaZ, Entity pushingEntity, CallbackInfo ci) {
		if(ConfigManager.getConfig().behavior.disableMobPushingWithKbr) {
			return;
		}

		final Entity self = Util.c(this);
		final var knockbackResist = self instanceof LivingEntity living ?
			living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0.0;
		if (knockbackResist >= 1) {
			ci.cancel();
		}
	}
}
