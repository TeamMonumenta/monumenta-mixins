package com.playmonumenta.papermixins.mixin.behavior.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author Flowey
 * @mm-patch 0017-Monumenta-Disable-a-bunch-of-villager-AI.patch
 * <p>
 * Delete a lot of vanilla MC behaviour for villagers.
 */
@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {
    public WanderingTraderMixin(EntityType<? extends AbstractVillager> type, Level world) {
        super(type, world);
    }

    /**
     * @author Flowey
     * @reason Remove AI.
     */
    @Overwrite
    public void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }
}
