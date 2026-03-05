package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.ConfigManager;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Dolphin.class)
public class DolphinMixin {
    // prevent dolphins from going after dropped items
    @Redirect(method = "registerGoals()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 9))
    private void cancelPlayWithItemsGoal(GoalSelector instance, int priority, Goal goal) {
        if(!ConfigManager.getConfig().behavior.disableDolphinPlayWithItems) {
            instance.addGoal(priority, goal);
        }
    }
}
