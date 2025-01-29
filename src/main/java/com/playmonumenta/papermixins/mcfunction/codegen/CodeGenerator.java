package com.playmonumenta.papermixins.mcfunction.codegen;

import com.playmonumenta.papermixins.mcfunction.execution.ControlInstr;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.functions.PlainTextFunction;
import net.minecraft.resources.ResourceLocation;

/**
 * The main code generation class, used to emit "instructions." Supports address labels for advanced control flow
 * constructs.
 */
public class CodeGenerator<T extends ExecutionCommandSource<T>> {
	protected final List<IntObjectPair<Linkable<T>>> linkables = new ArrayList<>();
	protected final List<UnboundEntryAction<T>> entries = new ArrayList<>();
	private int currLabelId = 0;

	public Label defineLabel(String name) {
		return new Label(currLabelId++, name);
	}

	public Label defineLabel() {
		return new Label(currLabelId++, null);
	}

	public void emitPlain(UnboundEntryAction<T> instr) {
		entries.add(instr);
	}

	public void emitControl(ControlInstr<T> instr) {
		entries.add(instr);
	}

	public void emitControlNamed(String name, ControlInstr.StateModifier<T> instr) {
		emitControl(ControlInstr.named(name, instr));
	}

	public void emitLinkable(Linkable<T> linkable) {
		linkables.add(IntObjectPair.of(nextInstrIndex(), linkable));
		entries.add(null);
	}

	public void emitControlLinkable(Supplier<ControlInstr<T>> gen) {
		emitLinkable(Linkable.wrap(gen));
	}

	public void emitControlLinkable(List<Label> targets, Supplier<ControlInstr<T>> gen) {
		emitLinkable(Linkable.wrap(targets, gen));
	}

	public void emitLabel(Label label) {
		label.offset = nextInstrIndex();
	}

	public int nextInstrIndex() {
		return entries.size();
	}

	public PlainTextFunction<T> define(ResourceLocation id) {
		// linking :3
		for (var linkableInfo : linkables) {
			entries.set(linkableInfo.firstInt(), linkableInfo.second().link());
		}

		return new PlainTextFunction<>(id, entries);
	}

	public String dumpDisassembly() {
		throw new IllegalStateException();
	}
}
