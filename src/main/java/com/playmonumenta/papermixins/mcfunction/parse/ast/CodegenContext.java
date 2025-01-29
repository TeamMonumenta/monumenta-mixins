package com.playmonumenta.papermixins.mcfunction.parse.ast;

import com.playmonumenta.papermixins.mcfunction.codegen.Label;
import com.playmonumenta.papermixins.mcfunction.execution.FuncExecState;
import com.playmonumenta.papermixins.util.RAII;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

public final class CodegenContext {
	private final Map<String, Label> subroutines = new HashMap<>();
	private final List<Label> breakTargets = new ArrayList<>();

	public Map<String, Label> subroutines() {
		return subroutines;
	}

	public RAII visitBreakable(Label breakable) {
        breakTargets.add(breakable);
		return () -> breakTargets.remove(breakTargets.size() - 1);
	}

	public List<Label> getBreakables() {
		return Collections.unmodifiableList(breakTargets);
	}
}
