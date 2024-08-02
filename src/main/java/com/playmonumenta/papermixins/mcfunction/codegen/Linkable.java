package com.playmonumenta.papermixins.mcfunction.codegen;

import com.playmonumenta.papermixins.mcfunction.execution.instr.BranchInstr;
import com.playmonumenta.papermixins.mcfunction.execution.instr.CallInstr;
import com.playmonumenta.papermixins.mcfunction.execution.instr.ControlInstr;
import com.playmonumenta.papermixins.mcfunction.execution.instr.PushInstrAddrInstr;
import net.minecraft.commands.execution.UnboundEntryAction;

import java.util.List;
import java.util.function.Supplier;

public interface Linkable<T> {
    default List<Label> targets() {
        return List.of();
    }

    UnboundEntryAction<T> link();

    static <T> Linkable<T> call(Label target) {
        return wrap(List.of(target), () -> new CallInstr<>(target.offset()));
    }

    static <T> Linkable<T> branch(Label target) {
        return wrap(List.of(target), () -> new BranchInstr<T>(target.offset()));
    }

    static <T> Linkable<T> pushInstrAddr(Label target) {
        return wrap(List.of(target), () -> new PushInstrAddrInstr<T>(target.offset()));
    }

    static <T> Linkable<T> exit() {
        return wrap(() -> new BranchInstr<T>(Integer.MAX_VALUE));
    }

    static <T> Linkable<T> wrap(Supplier<ControlInstr<T>> gen) {
        return gen::get;
    }

    static <T> Linkable<T> wrap(List<Label> targets, Supplier<ControlInstr<T>> gen) {
        return new Linkable<>() {
            @Override
            public List<Label> targets() {
                return targets;
            }

            @Override
            public UnboundEntryAction<T> link() {
                return gen.get();
            }
        };
    }
}
