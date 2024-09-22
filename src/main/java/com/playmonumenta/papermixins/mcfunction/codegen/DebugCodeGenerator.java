package com.playmonumenta.papermixins.mcfunction.codegen;

import com.playmonumenta.papermixins.mcfunction.execution.instr.ControlInstr;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.functions.PlainTextFunction;
import net.minecraft.resources.ResourceLocation;

public class DebugCodeGenerator<T extends ExecutionCommandSource<T>> extends CodeGenerator<T> {
	private final List<String> disassembly = new ArrayList<>();
	private final IntList linkableDisassemblyIndex = new IntArrayList();

	@Override
	public void emitPlain(UnboundEntryAction<T> instr) {
		disassembly.add("  RUN " + instr);
		super.emitPlain(instr);
	}

	@Override
	public void emitControl(ControlInstr<T> instr) {
		disassembly.add("  " + instr);
		super.emitControl(instr);
	}

	@Override
	public void emitLinkable(Linkable<T> linkable) {
		// Logic here is a bit cursed
		linkableDisassemblyIndex.add(disassembly.size());
		disassembly.add(null);
		super.emitLinkable(linkable);
	}

	@Override
	public void emitLabel(Label label) {
		disassembly.add(label + ":");
		super.emitLabel(label);
	}

	@Override
	public PlainTextFunction<T> define(ResourceLocation id) {
		// linking :3
		for (int i = 0; i < linkables.size(); i++) {
			var linkableInfo = linkables.get(i);
			final var result = linkableInfo.second().link();

			entries.set(linkableInfo.firstInt(), linkableInfo.second().link());

			disassembly.set(linkableDisassemblyIndex.getInt(i), "  " + result + " [" + String.join(", ",
				linkableInfo.second().targets().stream().map(Label::toString).toList()) + "]");
		}

		return new PlainTextFunction<>(id, entries);
	}

	public String dumpDisassembly() {
		return String.join("\n", disassembly);
	}
}
