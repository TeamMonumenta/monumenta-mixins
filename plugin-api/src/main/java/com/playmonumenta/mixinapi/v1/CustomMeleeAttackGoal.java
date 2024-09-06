package com.playmonumenta.mixinapi.v1;

import org.bukkit.entity.LivingEntity;

public interface CustomMeleeAttackGoal {
	boolean isWithinAttackRange(LivingEntity entity);
}
