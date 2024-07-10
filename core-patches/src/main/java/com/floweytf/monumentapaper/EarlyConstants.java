package com.floweytf.monumentapaper;

import java.io.PrintStream;

// Classloaded during mixin configuration
public class EarlyConstants {
    private static final boolean ENABLE_DEBUG_PRINT = true;
    public static PrintStream out;

    public static void println(String str) {
        if (ENABLE_DEBUG_PRINT) {
            out.println("[CorePluginMixinService; debug]: " + str);
        }
    }
}
