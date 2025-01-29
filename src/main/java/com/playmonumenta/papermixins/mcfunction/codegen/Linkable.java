package com.playmonumenta.papermixins.mcfunction.codegen;

import com.playmonumenta.papermixins.mcfunction.execution.BranchInstr;
import com.playmonumenta.papermixins.mcfunction.execution.ControlInstr;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.commands.execution.UnboundEntryAction;

public interface Linkable<T> {
	static <T> Linkable<T> branch(Label target) {
		return wrap(List.of(target), () -> new BranchInstr<>(target.offset()));
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

	default List<Label> targets() {
		return List.of();
	}

	UnboundEntryAction<T> link();
}
