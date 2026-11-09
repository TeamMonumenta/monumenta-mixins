package com.playmonumenta.papermixins.mixin;

import ca.spottedleaf.moonrise.patches.chunk_system.level.entity.EntityLookup;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLookup.class)
public class EntityLookupMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    public void logAdd(Entity entity, boolean fromDisk, boolean event, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("Entity added to world: " + entity.getStringUUID());
        try {
            throw new Exception("Test exception for debugging: entity added to world");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
