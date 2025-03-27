package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Flowey
 * @mm-patch 0017-Monumenta-Disable-a-bunch-of-villager-AI.patch
 * <p>
 * Delete a lot of vanilla MC behaviour for villagers.
 */
@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {
	@Shadow
	private static Pair<Integer, BehaviorControl<LivingEntity>> getFullLookBehavior() {
		throw new AssertionError();
	}

	/**
	 * @author Flowey
	 * @reason Disable a few POI things.
	 */
	@Overwrite
	public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getCorePackage(VillagerProfession profession, float speed) {
		return ImmutableList.of(Pair.of(0, new LookAtTargetSink(45, 90)), Pair.of(3,
			new LookAndFollowTradingPlayerSink(speed)));
	}

	/**
	 * @author Flowey
	 * @reason Disable a few idle rules.
	 */
	@Overwrite
	public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getIdlePackage(VillagerProfession profession, float speed) {
		return ImmutableList.of(
			Pair.of(2, new RunOne<>(ImmutableList.of(Pair.of(new DoNothing(30, 60), 1)))),
			Pair.of(3, SetLookAndInteract.create(EntityType.PLAYER, 4)),
			Pair.of(3, new ShowTradesToPlayer(400, 1600)),
			Pair.of(3,
				new GateBehavior<>(
					ImmutableMap.of(),
					ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
					GateBehavior.OrderPolicy.ORDERED,
					GateBehavior.RunningPolicy.RUN_ONE,
					ImmutableList.of(Pair.of(new TradeWithVillager(), 1))
				)
			),
			getFullLookBehavior(),
			Pair.of(99, UpdateActivityFromSchedule.create())
		);
	}
}
