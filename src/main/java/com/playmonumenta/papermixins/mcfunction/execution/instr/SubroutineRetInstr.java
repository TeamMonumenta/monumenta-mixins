package com.playmonumenta.papermixins.mcfunction.execution.instr;

import com.playmonumenta.papermixins.mcfunction.execution.FuncExecState;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public class SubroutineRetInstr<T> implements ControlInstr<T> {
	private static final SubroutineRetInstr<?> INSTANCE = new SubroutineRetInstr<>();

	private SubroutineRetInstr() {

	}

	@SuppressWarnings("unchecked")
	public static <T> SubroutineRetInstr<T> get() {
		return (SubroutineRetInstr<T>) INSTANCE;
	}

	@Override
	public void modifyState(FuncExecState<T> state, ExecutionContext<T> context, Frame frame) {
		final var target = state.basePointerStack.popInt();
		if (state.stack.size() < target) {
			throw new IllegalStateException("Stack was popped too much");
		}

		while (state.stack.size() > target) {
			state.stack.discard();
		}

		state.popSource();
		state.instr = state.stack.popInstrAddress();
	}

	@Override
	public String toString() {
		return "subroutine::ret";
	}
}
