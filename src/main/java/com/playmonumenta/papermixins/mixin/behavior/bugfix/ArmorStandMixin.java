package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntity {
	protected ArmorStandMixin(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
	}

	@Inject(method = "kill(Z)V", at = @At("HEAD"))
	private void onKill(boolean callEvent, CallbackInfo ci) {
		this.setHealth(0);
	}

	@Inject(method = "brokenByAnything", at = @At("HEAD"))
	private void onBroken(DamageSource damageSource, CallbackInfoReturnable<EntityDeathEvent> cir) {
		this.setHealth(0);
	}
}
