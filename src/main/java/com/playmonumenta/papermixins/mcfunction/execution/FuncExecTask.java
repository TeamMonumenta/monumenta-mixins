package com.playmonumenta.papermixins.mcfunction.execution;

import com.playmonumenta.papermixins.MonumentaMod;
import java.util.List;
import net.minecraft.commands.execution.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Rewrite of minecraft's continuation task to support control flow.
 *
 * @see FuncExecState
 */
public class FuncExecTask<T> implements EntryAction<T> {
	private static final Logger LOGGER = MonumentaMod.getLogger("FunctionExecutor");
	private final List<UnboundEntryAction<T>> actions;
	private final CommandQueueEntry<T> selfEntry;
	private final FuncExecState<T> state;

	private FuncExecTask(List<UnboundEntryAction<T>> actions, Frame frame,
						T initialSource) {
		this.actions = actions;
		this.selfEntry = new CommandQueueEntry<T>(frame, this);
		this.state = new FuncExecState<>(initialSource);
	}

	public static <T> void schedule(ExecutionContext<T> c, Frame f,
									List<UnboundEntryAction<T>> actions, T source) {
		if (actions.isEmpty())
			return;

		c.queueNext((new FuncExecTask<>(actions, f, source)).selfEntry);
	}

	@Override
	public void execute(@NotNull ExecutionContext<T> exec, @NotNull Frame frame) {
		if (state.instr >= actions.size()) {
			return;
		}

		if (state.instr < 0) {
			LOGGER.warn("state index out of bounds");
			return;
		}

		final var task = actions.get(state.instr);

		if (task instanceof ControlInstr<T> control) {
			// special instructions
			exec.queueNext(new CommandQueueEntry<>(
				frame,
				new EntryAction<>() {
					@Override
					public void execute(@NotNull ExecutionContext<T> c, @NotNull Frame f) {
						control.execute(state.source, c, f);
						state.instr++;
						control.modifyState(state, c, f);
					}

					@Override
					public String toString() {
						return control.toString();
					}
				}
			));
			exec.queueNext(selfEntry);
		} else {
			exec.queueNext(new CommandQueueEntry<>(frame, task.bind(state.source)));
			state.instr++;
			exec.queueNext(selfEntry);
		}
	}

	@Override
	public String toString() {
		return "FuncExecTask" + state.toString();
	}
}
