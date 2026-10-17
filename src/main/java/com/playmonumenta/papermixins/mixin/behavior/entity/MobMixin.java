package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

/**
 * @author Flowey
 * @mm-patch 0034-Monumenta-Fix-passengers-breaking-controlling-mob-AI.patch
 * <p>
 * Fix passengers breaking/controlling mob AI.
 */
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
	@Unique private boolean meow = false;

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
		if(ConfigManager.getConfig().behavior.disableControllingPassenger) {
			cir.setReturnValue(null);
		}
	}

	@ModifyExpressionValue(method = "checkDespawn()V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Mob;shouldDespawnInPeaceful()Z"))
	public boolean shouldDespawn(boolean original) {
//		if (!meow) {
//			meow = true;
//			Bukkit.getOnlinePlayers().forEach(p -> p.sendRawMessage("name: " + this.getName().getString()));
//			Bukkit.getOnlinePlayers().forEach(p -> p.sendRawMessage("tag: " + this.getTags().contains("peaceful_despawn_immune")));
//			Level level = this.level();
//			Bukkit.getOnlinePlayers().forEach(p -> p.sendRawMessage("world: " + level.getWorld().getName()));
//			Bukkit.getOnlinePlayers().forEach(p -> p.sendRawMessage("difficulty: " + level.getDifficulty().getSerializedName()));
//		}
		return original && !this.getTags().contains("peaceful_despawn_immune");
	}
}
