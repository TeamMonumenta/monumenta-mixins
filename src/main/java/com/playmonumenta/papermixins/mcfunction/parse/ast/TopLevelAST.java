package com.playmonumenta.papermixins.mcfunction.parse.ast;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.execution.BranchInstr;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.subroutine.SubroutineDefinitionAST;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

public class TopLevelAST extends ASTNode {
	private final BlockAST block;
	private final List<SubroutineDefinitionAST> subroutines;

	public TopLevelAST(BlockAST block, List<SubroutineDefinitionAST> subroutines) {
		this.block = block;
		this.subroutines = subroutines;
	}

	@Override
	public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
		final var map = new HashMap<String, SubroutineDefinitionAST>();

		for (final var subroutine : subroutines) {
			if (map.containsKey(subroutine.name())) {
				diagnostics.reportErr(
					subroutine.line(),
					"re-definition of subroutine (previously defined on line %d)",
					map.get(subroutine.name()).line()
				);
				continue;
			}

			map.put(subroutine.name(), subroutine);
		}

		map.forEach((name, ast) -> cgCtx.subroutines().put(name, gen.defineLabel("subroutine_" + name)));

		block.emit(diagnostics, cgCtx, gen);
		if (!subroutines.isEmpty()) {
			gen.emitControl(BranchInstr.exit());
		}

		for (final var subroutine : subroutines) {
			subroutine.emit(diagnostics, cgCtx, gen);
		}
	}

	@Override
	public void visit(Consumer<ASTNode> visitor) {
		subroutines.forEach(visitor);
		visitor.accept(block);
	}

	@Override
	public String toString() {
		return "TopLevelAST";
	}
}
