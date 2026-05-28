package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;
import org.bukkit.craftbukkit.CraftLootTable;
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
	@Inject(
		method = "convertContext(Lorg/bukkit/loot/LootContext;)Lnet/minecraft/world/level/storage/loot/LootParams;",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/loot/LootContext;getLootedEntity()Lorg/bukkit/entity/Entity;",
			ordinal = 0
		)
	)
	private void addWithLuck(LootContext context, CallbackInfoReturnable<LootParams> cir,
							 @Local(name = "builder") LootParams.Builder builder) {
		builder.withLuck(context.getLuck());
	}

	@Redirect(
		method = "convertContext(Lorg/bukkit/loot/LootContext;)Lnet/minecraft/world/level/storage/loot/LootParams;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/context/ContextKeySet$Builder;required" +
				"(Lnet/minecraft/util/context/ContextKey;)Lnet/minecraft/util/context/ContextKeySet$Builder;"
		)
	)
	private ContextKeySet.Builder makeRequiredOptional(ContextKeySet.Builder instance, ContextKey<?> param) {
		return instance.optional(param);
	}

	@ModifyExpressionValue(
		method = "convertContext(Lorg/bukkit/loot/LootContext;)Lnet/minecraft/world/level/storage/loot/LootParams;",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
		)
	)
	private boolean alwaysAddParam(boolean original) { // :3
		return false;
	}

	@Redirect(
		method = "convertContext(Lorg/bukkit/loot/LootContext;)Lnet/minecraft/world/level/storage/loot/LootParams;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;create" +
				"(Lnet/minecraft/util/context/ContextKeySet;)Lnet/minecraft/world/level/storage/loot/LootParams;"
		)
	)
	private LootParams useNMSBuilder(LootParams.Builder instance, ContextKeySet contextKeySet,
									 @Local(name = "nmsBuilder") ContextKeySet.Builder nmsBuilder) {
		return instance.create(nmsBuilder.build());
	}
}
