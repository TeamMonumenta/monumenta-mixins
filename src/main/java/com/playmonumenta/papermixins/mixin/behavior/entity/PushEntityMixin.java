package com.playmonumenta.papermixins.mixin.behavior.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class PushEntityMixin {
    @Unique
    LivingEntity entity = (LivingEntity) (Object) this;
    /**
     * @author ashphyx
     * @reason Prevent mobs pushing each other from bypassing Knockback Resistance.
     */
    @Shadow
    @Overwrite
    public void push(double deltaX, double deltaY, double deltaZ, @Nullable Entity pushingEntity) {
        org.bukkit.util.Vector delta = new org.bukkit.util.Vector(deltaX, deltaY, deltaZ);
        if (pushingEntity != null) {
            io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent event = new io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent(entity.getBukkitEntity(), pushingEntity.getBukkitEntity(), delta);
            if (!event.callEvent()) {
                return;
            }
            delta = event.getAcceleration();
        }
        entity.setDeltaMovement(entity.getDeltaMovement()
                .add(new Vec3(delta.getX(), delta.getY(), delta.getZ())
                        .scale(Math.max(1 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0))));
        entity.hasImpulse = true;
        // Paper end - Add EntityKnockbackByEntityEvent and EntityPushedByEntityAttackEvent
    }
}
