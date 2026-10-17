package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	@Final
	private static Logger LOGGER;

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
		if (knockbackResist >= 0.9999) {
			ci.cancel();
		}
	}

//	@Inject(method = "setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;Lorg/bukkit/event/entity/EntityRemoveEvent$Cause;)V",
//		at = @At("HEAD"))
//	public void a(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause, CallbackInfo ci) {
//		Bukkit.getOnlinePlayers().forEach(p -> p.sendRawMessage("entity removed"));
//		LOGGER.info(String.join("\n", Arrays.stream(Thread.currentThread().getStackTrace()).map(Object::toString).toList()));
//	}
}
