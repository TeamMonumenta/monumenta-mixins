package com.playmonumenta.papermixins.mcfunction.execution.instr;

import com.playmonumenta.papermixins.mcfunction.execution.FuncExecState;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

/**
 * Subroutine return instruction.
 * <h3>Pseudocode (assembly)</h3>
 * <pre>
 * {@code
 * %ip = POP[InstrAddress]()
 * }
 * </pre>
 **/
public class RetInstr<T> implements ControlInstr<T> {
	private static final RetInstr<?> INSTANCE = new RetInstr<>();

	private RetInstr() {

	}

	@SuppressWarnings("unchecked")
	public static <T> RetInstr<T> get() {
		return (RetInstr<T>) INSTANCE;
	}

	@Override
	public void modifyState(FuncExecState<T> state, ExecutionContext<T> context, Frame frame) {
		state.instr = state.stack.popInstrAddress();
	}

	@Override
	public String toString() {
		return "builtin::ret";
	}
}
