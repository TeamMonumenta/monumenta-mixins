package com.playmonumenta.papermixins.mcfunction.execution;

import java.util.function.BinaryOperator;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CustomExecSource<T> extends CommandSourceStack {
	private final T state;

	public CustomExecSource(CommandSourceStack stack, T state) {
		super(
			stack.source,
			stack.getPosition(),
			stack.getRotation(),
			stack.getLevel(),
			stack.permissionLevel,
			stack.getTextName(),
			stack.getDisplayName(),
			stack.getServer(),
			stack.getEntity(),
			stack.isSilent(),
			stack.callback(),
			stack.getAnchor(),
			stack.getSigningContext(),
			stack.getChatMessageChainer()
		);

		this.state = state;
	}

	public T getState() {
		return state;
	}

	@Override
	public @NotNull CustomExecSource<T> withSource(@NotNull CommandSource output) {
		return new CustomExecSource<>(super.withSource(output), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withEntity(@NotNull Entity entity) {
		return new CustomExecSource<>(super.withEntity(entity), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withPosition(@NotNull Vec3 position) {
		return new CustomExecSource<T>(super.withPosition(position), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withRotation(@NotNull Vec2 rotation) {
		return new CustomExecSource<T>(super.withRotation(rotation), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withCallback(@NotNull CommandResultCallback returnValueConsumer) {
		return new CustomExecSource<T>(super.withCallback(returnValueConsumer), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withCallback(@NotNull CommandResultCallback returnValueConsumer,
													@NotNull BinaryOperator<CommandResultCallback> merger) {
		return new CustomExecSource<T>(super.withCallback(returnValueConsumer, merger), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withSuppressedOutput() {
		return new CustomExecSource<T>(super.withSuppressedOutput(), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withPermission(int level) {
		return new CustomExecSource<T>(super.withPermission(level), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withMaximumPermission(int level) {
		return new CustomExecSource<T>(super.withMaximumPermission(level), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withAnchor(@NotNull EntityAnchorArgument.Anchor anchor) {
		return new CustomExecSource<T>(super.withAnchor(anchor), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withLevel(@NotNull ServerLevel world) {
		return new CustomExecSource<T>(super.withLevel(world), state);
	}

	@Override
	public @NotNull CustomExecSource<T> withSigningContext(
		@NotNull CommandSigningContext args,
		@NotNull TaskChainer queue
	) {
		return new CustomExecSource<T>(super.withSigningContext(args, queue), state);
	}
}
