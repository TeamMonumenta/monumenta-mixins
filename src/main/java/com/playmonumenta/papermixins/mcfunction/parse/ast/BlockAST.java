package com.playmonumenta.papermixins.mcfunction.parse.ast;

import com.playmonumenta.papermixins.mcfunction.codegen.CodeGenerator;
import com.playmonumenta.papermixins.mcfunction.parse.Diagnostics;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;

public class BlockAST extends ASTNode {
    private final List<ASTNode> children;

    public BlockAST(List<ASTNode> children) {
        this.children = children;
    }

    @Override
    public void emit(Diagnostics diagnostics, CodegenContext cgCtx, CodeGenerator<CommandSourceStack> gen) {
        for (final var child : children) {
            child.emit(diagnostics, cgCtx, gen);
        }
    }

    @Override
    public void visit(Consumer<ASTNode> visitor) {
        children.forEach(visitor);
    }

    @Override
    public String toString() {
        return "BlockAST";
    }
}
