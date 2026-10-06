package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.Mob.DEFAULT_ATTACK_REACH;

/**
 * @author Flowey
 * @mm-patch 0034-Monumenta-Fix-passengers-breaking-controlling-mob-AI.patch
 * <p>
 * Fix passengers breaking/controlling mob AI.
 */
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
	protected MobMixin(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
	}

	/**
	 * @author Flowey
	 * @reason Remove passenger AI checks.
	 */
	@Inject(
		method = "getControllingPassenger",
		at = @At("HEAD"),
		cancellable = true
	)
	public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
		if (ConfigManager.getConfig().behavior.disableControllingPassenger) {
			cir.setReturnValue(null);
		}
	}

	@WrapMethod(method = "isWithinMeleeAttackRange")
	public boolean squircleWithinMeleeAttack3Range(LivingEntity entity, Operation<Boolean> original) {
		if (!ConfigManager.getConfig().behavior.roundedMobReachHitbox) {
			return original.call(entity);
		}
		// cases:
		// - entity AABB engulfs rectangular segment
		// - entity AABB has 1 corner inside a round corner
		// - entity AABB has 1 corner inside rectangular segment

		AABB selfBB = this.getBoundingBox();
		AABB otherBB = entity.getBoundingBox();
		// Case 1 & 3:
		double halfXSize = (selfBB.getXsize() + otherBB.getXsize()) / 2;
		double halfZSize = (selfBB.getZsize() + otherBB.getZsize()) / 2;
		double xReach = DEFAULT_ATTACK_REACH + halfXSize;
		double zReach = DEFAULT_ATTACK_REACH + halfZSize;
		double xDist = Math.abs((selfBB.minX + selfBB.maxX) / 2 - (otherBB.minX + otherBB.maxX) / 2);
		double zDist = Math.abs((selfBB.minZ + selfBB.maxZ) / 2 - (otherBB.minZ + otherBB.maxZ) / 2);
		// out of y range
		if (selfBB.minY - DEFAULT_ATTACK_REACH > otherBB.maxY || selfBB.maxY + DEFAULT_ATTACK_REACH < otherBB.minY) {
			return false;
		}
		if (xDist <= xReach && zDist <= halfZSize || zDist <= zReach && xDist <= halfXSize) {
			return true;
		}
		double reachSq = DEFAULT_ATTACK_REACH * DEFAULT_ATTACK_REACH;
		return distSqr(selfBB.maxX, selfBB.maxZ, otherBB.minX, otherBB.minZ) <= reachSq
			|| distSqr(selfBB.maxX, selfBB.minZ, otherBB.minX, otherBB.maxZ) <= reachSq
			|| distSqr(selfBB.minX, selfBB.maxZ, otherBB.maxX, otherBB.minZ) <= reachSq
			|| distSqr(selfBB.minX, selfBB.minZ, otherBB.maxX, otherBB.maxZ) <= reachSq;
	}

	@Unique
	private static double distSqr(double x1, double z1, double x2, double z2) {
		double dx = x1 - x2;
		double dz = z2 - z1;
		return dx * dx + dz * dz;
	}
}
