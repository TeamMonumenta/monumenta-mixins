package com.playmonumenta.papermixins.mixin.optimize;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.v1_20_R3.scheduler.CraftAsyncScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftAsyncScheduler.class)
public class CraftAsyncSchedulerMixin {
	@Unique
	private final ExecutorService monumenta$threadPool = Executors.newThreadPerTaskExecutor(
		Thread.ofVirtual()
			.name("Craft Scheduler VThread - ", 0)
			.uncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(MinecraftServer.LOGGER))
			.factory()
	);

	@ModifyExpressionValue(
		method = "<init>",
		at = @At(value = "NEW", target = "(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;" +
			"Ljava/util/concurrent/ThreadFactory;)Ljava/util/concurrent/ThreadPoolExecutor;")
	)
	private ThreadPoolExecutor replaceExecutor(ThreadPoolExecutor original) {
		return null;
	}

	@WrapOperation(
		method = "<init>",
		at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;allowCoreThreadTimeOut(Z)V")
	)
	private void noop0(ThreadPoolExecutor instance, boolean value, Operation<Void> original) {
	}

	@WrapOperation(
		method = "<init>",
		at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;prestartAllCoreThreads()I")
	)
	private int noop1(ThreadPoolExecutor instance, Operation<Integer> original) {
		return 0;
	}

	@Redirect(
		method = "executeTask",
		at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;execute(Ljava/lang/Runnable;)V")
	)
	private void scheduleOnVirtualThreads(ThreadPoolExecutor instance, Runnable command) {
		monumenta$threadPool.execute(command);
	}
}
