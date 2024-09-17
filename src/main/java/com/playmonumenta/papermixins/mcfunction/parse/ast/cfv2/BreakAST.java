package com.playmonumenta.papermixins.mcfunction.parse.ast.cfv2;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

public class BreakAST extends ASTNode {
    private final int lineNo;

    public BreakAST(int lineNo) {
        this.lineNo = lineNo;
    }

    @Override
    public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
        if (cgCtx.breakExitLabel() == null) {
            diagnostics.reportErr(lineNo, "'break' may only be used inside iterative control flow");
        }
    }

    @Override
    public void visit(Consumer<ASTNode> visitor) {

    }
}
