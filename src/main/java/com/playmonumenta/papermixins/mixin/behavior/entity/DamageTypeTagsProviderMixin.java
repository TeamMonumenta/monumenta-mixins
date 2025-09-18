package com.playmonumenta.papermixins.mixin.behavior.entity;

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

import java.util.concurrent.CompletableFuture;

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
        this.tag(DamageTypeTags.NO_IMPACT).add(DamageTypes.DROWN);
    }
}
