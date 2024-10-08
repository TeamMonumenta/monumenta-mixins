package com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.codegen.Linkable;
import com.playmonumenta.papermixins.mcfunction.execution.FunctionExecSource;
import com.playmonumenta.papermixins.mcfunction.execution.instr.ControlInstr;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.BlockAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.execution.UnboundEntryAction;

/**
 * <b>Pseudocode</b>
 * <pre>
 * {@code
 * var commandSources = runSelector();
 * for(var source : commandSources) {
 *     runBody();
 * }
 * }
 * </pre>
 * <b>Pseudocode (assembly)</b>
 * <pre>
 * {@code
 *   PUSH[Source](%source)
 *   PUSH[SourceList](runSelectors())
 * loop_begin:
 *   %0 = PEEK[Source](%source)
 *   BR_COND(%0.isEmpty(), &loop_exit)
 *   %source = %0.popFront()
 *
 *   // ... 'run' statement body
 *
 *   BR(&loop_begin)
 * loop_exit:
 *   POP[SourceList]()
 *   %source = POP[Source]()
 * }
 * </pre>
 */
public class RunAST extends ASTNode {
	private final BlockAST body;
	private final UnboundEntryAction<CommandSourceStack> statement;

	public RunAST(BlockAST body, UnboundEntryAction<CommandSourceStack> statement) {
		this.body = body;
		this.statement = statement;
	}

	@Override
	public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
		final var loopBegin = gen.defineLabel("cfv1$run$loop_begin");
		final var loopExit = gen.defineLabel("cfv1$run$loop_exit");

		// PUSH[Source](%source)
		// PUSH[SourceList](runSelectors())
		gen.emitControlNamed(
			"cfv1::loop::push_source_and_match",
			(state, context, frame) -> {
				state.stack.pushSource(state.source);
				state.stack.pushSourceList(new ArrayList<>());
				statement.execute(new FunctionExecSource(state.source, state), context, frame);
			}
		);

		// loop_begin:
		gen.emitLabel(loopBegin);

		// %0 = PEEK[Source](%source)
		// BR_COND(%0.isEmpty(), &loop_exit)
		// %source = %0.popFront()
		gen.emitControlLinkable(List.of(loopExit), () -> {
			final var loopExitTarget = loopExit.offset();
			return ControlInstr.named("cfv1::run::pop_source_or_branch", (state, context, frame) -> {
				if (state.stack.peekSourceList().isEmpty()) {
					state.instr = loopExitTarget;
					return;
				}

				final var list = state.stack.popSourceList();
				state.stack.pushSourceList(list.subList(1, list.size()));
				state.source = list.get(0);
			});
		});

		// body
		body.emit(diagnostics, cgCtx, gen);

		// BR(&loop_begin)
		gen.emitLinkable(Linkable.branch(loopBegin));

		// loop_exit:
		gen.emitLabel(loopExit);

		// POP[SourceList]()
		// %source = POP[Source]()
		gen.emitControlNamed("cfv1::run::cleanup", (state, context, frame) -> {
			state.stack.popSourceList();
			state.source = state.stack.popSource();
		});
	}

	@Override
	public void visit(Consumer<ASTNode> visitor) {
		visitor.accept(body);
	}

	@Override
	public String toString() {
		return "RunAST[" + statement + "]";
	}
}
