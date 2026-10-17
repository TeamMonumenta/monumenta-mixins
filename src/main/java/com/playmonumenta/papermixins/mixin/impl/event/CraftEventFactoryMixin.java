package com.playmonumenta.papermixins.mixin.impl.event;

import com.google.common.base.Function;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.MixinState;
import com.playmonumenta.papermixins.impl.v1.MonumentaPaperAPIImpl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * @mm-patch 0027-Monumenta-Handle-iframes-after-damage-event.patch
 * <p>
 * Add IFRAMES to event.
 */
@SuppressWarnings("deprecation")
@Mixin(CraftEventFactory.class)
public class CraftEventFactoryMixin {
	@Inject(
		method = "handleLivingEntityDamageEvent",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
			ordinal = 0
		)
	)
	private static void addIframeModifier(
		Entity damagee, DamageSource source,
		double rawDamage, double hardHatModifier, double blockingModifier, double armorModifier,
		double resistanceModifier, double magicModifier, double absorptionModifier,
		Function<Double, Double> hardHat, Function<Double, Double> blocking, Function<Double, Double> armor,
		Function<Double, Double> resistance, Function<Double, Double> magic, Function<Double, Double> absorption,
		CallbackInfoReturnable<EntityDamageEvent> cir,
		@Local(name = "modifiers") Map<EntityDamageEvent.DamageModifier, Double> modifiers,
		@Local(name = "modifierFunctions") Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions
	) {
		final var api = MonumentaPaperAPIImpl.getInstance();
		modifiers.put(api.getIframes(), MixinState.IFRAME_VALUE.get());
		modifierFunctions.put(api.getIframes(), (Function<? super Double, Double>) MixinState.IFRAME_FUNC.get());
	}

	/**
	 * @author Flowey, usb
	 * Handle 'monumenta:custom' damage source from main plugin
	 * TODO: redo this in 1.20.5+ with Paper's damage source API
	 */
	@ModifyArg(
		method = "handleEntityDamageEvent(Lnet/minecraft/world/entity/Entity;" +
				"Lnet/minecraft/world/damagesource/DamageSource;Ljava/util/Map;Ljava/util/Map;Z)" +
				"Lorg/bukkit/event/entity/EntityDamageEvent;",
		at = @At(
				value = "INVOKE",
				target = "Lorg/bukkit/craftbukkit/v1_20_R3/event/CraftEventFactory;callEntityDamageEvent" +
						"(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;" +
						"Lorg/bukkit/event/entity/EntityDamageEvent$DamageCause;Lorg/bukkit/damage/DamageSource;" +
						"Ljava/util/Map;Ljava/util/Map;ZZ)Lorg/bukkit/event/entity/EntityDamageEvent;",
				ordinal = 0
		),
		slice = @Slice(
				from = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;"
				)
		),
		index = 2
	)
	private static EntityDamageEvent.DamageCause handleCustomCause(EntityDamageEvent.DamageCause cause, @Local(argsOnly = true) DamageSource type) {
		// type.getMsgId() doesn't print namespace, so use 'custom' as a match
		return type.getMsgId().contains("custom") ? EntityDamageEvent.DamageCause.CUSTOM : cause;
	}

	@Definition(id = "spawnReason", local = @Local(type = CreatureSpawnEvent.SpawnReason.class, argsOnly = true))
	@Definition(id = "CUSTOM", field = "Lorg/bukkit/event/entity/CreatureSpawnEvent$SpawnReason;CUSTOM:Lorg/bukkit/event/entity/CreatureSpawnEvent$SpawnReason;")
	@Expression("spawnReason != CUSTOM")
	@ModifyExpressionValue(method = "doEntityAddEventCalling", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean a(boolean original, Level world, Entity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
		return original || entity.getTags().contains("peaceful_despawn_immune");
	}
}
