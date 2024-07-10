package com.floweytf.monumentapaper.api;

import org.bukkit.entity.LivingEntity;

public interface CustomMeleeAttackGoal {
    boolean isWithinAttackRange(LivingEntity entity);
}
