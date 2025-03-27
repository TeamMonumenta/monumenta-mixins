package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


/**
 * @author Flowey
 * @mm-patch 0017-Monumenta-Disable-a-bunch-of-villager-AI.patch
 * <p>
 * Delete a lot of vanilla MC behaviour for villagers.
 */
@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
	public VillagerMixin(EntityType<? extends AbstractVillager> type, Level world) {
		super(type, world);
	}

	@Shadow
	public abstract VillagerData getVillagerData();

	@ModifyConstant(
		method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;" +
			"Lnet/minecraft/world/entity/npc/VillagerType;)V",
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
	private void registerBrainGoals(Brain<Villager> brain) {
		VillagerProfession profession = this.getVillagerData().getProfession();

		brain.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(profession, 0.5F));
		brain.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(profession, 0.5F));
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.setActiveActivityIfPossible(Activity.IDLE);
		brain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
	}
}
