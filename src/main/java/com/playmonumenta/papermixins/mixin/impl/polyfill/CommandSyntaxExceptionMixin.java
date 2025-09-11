package com.playmonumenta.papermixins.mixin.impl.polyfill;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.brigadier.PaperBrigadier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.ComponentMessageThrowable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// from https://github.com/PaperMC/Paper/blob/51706e5ac1305c8490538a44c9b7019df0ac2bfc/paper-server/patches/sources/com/mojang/brigadier/exceptions/CommandSyntaxException.java.patch
@Mixin(CommandSyntaxException.class)
public class CommandSyntaxExceptionMixin implements ComponentMessageThrowable {
    @Shadow
    @Final
    private Message message;

    @Override
    public @Nullable Component componentMessage() {
        return PaperBrigadier.componentFromMessage(this.message);
    }
}
