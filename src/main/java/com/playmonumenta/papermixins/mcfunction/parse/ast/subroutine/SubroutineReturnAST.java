package com.playmonumenta.papermixins.mcfunction.parse.ast.subroutine;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

/**
 * Return from the current **subroutine**. This is not related to the
 * <a href="https://minecraft.wiki/w/Commands/return">vanilla</a> command, which roughly corresponds to "exit"
 */
public class SubroutineReturnAST extends ASTNode {
	@Override
	public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
		//gen.emitControl(SubroutineRetInstr.get());
	}

	@Override
	public void visit(Consumer<ASTNode> visitor) {
	}

	@Override
	public String toString() {
		return "SubroutineReturnAST";
	}
}
