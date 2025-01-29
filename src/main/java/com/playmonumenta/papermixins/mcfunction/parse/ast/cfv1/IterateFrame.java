package com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1;

import com.playmonumenta.papermixins.mcfunction.execution.StateEntry;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;

public final class IterateFrame implements StateEntry {
	private final CommandSourceStack original;
	private final List<CommandSourceStack> sources;
	private int index = 0;

	IterateFrame(CommandSourceStack original, List<CommandSourceStack> sources) {
		this.original = original;
		this.sources = sources;
	}

	public CommandSourceStack original() {
		return original;
	}

	public CommandSourceStack take() {
		return sources.get(index++);
	}

	public boolean has() {
		return index < sources.size();
	}
}
