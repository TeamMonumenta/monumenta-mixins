package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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
    private float monumenta$modifyAttackDamage(float amount, @Local(ordinal = 0) float f) {
        return f;
    }
}
