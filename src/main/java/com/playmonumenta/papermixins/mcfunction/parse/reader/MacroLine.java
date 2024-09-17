package com.playmonumenta.papermixins.mcfunction.parse.reader;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.commands.functions.StringTemplate;
import org.jetbrains.annotations.NotNull;

public class MacroLine {
    private final StringTemplate template;
    private final IntList parameters;

    public MacroLine(StringTemplate invocation, IntList variableIndices) {
        this.template = invocation;
        this.parameters = variableIndices;
    }

    public @NotNull IntList parameters() {
        return this.parameters;
    }

    public @NotNull String instantiate(@NotNull List<String> args) {
        return this.template.substitute(args);
    }
}