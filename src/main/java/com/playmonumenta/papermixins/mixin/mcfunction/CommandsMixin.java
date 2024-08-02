package com.playmonumenta.papermixins.mixin.mcfunction;

import com.playmonumenta.papermixins.mcfunction.parse.parser.Parser;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {
    @Inject(
        method = "<init>(Lnet/minecraft/commands/Commands$CommandSelection;" +
            "Lnet/minecraft/commands/CommandBuildContext;)V",
        at = @At("TAIL")
    )
    private void registerCommands(Commands.CommandSelection environment, CommandBuildContext access,
                                  CallbackInfo ci) {
        Parser.init(access);
    }
}
