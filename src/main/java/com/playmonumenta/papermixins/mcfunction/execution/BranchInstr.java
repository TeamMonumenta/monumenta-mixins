package com.playmonumenta.papermixins.mcfunction.execution;

import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public record BranchInstr<T>(int target) implements ControlInstr<T> {
	private static final BranchInstr<?> EXIT = new BranchInstr<>(Integer.MAX_VALUE);

	@SuppressWarnings("unchecked")
	public static <T> BranchInstr<T> exit() {
		return (BranchInstr<T>) EXIT;
	}

	@Override
	public void modifyState(FuncExecState<T> state, ExecutionContext<T> context, Frame frame) {
		state.instr = target;
	}

	@Override
	public String toString() {
		return "builtin::br[" + target + "]";
	}
}
