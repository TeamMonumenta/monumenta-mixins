package com.playmonumenta.papermixins.mcfunction.execution;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class FuncExecState<T> {
	private final Deque<StateEntry> stack = new ArrayDeque<>();

	@NotNull
	public T source;
	public int instr = 0;

	public FuncExecState(@NotNull T source) {
		this.source = source;
	}

	public void push(StateEntry entry) {
		stack.push(entry);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public <F extends StateEntry> F peek() {
		return (F) Objects.requireNonNull(stack.peek());
	}

	public void pop() {
		stack.pop();
	}

	@Override
	public String toString() {
		return String.format("[instr = %d, stack = %s]", instr, stack);
	}
}
