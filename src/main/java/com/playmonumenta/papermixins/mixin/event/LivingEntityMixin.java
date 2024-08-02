package com.playmonumenta.papermixins.mixin.event;

import com.google.common.base.Function;
import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public int invulnerableDuration;

    @Shadow public float lastHurt;

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyVariable(
        method = "actuallyHurt",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)" +
                "Lnet/minecraft/world/entity/LivingEntity$1;"
        ),
        index = 2,
        argsOnly = true
    )
    private float setupIframeHandlers(float value) {
        Function<Double, Double> iframes = f -> {
            if ((float) invulnerableTime > (float) invulnerableDuration / 2.0F) {
                return -(Math.max(f - Math.max(f - lastHurt, 0.0F), 0.0F));
            }
            return 0.0;
        };

        var iframesModifier = iframes.apply((double) value);
        MonumentaMod.IFRAME_VALUE.set(iframesModifier);
        MonumentaMod.IFRAME_FUNC.set(iframes);
        value += iframesModifier.floatValue();
        return value;
    }
}
