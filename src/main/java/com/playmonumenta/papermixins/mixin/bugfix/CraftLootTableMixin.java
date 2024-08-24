package com.playmonumenta.papermixins.mixin.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Random;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.bukkit.craftbukkit.v1_20_R3.CraftLootTable;
import org.bukkit.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0023-Monumenta-Fix-Generating-Loot-Table-Contents.patch
 * <p>
 * Fix generating loot table contents.
 */
@Mixin(CraftLootTable.class)
public class CraftLootTableMixin {
    /// <parameter minecraft:explosion_radius>,
    // <parameter minecraft:direct_killer_entity>,
    // <parameter minecraft:tool>,
    // <parameter minecraft:block_state>,
    // <parameter minecraft:killer_entity>
    // , <parameter minecraft:block_entity>,
    // <parameter minecraft:last_damage_player>,
    // <parameter minecraft:this_entity>,
    // <parameter minecraft:damage_source>

    @Inject(
        method = "convertContext(Lorg/bukkit/loot/LootContext;Ljava/util/Random;)" +
            "Lnet/minecraft/world/level/storage/loot/LootParams;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/bukkit/loot/LootContext;getLootedEntity()Lorg/bukkit/entity/Entity;",
            ordinal = 0
        )
    )
    private void addWithLuck(LootContext context, Random random, CallbackInfoReturnable<LootParams> cir,
                             @Local LootParams.Builder builder) {
        builder.withLuck(context.getLuck());
    }

    @Redirect(
        method = "convertContext(Lorg/bukkit/loot/LootContext;Ljava/util/Random;)" +
            "Lnet/minecraft/world/level/storage/loot/LootParams;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/parameters/LootContextParamSet$Builder;required" +
                "(Lnet/minecraft/world/level/storage/loot/parameters/LootContextParam;)" +
                "Lnet/minecraft/world/level/storage/loot/parameters/LootContextParamSet$Builder;"
        )
    )
    private LootContextParamSet.Builder makeRequiredOptional(LootContextParamSet.Builder instance,
                                                             LootContextParam<?> parameter) {
        return instance.optional(parameter);
    }

    @ModifyExpressionValue(
        method = "convertContext(Lorg/bukkit/loot/LootContext;Ljava/util/Random;)" +
            "Lnet/minecraft/world/level/storage/loot/LootParams;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
        )
    )
    private boolean alwaysAddParam(boolean original) { // :3
        return false;
    }

    @Redirect(
        method = "convertContext(Lorg/bukkit/loot/LootContext;Ljava/util/Random;)" +
            "Lnet/minecraft/world/level/storage/loot/LootParams;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;create" +
                "(Lnet/minecraft/world/level/storage/loot/parameters/LootContextParamSet;)" +
                "Lnet/minecraft/world/level/storage/loot/LootParams;"
        )
    )
    private LootParams useNMSBuilder(LootParams.Builder instance, LootContextParamSet contextType,
                                     @Local LootContextParamSet.Builder nmsBuilder) {
        return instance.create(nmsBuilder.build());
    }
}
