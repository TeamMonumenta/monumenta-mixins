package com.playmonumenta.papermixins.mcfunction.parse.ast.subroutine;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.execution.instr.SubroutineCallInstr;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

public class SubroutineCallAST extends ASTNode {
    private static final String ERR_SUBROUTINE_NOT_DEFINED = "subroutine '%s' not defined";

    private final String name;
    private final int lineNo;

    public SubroutineCallAST(String name, int lineNo) {
        this.name = name;
        this.lineNo = lineNo;
    }

    @Override
    public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
        if (!cgCtx.subroutines().containsKey(name)) {
            diagnostics.reportErr(lineNo, ERR_SUBROUTINE_NOT_DEFINED, name);
            return;
        }

        final var targetLabel = cgCtx.subroutines().get(name);
        gen.emitControlLinkable(List.of(targetLabel), () -> new SubroutineCallInstr<>(targetLabel.offset()));
    }

    @Override
    public void visit(Consumer<ASTNode> visitor) {

    }

    @Override
    public String toString() {
        return "SubroutineCallAST[" + name + "]";
    }
}
