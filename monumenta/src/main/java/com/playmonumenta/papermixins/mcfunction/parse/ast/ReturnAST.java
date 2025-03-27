package com.playmonumenta.papermixins.mcfunction.parse.ast;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.execution.instr.BranchInstr;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

/**
 * Exit from the currently executing MCFunction
 */
public class ReturnAST extends ASTNode {
	@Override
	public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
		gen.emitControl(BranchInstr.exit());
	}

	@Override
	public void visit(Consumer<ASTNode> visitor) {

	}

	@Override
	public String toString() {
		return "ReturnAST";
	}
}
