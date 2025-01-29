package com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.codegen.Linkable;
import com.playmonumenta.papermixins.mcfunction.execution.ControlInstr;
import com.playmonumenta.papermixins.mcfunction.execution.CustomExecSource;
import com.playmonumenta.papermixins.mcfunction.execution.FuncExecState;
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

		gen.emitControlNamed(
			"cfv1::run::push_source_and_match",
			(state, context, frame) -> {
				final var sources = new ArrayList<CommandSourceStack>();
				final var entry = new IterateFrame(state.source, sources);
				state.push(entry);
				statement.execute(
					new CustomExecSource<Consumer<CommandSourceStack>>(state.source, sources::add),
					context,
					frame
				);
			}
		);

		gen.emitLabel(loopBegin);

		gen.emitControlLinkable(List.of(loopExit), () -> {
			final var loopExitTarget = loopExit.offset();
			return ControlInstr.named("cfv1::run::pop_source_or_branch", (state, context, frame) -> {
				final IterateFrame runFrame = state.peek();

				if (!runFrame.has()) {
					state.instr = loopExitTarget;
					return;
				}

				state.source = runFrame.take();
			});
		});

		try (var ignored = cgCtx.visitBreakable(loopExit)) {
			body.emit(diagnostics, cgCtx, gen);
		}

		gen.emitLinkable(Linkable.branch(loopBegin));

		gen.emitLabel(loopExit);

		gen.emitControlNamed("cfv1::run::cleanup", (state, context, frame) -> {
			state.pop();
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
