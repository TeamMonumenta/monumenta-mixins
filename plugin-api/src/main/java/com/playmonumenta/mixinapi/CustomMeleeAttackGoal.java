package com.playmonumenta.mixinapi;

import org.bukkit.entity.LivingEntity;

public interface CustomMeleeAttackGoal {
	boolean isWithinAttackRange(LivingEntity entity);
}
