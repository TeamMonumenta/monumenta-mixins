package com.playmonumenta.papermixins.paperapi.v1;

import org.bukkit.entity.LivingEntity;

public interface CustomMeleeAttackGoal {
	boolean isWithinAttackRange(LivingEntity entity);
}
