package com.playmonumenta.papermixins.mixin.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Level.class)
public class LevelMixin {
    @ModifyArgs(method = "clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;fastClip(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"))
    public void a(Args args) {
        Vec3 from = args.get(0);
        Vec3 to = args.get(1);
        ClipContext clipContext = args.get(3);
        if (clipContext.collisionContext instanceof EntityCollisionContext e && Math.abs(from.y - to.y) > 1e8) {
            System.out.println("Entity attempted to clip too far!");
            args.set(1, new Vec3(to.x, from.y, to.z));
            Entity entity = e.getEntity();
            if (entity == null) {
                System.out.println("But it was null?");
                return;
            }
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(new Vec3(motion.x, 0, motion.z));
            System.out.printf(
                    "Entity name %s entity type %s entity tags %s%n",
                    entity.getName().getString(),
                    EntityType.getKey(entity.getType()).getPath(),
                    String.join(", ", entity.getTags())
            );
        }
    }
}
