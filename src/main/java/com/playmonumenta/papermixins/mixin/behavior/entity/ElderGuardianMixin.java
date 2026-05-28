package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0022-Monumenta-Disable-Elder-Guardian-Mining-Fatigue.patch
 * <p>
 * Disable elder guardian mining fatigue application.
 */
@Mixin(ElderGuardian.class)
public class ElderGuardianMixin extends Guardian {
	public ElderGuardianMixin(EntityType<? extends Guardian> type, Level world) {
		super(type, world);
	}

	/**
	 * @author Flowey
	 * @reason Remove mining fatigue logic
	 */
	@ModifyExpressionValue(
		method = "customServerAiStep",
		at = @At("MIXINEXTRAS:EXPRESSION")
	)
	@Expression("? % 1200 == 0")
	public boolean customServerAiStep(boolean original) {
		if (ConfigManager.getConfig().behavior.disableGuardianMiningFatigue) {
			return false;
		}

		return original;
	}
}
