package com.playmonumenta.papermixins.mcfunction.parse.ast;

import com.playmonumenta.papermixins.mcfunction.codegen.Label;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class CodegenContext {
    private final Map<String, Label> subroutines = new HashMap<>();
    private @Nullable Label breakExitLabel;

    public CodegenContext(@Nullable Label breakExitLabel) {
        this.breakExitLabel = breakExitLabel;
    }

    public CodegenContext() {
        this(null);
    }

    public @Nullable Label breakExitLabel() {
        return breakExitLabel;
    }

    public void breakExitLabel(@Nullable Label breakExitLabel) {
        this.breakExitLabel = breakExitLabel;
    }

    public Map<String, Label> subroutines() {
        return subroutines;
    }
}
