package com.playmonumenta.papermixins.mixin.behavior.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TheEndGatewayBlockEntity.class)
public class TheEndGatewayBlockEntityMixin {
    @Shadow public long age;

    @Shadow @Final private static int SPAWN_TIME;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void removeBeamAnimationTickAgeSet(BlockPos pos, BlockState state, CallbackInfo ci) {
        if(ConfigManager.getConfig().behavior.freezeEndGateway) {
            this.age = SPAWN_TIME;
        }
    }

    @WrapOperation(
        method = "beamAnimationTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;age:J",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static void removeBeamAnimationTickAgeSet(TheEndGatewayBlockEntity instance, long value, Operation<Void> original) {
        if(!ConfigManager.getConfig().behavior.freezeEndGateway) {
            original.call(instance, value);
        }
    }

    @WrapOperation(
        method = "teleportTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;age:J",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static void removeTeleportTickAgeSet(TheEndGatewayBlockEntity instance, long value, Operation<Void> original) {
        if(!ConfigManager.getConfig().behavior.freezeEndGateway) {
            original.call(instance, value);
        }
    }
}
