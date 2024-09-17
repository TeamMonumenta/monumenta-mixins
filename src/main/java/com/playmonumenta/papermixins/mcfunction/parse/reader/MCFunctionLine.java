package com.playmonumenta.papermixins.mcfunction.parse.reader;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.functions.StringTemplate;

public class MCFunctionLine {
    // TODO: optimize CONTROL_FLOW pre-parsing; it should generate a parse result rather than storing the raw data.
    //  this doesn't matter for regular MCFunctions, but may degrade performance on macros!
    public enum Type {
        PRE_PARSED,
        CONTROL_FLOW,
        MACRO
    }

    private final int lineNumber;
    private final Type type;
    private final Object data;

    private MCFunctionLine(int lineNumber, Type type, Object data) {
        this.lineNumber = lineNumber;
        this.type = type;
        this.data = data;
    }

    public static MCFunctionLine preParsed(int lineNumber, UnboundEntryAction<CommandSourceStack> info) {
        return new MCFunctionLine(lineNumber, Type.PRE_PARSED, info);
    }

    public static MCFunctionLine controlFlow(int lineNumber, String type, String data) {
        return new MCFunctionLine(lineNumber, Type.CONTROL_FLOW, Pair.of(type, data));
    }

    public static MCFunctionLine macro(int lineNumber, StringTemplate invocation, IntList variableIndices) {
        return new MCFunctionLine(lineNumber, Type.MACRO, new MacroLine(invocation, variableIndices));
    }

    public Type type() {
        return type;
    }

    public int lineNumber() {
        return lineNumber;
    }

    @SuppressWarnings("unchecked")
    public UnboundEntryAction<CommandSourceStack> preParsed() {
        if (type != Type.PRE_PARSED) {
            throw new IllegalStateException("not a macro");
        }

        return (UnboundEntryAction<CommandSourceStack>) data;
    }

    public MacroLine macro() {
        if (type != Type.MACRO) {
            throw new IllegalStateException("not a macro");
        }

        return (MacroLine) data;
    }

    @SuppressWarnings("unchecked")
    public Pair<String, String> controlFlow() {
        if (type != Type.CONTROL_FLOW) {
            throw new IllegalStateException("not a macro");
        }

        return (Pair<String, String>) data;
    }
}
