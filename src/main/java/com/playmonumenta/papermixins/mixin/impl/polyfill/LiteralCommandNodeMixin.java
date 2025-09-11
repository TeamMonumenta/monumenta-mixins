package com.playmonumenta.papermixins.mixin.impl.polyfill;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// https://github.com/PaperMC/Paper/blob/51706e5ac1305c8490538a44c9b7019df0ac2bfc/paper-server/patches/sources/com/mojang/brigadier/tree/LiteralCommandNode.java.patch
@Mixin(LiteralCommandNode.class)
public abstract class LiteralCommandNodeMixin<S> {
    @Shadow
    protected abstract int parse(StringReader reader);

    @Mutable
    @Unique
    @Final
    private String nonPrefixed;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void setNonPrefixed(
        String literal, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect,
        RedirectModifier<S> modifier, boolean forks, CallbackInfo ci
    ) {
        if (literal.startsWith("minecraft:")) {
            this.nonPrefixed = literal.substring("minecraft:".length());
        } else {
            this.nonPrefixed = null;
        }
    }

    // duped code but i don't give a shit :P
    @Unique
    private int monumenta$parseSecondPass(final StringReader reader) {
        final int start = reader.getCursor();
        if (reader.canRead(nonPrefixed.length())) {
            final int end = start + nonPrefixed.length();
            if (reader.getString().substring(start, end).equals(nonPrefixed)) {
                reader.setCursor(end);
                if (!reader.canRead() || reader.peek() == ' ') {
                    return end;
                } else {
                    reader.setCursor(start);
                }
            }
        }
        return -1;
    }

    // slop
    @WrapOperation(
        method = "parse(Lcom/mojang/brigadier/StringReader;Lcom/mojang/brigadier/context/CommandContextBuilder;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/tree/LiteralCommandNode;parse(Lcom/mojang/brigadier/StringReader;)I"
        )
    )
    private int prioritizeMc(LiteralCommandNode<S> instance, StringReader stringReader, Operation<Integer> original) {
        int end = original.call(instance, stringReader);
        if (end == -1 && this.nonPrefixed != null) {
            end = monumenta$parseSecondPass(stringReader);
        }
        return end;
    }
}
