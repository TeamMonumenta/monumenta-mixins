package com.playmonumenta.papermixins.mixin.behavior.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0022-Monumenta-Disable-Elder-Guardian-Mining-Fatigue.patch
 * <p>
 * Disable elder guardian mining fatigue application.
 */
@Mixin(ElderGuardian.class)
public class ElderGuardianMixin extends Guardian {
    public ElderGuardianMixin(EntityType<? extends Guardian> type, Level world) {
        super(type, world);
    }

    /**
     * @author Flowey
     * @reason Remove mining fatigue logic
     * There might be a more portable way of doing this...
     * TODO: look for a more portable way.
     */
    @Overwrite
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (!this.hasRestriction()) {
            this.restrictTo(this.blockPosition(), 16);
        }
    }
}
