package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.playmonumenta.papermixins.duck.SnowballAccess;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class SnowballMixin extends ThrowableItemProjectile implements SnowballAccess {
	@Unique
	private IntOpenHashSet monumenta$piercedEntityIds;

	protected SnowballMixin(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
	}

	@Unique
	private int monumenta$getPiercingLevel() {
		ItemStack item = getItem();
		return item.isEmpty() ? 0 : EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, item);
	}

	@Override
	public IntOpenHashSet monumenta$getPiercedEntityIds() {
		return monumenta$piercedEntityIds;
	}

	@Inject(
		method = "onHitEntity",
		at = @At("TAIL")
	)
	private void trackPiercedEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
		if (monumenta$getPiercingLevel() <= 0) {
			return;
		}

		if (monumenta$piercedEntityIds == null) {
			monumenta$piercedEntityIds = new IntOpenHashSet(4);
		}

		monumenta$piercedEntityIds.add(entityHitResult.getEntity().getId());
	}

	@WrapOperation(
		method = "onHit",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/Snowball;discard(Lorg/bukkit/event/entity/EntityRemoveEvent$Cause;)V"
		)
	)
	@SuppressWarnings("removal")
	private void keepSnowballAlive(Snowball snowball, EntityRemoveEvent.Cause cause, Operation<Void> original, HitResult hitResult) {
		if (monumenta$getPiercingLevel() <= 0 || hitResult.getType() != HitResult.Type.ENTITY) {
			original.call(snowball, cause);
			return;
		}

		int piercedEntityCount = monumenta$piercedEntityIds == null ? 0 : monumenta$piercedEntityIds.size();
		if (piercedEntityCount >= monumenta$getPiercingLevel() + 1) {
			original.call(snowball, cause);
		}
	}

}
