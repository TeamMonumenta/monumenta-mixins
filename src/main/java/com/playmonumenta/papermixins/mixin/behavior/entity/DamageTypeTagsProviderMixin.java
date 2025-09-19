package com.playmonumenta.papermixins.mixin.behavior.entity;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTypeTagsProvider.class)
public abstract class DamageTypeTagsProviderMixin extends TagsProvider<DamageType> {

	public DamageTypeTagsProviderMixin(PackOutput output, CompletableFuture<HolderLookup.Provider> maxChainedNeighborUpdates) {
		super(output, Registries.DAMAGE_TYPE, maxChainedNeighborUpdates);
	}

	/**
	 * @author ashphyx
	 * @reason Disable damage flinching.
	 */
	@Inject(
			method = "addTags",
			at = @At(value = "TAIL")
	)
	private void addNoImpact(HolderLookup.Provider lookup, CallbackInfo ci) {
		this.tag(DamageTypeTags.NO_IMPACT).add(
				DamageTypes.IN_FIRE,
				DamageTypes.LIGHTNING_BOLT,
				DamageTypes.ON_FIRE,
				DamageTypes.LAVA,
				DamageTypes.HOT_FLOOR,
				DamageTypes.IN_WALL,
				DamageTypes.CRAMMING,
//                DamageTypes.DROWN,
				DamageTypes.STARVE,
				DamageTypes.CACTUS,
				DamageTypes.FALL,
				DamageTypes.FLY_INTO_WALL,
				DamageTypes.FELL_OUT_OF_WORLD,
				DamageTypes.GENERIC,
				DamageTypes.MAGIC,
				DamageTypes.WITHER,
				DamageTypes.DRAGON_BREATH,
				DamageTypes.DRY_OUT,
				DamageTypes.SWEET_BERRY_BUSH,
				DamageTypes.FREEZE,
				DamageTypes.STALAGMITE,
				DamageTypes.FALLING_BLOCK,
				DamageTypes.FALLING_ANVIL,
				DamageTypes.FALLING_STALACTITE,
				DamageTypes.STING,
				DamageTypes.MOB_ATTACK,
				DamageTypes.MOB_ATTACK_NO_AGGRO,
				DamageTypes.PLAYER_ATTACK,
				DamageTypes.ARROW,
				DamageTypes.TRIDENT,
				DamageTypes.MOB_PROJECTILE,
				DamageTypes.FIREWORKS,
				DamageTypes.UNATTRIBUTED_FIREBALL,
				DamageTypes.FIREBALL,
				DamageTypes.WITHER_SKULL,
				DamageTypes.THROWN,
				DamageTypes.INDIRECT_MAGIC,
				DamageTypes.THORNS,
				DamageTypes.EXPLOSION,
				DamageTypes.PLAYER_EXPLOSION,
				DamageTypes.SONIC_BOOM,
				DamageTypes.BAD_RESPAWN_POINT,
				DamageTypes.OUTSIDE_BORDER,
				DamageTypes.GENERIC_KILL
		);
	}
}
