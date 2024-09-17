package com.playmonumenta.papermixins.mcfunction.execution.instr;

import com.playmonumenta.papermixins.mcfunction.execution.FuncExecState;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

/**
 * Subroutine call instruction.
 * <h3>Pseudocode (assembly)</h3>
 * <pre>
 * {@code
 * PUSH[InstrAddress](%ip)
 * %ip = target
 * }
 * </pre>
 *
 * @param target The instruction to jump to.
 */
public record CallInstr<T>(int target) implements ControlInstr<T> {
    @Override
    public void modifyState(FuncExecState<T> state, ExecutionContext<T> context, Frame frame) {
        state.stack.pushInstrAddress(state.instr);
        state.instr = target;
    }

    @Override
    public String toString() {
        return "builtin::call[" + target + "]";
    }
}
