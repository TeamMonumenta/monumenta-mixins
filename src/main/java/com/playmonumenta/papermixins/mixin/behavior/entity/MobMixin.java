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
		AABB selfBB = this.getBoundingBox();
		AABB otherBB = entity.getBoundingBox();
		double selfX = (selfBB.minX + selfBB.maxX) / 2;
		double selfY = (selfBB.minY + selfBB.maxY) / 2;
		double selfZ = (selfBB.minZ + selfBB.maxZ) / 2;
		// Sphercle (Sphere with square 'center')!
		double distX = Math.max(otherBB.minX - selfX, selfX - otherBB.maxX) - selfBB.getXsize() / 2;
		double distY = Math.max(otherBB.minY - selfY, selfY - otherBB.maxY) - selfBB.getYsize() / 2;
		double distZ = Math.max(otherBB.minZ - selfZ, selfZ - otherBB.maxZ) - selfBB.getZsize() / 2;
		double distSquared = (distX > 0 ? distX * distX : 0) + (distY > 0 ? distY * distY : 0) + (distZ > 0 ? distZ * distZ : 0);
		return distSquared <= DEFAULT_ATTACK_REACH * DEFAULT_ATTACK_REACH;
	}
}
