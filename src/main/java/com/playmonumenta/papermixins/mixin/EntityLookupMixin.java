package com.playmonumenta.papermixins.mixin;

import io.papermc.paper.chunk.system.entity.EntityLookup;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLookup.class)
public class EntityLookupMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    public void logAdd(Entity entity, boolean fromDisk, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("Entity added to world: " + entity.getStringUUID());
        try {
            throw new Exception("Test exception for debugging: entity added to world");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
