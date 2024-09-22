package com.playmonumenta.papermixins.mixin.misc;

import com.playmonumenta.mixinapi.v1.CustomMeleeAttackGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin implements CustomMeleeAttackGoal {
	@Shadow
	@Final
	protected PathfinderMob mob;

	@Override
	public boolean isWithinAttackRange(LivingEntity entity) {
		return this.mob.isWithinMeleeAttackRange(((CraftLivingEntity) entity).getHandle());
	}

	@Redirect(
		method = "canUse",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/PathfinderMob;isWithinMeleeAttackRange" +
				"(Lnet/minecraft/world/entity/LivingEntity;)Z"
		)
	)
	private boolean redirectCanUse(PathfinderMob instance,
								net.minecraft.world.entity.LivingEntity livingEntity) {
		return isWithinAttackRange(livingEntity.getBukkitLivingEntity());
	}
}
