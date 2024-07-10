package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Ban zombie drowning.
 */
@Mixin(Zombie.class)
public class ZombieMixin {
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Zombie;startUnderWaterConversion(I)V"
        )
    )
    private void monumenta$disableConversion(Zombie instance, int ticksUntilWaterConversion) {
    }
}
