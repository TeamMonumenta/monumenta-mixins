package com.playmonumenta.papermixins.mcfunction.parse.ast;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

public class MacroAST extends ASTNode {
	private final int lineNo;
	private final String text;

	public MacroAST(int lineNo, String text) {
		this.lineNo = lineNo;
		this.text = text;
	}

	@Override
	public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
		gen.emitMacro(text, lineNo);
	}

	@Override
	public void visit(Consumer<ASTNode> visitor) {

	}

	@Override
	public String toString() {
		return "MacroAST[" + lineNo + ", '" + text + "']";
	}
}
