package com.playmonumenta.papermixins.registry.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.playmonumenta.papermixins.upgradetmp.CommandFunctionDumpUpgrader;
import com.playmonumenta.papermixins.upgradetmp.CommandJsonDumpUpgrader;
import com.playmonumenta.papermixins.upgradetmp.CommandSQJsonDumpUpgrader;
import com.playmonumenta.papermixins.upgradetmp.SimpleCommandUpgrader;
import static com.playmonumenta.papermixins.util.CommandUtil.arg;
import static com.playmonumenta.papermixins.util.CommandUtil.lit;
import net.minecraft.commands.CommandSourceStack;

public class DebugCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(lit("devtest", arg("cmd", StringArgumentType.greedyString(), c -> {
            System.out.println(SimpleCommandUpgrader.updateSingle(StringArgumentType.getString(c, "cmd")));
            return 0;
        })));
        dispatcher.register(lit("updatecommandjson", c -> {
            CommandJsonDumpUpgrader.doUpdate();
            return 0;
        }));
        dispatcher.register(lit("updatecommandmeow", c -> {
            CommandFunctionDumpUpgrader.doUpdate();
            return 0;
        }));
        dispatcher.register(lit("updatecommandsqjson", c -> {
            CommandSQJsonDumpUpgrader.doUpdate();
            return 0;
        }));
    }
}
