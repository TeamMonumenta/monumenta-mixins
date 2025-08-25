package com.playmonumenta.papermixins.mixin.impl.polyfill;

import static io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent.Cause;

import io.papermc.paper.command.brigadier.PaperCommands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventRunner;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
	@Shadow
	public Commands commands;

	@Inject(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/ReloadableServerResources;commands:Lnet/minecraft/commands/Commands;",
			shift = At.Shift.AFTER
		)
	)
	private void setPaperCommandDispatcher(
		RegistryAccess.Frozen registries,
		FeatureFlagSet enabledFeatures,
		Commands.CommandSelection environment,
		int functionPermissionLevel, CallbackInfo ci
	) {
		PaperCommands.INSTANCE.setDispatcher(this.commands,
			CommandBuildContext.simple(registries, enabledFeatures));
	}

	@SuppressWarnings("UnstableApiUsage")
	@Inject(
		method = "loadResources",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create" +
				"(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;" +
				"Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;" +
				"Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;")
	)
	private static void invokeLifecycleEvent(
		ResourceManager manager, RegistryAccess.Frozen dynamicRegistryManager,
		FeatureFlagSet enabledFeatures, Commands.CommandSelection environment,
		int functionPermissionLevel, Executor prepareExecutor,
		Executor applyExecutor,
		CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir
	) {
		LifecycleEventRunner.INSTANCE.callReloadableRegistrarEvent(
			LifecycleEvents.COMMANDS,
			PaperCommands.INSTANCE,
			BootstrapContext.class,
			MinecraftServer.getServer() == null ? Cause.INITIAL : Cause.RELOAD
		);
	}
}
