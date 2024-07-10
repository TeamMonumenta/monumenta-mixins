package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 * <p>
 * "don't override for grass/light, use super value"
 */
@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {
    protected AnimalMixin(EntityType<? extends AgeableMob> type, Level world) {
        super(type, world);
    }

    /**
     * @author Flowey
     * @reason don't override for grass/light, use super value.
     */
    @Overwrite
    @Override
    public float getWalkTargetValue(@NotNull BlockPos pos, @NotNull LevelReader world) {
        return super.getWalkTargetValue(pos, world);
    }
}
