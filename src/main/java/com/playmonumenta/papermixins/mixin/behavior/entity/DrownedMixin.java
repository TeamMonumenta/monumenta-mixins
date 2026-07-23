package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Drowned.class)
public class DrownedMixin extends Zombie {
	public DrownedMixin(EntityType<? extends Zombie> type, Level world) {
		super(type, world);
	}

	@ModifyExpressionValue(
		method = "okTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;isDay()Z"
		)
	)
	public boolean ignoreDay(boolean original) {
		if (ConfigManager.getConfig().behavior.drownedAttackDuringDay) {
			return false;
		}
		return original;
	}
}
