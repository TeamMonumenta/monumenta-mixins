package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {
	@Unique
	@Nullable
	private IntOpenHashSet ignoredEntities;

	public AbstractArrowMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@ModifyExpressionValue(
		method = "onHitEntity",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPierceLevel()B"
		),
		slice = @Slice(
			to = @At(
				value = "INVOKE",
				target = "Lit/unimi/dsi/fastutil/ints/IntOpenHashSet;<init>(I)V"
			)
		)
	)
	private byte usePiercingCode(byte original) {
		return Byte.MAX_VALUE;
	}

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPierceLevel()B"
		)
	)
	private byte usePiercingCode2(byte original) {
		return Byte.MAX_VALUE;
	}

	@ModifyExpressionValue(
		method = "preOnHit",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPierceLevel()B"
		)
	)
	private byte usePiercingCode3(byte original) {
		return Byte.MAX_VALUE;
	}


	@WrapOperation(
		method = "preOnHit",
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/ints/IntOpenHashSet;add(I)Z"
		)
	)
	private boolean useIgnoredEntities(IntOpenHashSet instance, int key, Operation<Boolean> original, @Local(name = "entityHitResult") EntityHitResult entityHitResult) {
		if (this.ignoredEntities == null) {
			this.ignoredEntities = new IntOpenHashSet(5);
		}
		return this.ignoredEntities.add(entityHitResult.getEntity().getId());
	}

	@ModifyExpressionValue(
		method = "canHitEntity",
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/ints/IntOpenHashSet;contains(I)Z"
		)
	)
	private boolean checkForIgnoredEntities(boolean original, @Local(argsOnly = true) Entity entity) {
		return original || (ignoredEntities != null && ignoredEntities.contains(entity.getId()));
	}

	@Inject(
		method = "resetPiercedEntities",
		at = @At("TAIL")
	)
	private void clearIgnoredEntities(CallbackInfo ci) {
		if (ignoredEntities != null) {
			ignoredEntities.clear();
		}
	}
}
