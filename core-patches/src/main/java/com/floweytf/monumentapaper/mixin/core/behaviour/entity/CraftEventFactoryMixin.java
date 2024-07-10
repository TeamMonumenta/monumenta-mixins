package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import com.floweytf.monumentapaper.Monumenta;
import com.floweytf.monumentapaper.api.MonumentaPaperAPI;
import com.google.common.base.Function;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
    private static void monumenta$addIframeModifier(
        Entity damagee, DamageSource source,
        double rawDamage, double hardHatModifier, double blockingModifier, double armorModifier,
        double resistanceModifier, double magicModifier, double absorptionModifier,
        Function<Double, Double> hardHat, Function<Double, Double> blocking, Function<Double, Double> armor,
        Function<Double, Double> resistance, Function<Double, Double> magic, Function<Double, Double> absorption,
        CallbackInfoReturnable<EntityDamageEvent> cir,
        @Local(name = "modifiers") Map<EntityDamageEvent.DamageModifier, Double> modifiers,
        @Local(name = "modifierFunctions") Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions
    ) {
        modifiers.put(MonumentaPaperAPI.IFRAMES, Monumenta.IFRAME_VALUE.get());
        modifierFunctions.put(MonumentaPaperAPI.IFRAMES,
            (Function<? super Double, Double>) Monumenta.IFRAME_FUNC.get());
    }
}
