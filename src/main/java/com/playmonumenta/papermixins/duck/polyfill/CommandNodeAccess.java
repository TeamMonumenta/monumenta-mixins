package com.playmonumenta.papermixins.duck.polyfill;

import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.APICommandMeta;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

public interface CommandNodeAccess {
	default CommandNode<CommandSourceStack> monumenta$getUnwrappedCached() {
		throw new AbstractMethodError();
	}

	default void monumenta$setWrappedCached(CommandNode<CommandSourceStack> pureNode) {
		throw new AbstractMethodError();
	}

	default void monumenta$setUnwrappedCached(CommandNode<CommandSourceStack> converted) {
		throw new AbstractMethodError();
	}

	default void monuemnta$setWrappedCached(CommandNode<CommandSourceStack> shadow) {
		throw new AbstractMethodError();
	}

	default @Nullable CommandNode<CommandSourceStack> monumenta$getWrappedCached() {
		throw new AbstractMethodError();
	}

	default void monumenta$clearAll() {
		throw new AbstractMethodError();
	}

	default APICommandMeta monumenta$getApiCommandMeta() {
		throw new AbstractMethodError();
	}

	default void monumenta$removeCommand(String name) {
		throw new AbstractMethodError();
	}

	default void monumenta$setApiCommandMeta(APICommandMeta apiCommandMeta) {
		throw new AbstractMethodError();
	}
}
