package com.floweytf.monumentapaper.mixin.core.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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

import java.util.Random;

/**
 * @author Flowey
 * @mm-patch 0023-Monumenta-Fix-Generating-Loot-Table-Contents.patch
 * <p>
 * Fix generating loot table contents.
 */
@Mixin(CraftLootTable.class)
public class CraftLootTableMixin {
    @Inject(
        method = "convertContext(Lorg/bukkit/loot/LootContext;Ljava/util/Random;)" +
            "Lnet/minecraft/world/level/storage/loot/LootParams;",
        at = @At(
            value = "INVOKE",
            target = "Lorg/bukkit/loot/LootContext;getLootedEntity()Lorg/bukkit/entity/Entity;",
            ordinal = 0
        )
    )
    private void monumenta$addWithLuck(LootContext context, Random random, CallbackInfoReturnable<LootParams> cir,
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
    private LootContextParamSet.Builder monumenta$makeRequiredOptional(LootContextParamSet.Builder instance,
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
    private boolean monumenta$alwaysAddParam(boolean original) { // :3
        return true;
    }
}