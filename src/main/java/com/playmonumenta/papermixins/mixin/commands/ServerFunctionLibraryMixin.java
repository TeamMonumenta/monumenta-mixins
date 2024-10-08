package com.playmonumenta.papermixins.mixin.commands;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.playmonumenta.papermixins.mcfunction.Compiler;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0018-Monumenta-Ensure-minecraft-reload-uses-latest-Brigad.patch
 * <p>
 * Remove a bunch of CommandAPI errors.
 */
@Mixin(ServerFunctionLibrary.class)
public class ServerFunctionLibraryMixin {
	@Shadow
	@Final
	private static Logger LOGGER;
	@Unique
	private static boolean monumenta$isInitialFunctionLoad = true;

	@Redirect(
		method = "lambda$reload$1",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/FileToIdConverter;listMatchingResources" +
				"(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
		)
	)
	private static Map<ResourceLocation, Resource> disableFunctionOnFirstLoad(FileToIdConverter instance,
																			ResourceManager resourceManager) {
		if (monumenta$isInitialFunctionLoad) {
			LOGGER.info("Skipping function loading since this is the initial function load!");
			return Map.of();
		} else {
			return instance.listMatchingResources(resourceManager);
		}
	}

	@Inject(
		method = "lambda$reload$5",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)" +
				"Lcom/google/common/collect/ImmutableMap$Builder;"
		)
	)
	private static void redirectFunctionParsing(ResourceLocation resourceLocation,
												ImmutableMap.Builder<?, ?> builder,
												CommandFunction<?> function, Throwable ex,
												CallbackInfoReturnable<Object> cir) {
		if (function == null) {
			LOGGER.error("Failed to parse function '{}'! See logs for details.", resourceLocation);
		}
	}

	@WrapOperation(
		method = "lambda$reload$5",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)" +
				"Lcom/google/common/collect/ImmutableMap$Builder;"
		)
	)
	private static ImmutableMap.Builder<?, ?> redirectFunctionParsing(ImmutableMap.Builder<?, ?> instance,
																	Object key, Object value,
																	Operation<ImmutableMap.Builder<?, ?>> original) {
		if (value != null) {
			original.call(instance, key, value);
		}

		return instance;
	}

	@Inject(
		method = "lambda$reload$7",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"
		)
	)
	private void onReload(Pair<?, ?> intermediate, CallbackInfo ci) {
		monumenta$isInitialFunctionLoad = false;
	}

	@SuppressWarnings("unchecked")
	@Redirect(
		method = "lambda$reload$2",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/commands/functions/CommandFunction;fromLines" +
				"(Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/brigadier/CommandDispatcher;" +
				"Lnet/minecraft/commands/ExecutionCommandSource;Ljava/util/List;)" +
				"Lnet/minecraft/commands/functions/CommandFunction;"
		)
	)
	private <T extends ExecutionCommandSource<T>> CommandFunction<T> redirectFunctionParsing(
		ResourceLocation id, CommandDispatcher<T> dispatcher, T source,
		List<String> lines, @Local(argsOnly = true) Map.Entry<ResourceLocation, Resource> map
	) {
		return (CommandFunction<T>) Compiler.compileFunction(
			(CommandDispatcher<CommandSourceStack>) dispatcher,
			(CommandSourceStack) source, lines, id, map.getValue().sourcePackId()
		);
	}
}
