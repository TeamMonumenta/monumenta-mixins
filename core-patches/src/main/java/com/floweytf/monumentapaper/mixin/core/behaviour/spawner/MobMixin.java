package com.floweytf.monumentapaper.mixin.core.behaviour.spawner;

import com.floweytf.monumentapaper.duck.EntityAccess;
import com.floweytf.monumentapaper.duck.SpawnerAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Flowey
 * @mm-patch 0025-Monumenta-Mobs-that-despawn-return-to-their-spawners.patch
 * <p>
 * Mobs that despawn return to their spawners.
 */
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Unique
    private boolean monumenta$isDespawnCandidate() {
        var spawner = ((EntityAccess) this).getSpawner();

        return spawner != null &&
            getHealth() >= 1 &&
            this.getY() > 0 &&
            ((SpawnerAccess) spawner).getBlockPos() != null;
    }

    @Unique
    public void monumenta$despawn() {
        if (monumenta$isDespawnCandidate()) {
            // Get the closest player to spawner
            var spawner = ((EntityAccess) this).getSpawner();
            var delveReprime = ((EntityAccess) this).getDelveReprime();

            var pos = ((SpawnerAccess) spawner).getBlockPos();
            var player = level().getNearestPlayer(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                -1, false
            );

            if (player != null) {
                // Figure out how far player is from spawner.
                double dX = player.getX() - pos.getX();
                double dY = player.getY() - pos.getY();
                double dZ = player.getZ() - pos.getZ();
                double nearestPlayerDistanceSquared = dX * dX + dY * dY + dZ * dZ;
                if (nearestPlayerDistanceSquared >= 576) { // 24 * 24
                    // No players are next to the spawner the mob came from - reprime it
                    if (delveReprime) {
                        // Player died nearby in a delve -> reprime to 5s
                        spawner.spawnDelay = 100;
                    } else {
                        spawner.spawnDelay = 0;
                    }
                }
            }
        }

        discard();
    }

    @Redirect(
        method = "checkDespawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;discard(Lorg/bukkit/event/entity/EntityRemoveEvent$Cause;)V"
        )
    )
    private void monumenta$replaceDiscardDespawn(Mob instance, EntityRemoveEvent.Cause cause) {
        monumenta$despawn();
    }
}
