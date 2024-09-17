package com.playmonumenta.papermixins.mcfunction.execution.instr;

import com.playmonumenta.papermixins.mcfunction.execution.FuncExecState;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public record PushInstrAddrInstr<T>(int target) implements ControlInstr<T> {
    @Override
    public void modifyState(FuncExecState<T> state, ExecutionContext<T> context, Frame frame) {
        state.stack.pushInstrAddress(target);
    }

    @Override
    public String toString() {
        return "builtin::push::InstrAddr[" + target + "]";
    }
}
