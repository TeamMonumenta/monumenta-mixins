package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * Charged creepers no longer cause mob head drops.
 */
@Mixin(Creeper.class)
public class CreeperMixin {
    /**
     * @author Flowey
     * @reason Disable skull drop.
     */
    @Overwrite
    public boolean canDropMobsSkull() {
        return false;
    }
}
