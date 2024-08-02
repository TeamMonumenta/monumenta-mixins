package com.playmonumenta.papermixins.mcfunction.parse.ast.subroutine;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.execution.instr.SubroutineRetInstr;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import com.playmonumenta.papermixins.mcfunction.parse.ast.ASTNode;
import com.playmonumenta.papermixins.mcfunction.parse.ast.BlockAST;
import com.playmonumenta.papermixins.mcfunction.parse.ast.CodegenContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Consumer;

public class SubroutineDefinitionAST extends ASTNode {
    private final BlockAST body;
    private final String name;
    private final int line;

    public SubroutineDefinitionAST(BlockAST body, String name, int line) {
        this.body = body;
        this.name = name;
        this.line = line;
    }

    public String name() {
        return name;
    }

    public int line() {
        return line;
    }

    @Override
    public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
        gen.emitLabel(cgCtx.subroutines().get(name));
        body.emit(diagnostics, cgCtx, gen);
        gen.emitControl(SubroutineRetInstr.get());
    }

    @Override
    public void visit(Consumer<ASTNode> visitor) {
        visitor.accept(body);
    }

    @Override
    public String toString() {
        return "SubroutineDefinitionAST[" + name + "]";
    }
}
