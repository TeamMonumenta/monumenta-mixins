package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0021-Monumenta-Fix-shulker-NoAI-allowing-peeking.patch
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Fix shulker peeking if NoAI is set.
 */
@Mixin(Shulker.class)
public abstract class ShulkerMixin extends AbstractGolem {
    protected ShulkerMixin(EntityType<? extends AbstractGolem> type, Level world) {
        super(type, world);
    }

    @Inject(
        method = "setRawPeekAmount",
        at = @At("HEAD"),
        cancellable = true
    )
    private void monumenta$cancelSetPeekAmountIfNoAI(int peekAmount, CallbackInfo ci) {
        if (isNoAi()) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
        method = "hurt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Shulker;isClosed()Z"
        )
    )
    private boolean monumenta$allowArrowsOnClosed(boolean original) {
        return false;
    }
}
