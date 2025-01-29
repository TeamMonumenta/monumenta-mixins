package com.playmonumenta.papermixins;

import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;

public class MixinState {
	public static final ThreadLocal<Function<? super Double, Double>> IFRAME_FUNC = new ThreadLocal<>();
	public static final ThreadLocal<Double> IFRAME_VALUE = new ThreadLocal<>();
	public static List<CommandNode<CommandSourceStack>> EXECUTE_CHILDREN_COMMANDS;
}
