package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.mixin.accessor.AbstractArrowAccessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin extends AbstractArrow {
	@Shadow
	public boolean dealtDamage;

	protected ThrownTridentMixin(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level, ItemStack.EMPTY);
	}

	@Unique
	private AbstractArrowAccessor monumenta$accessor() {
		return (AbstractArrowAccessor) this;
	}

	@Inject(method = "onHitEntity", at = @At("HEAD"))
	private void trackPiercedEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
		if (!ConfigManager.getConfig().behavior.allProjectilesPierce || getPierceLevel() <= 0) {
			return;
		}

		IntOpenHashSet piercedEntityIds = monumenta$accessor().monumenta$getPiercingIgnoreEntityIds();
		if (piercedEntityIds == null) {
			piercedEntityIds = new IntOpenHashSet(5);
			monumenta$accessor().monumenta$setPiercingIgnoreEntityIds(piercedEntityIds);
		}

		piercedEntityIds.add(entityHitResult.getEntity().getId());
	}

	@Redirect(
		method = "onHitEntity",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;dealtDamage:Z",
			opcode = Opcodes.PUTFIELD
		)
	)
	private void delayDealtDamage(ThrownTrident trident, boolean newValue) {
		if (!ConfigManager.getConfig().behavior.allProjectilesPierce || getPierceLevel() <= 0) {
			dealtDamage = newValue;
			return;
		}

		IntOpenHashSet piercedEntityIds = monumenta$accessor().monumenta$getPiercingIgnoreEntityIds();
		int hitCount = piercedEntityIds == null ? 0 : piercedEntityIds.size();
		dealtDamage = hitCount >= getPierceLevel() + 1;
	}

	@WrapOperation(
		method = "onHitEntity",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
		)
	)
	private void keepVelocity(ThrownTrident trident, Vec3 deltaMovement, Operation<Void> original) {
		if (!ConfigManager.getConfig().behavior.allProjectilesPierce || getPierceLevel() <= 0) {
			original.call(trident, deltaMovement);
			return;
		}

		IntOpenHashSet piercedEntityIds = monumenta$accessor().monumenta$getPiercingIgnoreEntityIds();
		int hitCount = piercedEntityIds == null ? 0 : piercedEntityIds.size();
		if (hitCount >= getPierceLevel() + 1) {
			original.call(trident, deltaMovement);
		}
	}
}
