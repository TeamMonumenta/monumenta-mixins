package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * @mm-patch 0037-Monumenta-Remove-vanilla-Enderman-teleportation.patch
 * <p>
 * "don't override for grass/light, use super value"
 */
@Mixin(EnderMan.class)
public abstract class EnderManMixin extends Monster {
    protected EnderManMixin(EntityType<? extends Monster> type, Level world) {
        super(type, world);
    }

    @ModifyExpressionValue(
        method = "hurt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/EnderMan;tryEscape" +
                "(Lcom/destroystokyo/paper/event/entity/EndermanEscapeEvent$Reason;)Z"
        )
    )
    private boolean monumenta$storeTryEscapeRes(
        boolean original,
        @Share("hasEscaped") LocalBooleanRef ref
    ) {
        ref.set(original);
        return original;
    }

    @ModifyReturnValue(
        method = "hurt",
        at = @At("TAIL")
    )
    private boolean monumenta$modifyHurtReturnValue(
        boolean original, DamageSource source, float amount,
        @Share("hasEscaped") LocalBooleanRef ref
    ) {
        if (ref.get())
            return original;
        return original || super.hurt(source, amount);
    }

    /**
     * @author Flowey
     * @reason Ignore carrying item.
     */
    @Overwrite
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence();
    }

    /**
     * @author Flowey
     * @reason Disable TP.
     */
    @Overwrite
    public boolean teleport() {
        return false;
    }
}