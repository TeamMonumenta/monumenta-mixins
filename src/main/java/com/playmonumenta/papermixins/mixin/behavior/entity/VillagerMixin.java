package com.playmonumenta.papermixins.mixin.behavior.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * @author Flowey
 * @mm-patch 0017-Monumenta-Disable-a-bunch-of-villager-AI.patch
 * <p>
 * Delete a lot of vanilla MC behavior for villagers.
 */
@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
	public VillagerMixin(EntityType<? extends AbstractVillager> type, Level world) {
		super(type, world);
	}

	@ModifyConstant(
		method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Holder;)V",
		constant = @Constant(intValue = 1, ordinal = 0)
	)
	private int disableOpeningDoors(int constant) {
		// False == 0 according to JVM
		return 0;
	}

	/**
	 * @author Flowey
	 * @reason Disable more AI goals.
	 */
	@Overwrite
	private static List<ActivityData<Villager>> lambda$static$0(Villager body) {
		Holder<VillagerProfession> profession = body.getVillagerData().profession();
		List<ActivityData<Villager>> activities = new ArrayList<>();
		activities.add(ActivityData.create(Activity.CORE, VillagerGoalPackages.getCorePackage(profession, 0.5F)));
		activities.add(ActivityData.create(Activity.IDLE, VillagerGoalPackages.getIdlePackage(0.5F)));
		return activities;
	}
}
