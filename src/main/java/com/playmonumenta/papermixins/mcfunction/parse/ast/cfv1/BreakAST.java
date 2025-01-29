package com.playmonumenta.papermixins.mcfunction.parse.ast.cfv1;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.codegen.Linkable;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

public class BreakAST extends ASTNode {
    private final int lineNo;
    private final int levels;

    public BreakAST(int lineNo, int levels) {
        this.lineNo = lineNo;
        this.levels = levels;
    }

    @Override
    public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
        final var breakables = cgCtx.getBreakables();

        if (breakables.isEmpty()) {
            diagnostics.reportErr(lineNo, "'break' may only be used inside iterative control flow");
            return;
        }

        if (breakables.size() < levels) {
            diagnostics.reportErr(lineNo, "not enough levels of control flow!");
            return;
        }

        final var branchTarget = breakables.get(breakables.size() - levels);
        gen.emitLinkable(Linkable.branch(branchTarget));
    }

    @Override
    public void visit(Consumer<ASTNode> visitor) {
    }
}
