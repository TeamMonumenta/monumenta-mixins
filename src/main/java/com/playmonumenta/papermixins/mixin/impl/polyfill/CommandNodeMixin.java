package com.playmonumenta.papermixins.mixin.impl.polyfill;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.playmonumenta.papermixins.duck.polyfill.CommandNodeAccess;
import io.papermc.paper.command.brigadier.APICommandMeta;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandNode.class)
public class CommandNodeMixin<S> implements CommandNodeAccess {
    @Shadow
    @Final
    private Map<String, CommandNode<S>> children;
    @Shadow
    @Final
    private Map<String, LiteralCommandNode<S>> literals;
    @Shadow
    @Final
    private Map<String, ArgumentCommandNode<S, ?>> arguments;

    @Unique
    public CommandNode<S> clientNode;

    @Unique
    private CommandNode<CommandSourceStack> monumenta$unwrappedCached = null;

    @Unique
    private CommandNode<CommandSourceStack> monumenta$wrappedCached = null;

    @Unique
    private APICommandMeta monumenta$apiCommandMeta;

    @Override
    public CommandNode<CommandSourceStack> monumenta$getUnwrappedCached() {
        return monumenta$unwrappedCached;
    }

    @Override
    public void monumenta$setUnwrappedCached(CommandNode<CommandSourceStack> converted) {
        monumenta$unwrappedCached = converted;
    }

    @Override
    public @Nullable CommandNode<CommandSourceStack> monumenta$getWrappedCached() {
        return monumenta$wrappedCached;
    }

    @Override
    public void monumenta$setWrappedCached(CommandNode<CommandSourceStack> pureNode) {
        monumenta$wrappedCached = pureNode;
    }

    @Override
    public APICommandMeta monumenta$getApiCommandMeta() {
        return monumenta$apiCommandMeta;
    }

    @Override
    public void monumenta$setApiCommandMeta(APICommandMeta apiCommandMeta) {
        monumenta$apiCommandMeta = apiCommandMeta;
    }

    @Override
    public void monumenta$clearAll() {
        this.children.clear();
        this.literals.clear();
        this.arguments.clear();
    }

    @Override
    public void monumenta$removeCommand(String name) {
        this.children.remove(name);
        this.literals.remove(name);
        this.arguments.remove(name);
    }
}
