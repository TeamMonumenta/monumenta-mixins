package com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.codegen.Linkable;
import com.playmonumenta.papermixins.mcfunction.execution.ControlInstr;
import com.playmonumenta.papermixins.mcfunction.execution.CustomExecSource;
import com.playmonumenta.papermixins.mcfunction.execution.StateEntry;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.BlockAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.execution.UnboundEntryAction;

/**
 * AST for a {@code loop ... { ... }} statement. This is a bit more involved than {@code run},
 * since it's recursive.
 * <br>
 * <h3>Pseudocode</h3>
 * <pre>
 * {@code
 * void anon() {
 *     var commandSources = runSelector();
 *     for(var source : commandSources) {
 *         runBody();
 *         anon();
 *     }
 * }
 * }
 * </pre>
 */
public class LoopAST extends ASTNode {
	private static class LoopFrame implements StateEntry {
		public final Deque<Integer> retStack = new ArrayDeque<>();
		public final Deque<IterateFrame> entries = new ArrayDeque<>();
	}

	private final BlockAST body;
	private final UnboundEntryAction<CommandSourceStack> statement;

	public LoopAST(BlockAST body, UnboundEntryAction<CommandSourceStack> statement) {
		this.body = body;
		this.statement = statement;
	}

	@Override
	public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
		final var wrapperFuncLabel = gen.defineLabel("cfv1$loop$wrapper_func");
		final var loopBeginLabel = gen.defineLabel("cfv1$loop$loop_begin");
		final var loopExitLabel = gen.defineLabel("cfv1$loop$loop_exit");
		final var endLabel = gen.defineLabel("cfv1$loop$end");

		gen.emitControlLinkable(List.of(endLabel), () -> {
			final var endTarget = endLabel.offset();
			return ControlInstr.named("cfv1::loop::setup", (state, context, frame) -> {
				final var loopFrame = new LoopFrame();
				loopFrame.retStack.push(endTarget);
				state.push(loopFrame);
			});
		});

		gen.emitLabel(wrapperFuncLabel);

		gen.emitControlNamed(
			"cfv1::loop::push_source_and_match",
			(state, context, frame) -> {
				final LoopFrame loopFrame = state.peek();
				final var sources = new ArrayList<CommandSourceStack>();
				loopFrame.entries.push(new IterateFrame(state.source, sources));

				statement.execute(
					new CustomExecSource<Consumer<CommandSourceStack>>(state.source, sources::add),
					context,
					frame
				);
			}
		);

		gen.emitLabel(loopBeginLabel);

		gen.emitControlLinkable(List.of(loopExitLabel), () -> {
			final var loopExitTarget = loopExitLabel.offset();
			return ControlInstr.named("cfv1::loop::pop_source_or_branch", (state, context, frame) -> {
				final LoopFrame loopFrame = state.peek();
				final var currIter = Objects.requireNonNull(loopFrame.entries.peek());

				if (!currIter.has()) {
					state.instr = loopExitTarget;
					return;
				}

				state.source = currIter.take();
			});
		});

		body.emit(diagnostics, cgCtx, gen);

		gen.emitControlLinkable(List.of(wrapperFuncLabel), () -> {
			final var target = wrapperFuncLabel.offset();
			return ControlInstr.named("cfv1::loop::recurse", (state, context, frame) -> {
				final LoopFrame loopFrame = state.peek();
				loopFrame.retStack.push(state.instr); // state.instr points to next
				state.instr = target;
			});
		});

		gen.emitLinkable(Linkable.branch(loopBeginLabel));

		gen.emitLabel(loopExitLabel);

		gen.emitControlNamed("cfv1::loop::function_exit", (state, context, frame) -> {
			final LoopFrame loopFrame = state.peek();

            // clean up state
			final var entry = loopFrame.entries.pop();
			state.source = entry.original();
			state.instr = loopFrame.retStack.pop();

			if(loopFrame.entries.isEmpty()) {
				state.pop();
			}
		});

		gen.emitLabel(endLabel);
	}

	@Override
	public void visit(Consumer<ASTNode> visitor) {
		visitor.accept(body);
	}

	@Override
	public String toString() {
		return "LoopAST[" + statement + "]";
	}
}
