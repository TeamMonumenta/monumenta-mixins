package com.playmonumenta.papermixins.mixin.commands;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
import net.minecraft.resources.Identifier;
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
	private static Map<Identifier, Resource> disableFunctionOnFirstLoad(FileToIdConverter instance,
																			ResourceManager manager) {
		if (monumenta$isInitialFunctionLoad) {
			LOGGER.info("Skipping function loading since this is the initial function load!");
			return Map.of();
		} else {
			return instance.listMatchingResources(manager);
		}
	}

	@Inject(
		method = "lambda$reload$5",
		at = @At(value = "TAIL")
	)
	private static void onReload(Pair<?, ?> data, CallbackInfo ci) {
		monumenta$isInitialFunctionLoad = false;
	}

	@SuppressWarnings("unchecked")
	@Redirect(
		method = "lambda$reload$3",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/commands/functions/CommandFunction;fromLines" +
				"(Lnet/minecraft/resources/Identifier;Lcom/mojang/brigadier/CommandDispatcher;" +
				"Lnet/minecraft/commands/ExecutionCommandSource;Ljava/util/List;)" +
				"Lnet/minecraft/commands/functions/CommandFunction;"
		)
	)
	private <T extends ExecutionCommandSource<T>> CommandFunction<T> redirectFunctionParsing(
		Identifier id, CommandDispatcher<T> dispatcher, T source,
		List<String> lines, @Local(argsOnly = true) Map.Entry<Identifier, Resource> map
	) {
		return (CommandFunction<T>) Compiler.compileFunction(
			(CommandDispatcher<CommandSourceStack>) dispatcher,
			(CommandSourceStack) source, lines, id, map.getValue().sourcePackId()
		);
	}

	@Expression("? != null")
	@ModifyExpressionValue(
		method = "lambda$reload$7",
		at = @At(
			"MIXINEXTRAS:EXPRESSION"
		)
	)
	private static boolean modifyPredicate(boolean original, @Local(argsOnly = true) CommandFunction<?> function) {
		return function == null;
	}
}
