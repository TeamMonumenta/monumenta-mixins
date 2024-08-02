package com.playmonumenta.papermixins.mixin.commands;

import com.playmonumenta.papermixins.duck.EntitySelectorParserAccess;
import java.util.Arrays;
import java.util.function.Predicate;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0011-Monumenta-Selectors-require-targets-to-be-alive.patch
 * @mm-patch 0019-Monumenta-Added-all_worlds-selector-argument.patch
 * <p>
 * Ensure that entity selectors requires entity is alive by default
 * Also implement entity selector stuff
 */
@Mixin(EntitySelectorOptions.class)
public abstract class EntitySelectorOptionsMixin {
    @Shadow
    private static void register(String id, EntitySelectorOptions.Modifier handler,
                                 Predicate<EntitySelectorParser> condition, Component description) {
    }

    @Inject(
        method = "bootStrap",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions;register" +
                "(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options" +
                "/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;" +
                ")V",
            ordinal = 2
        )
    )
    private static void registerAllWorlds(CallbackInfo ci) {
        register("all_worlds",
            (reader) -> {
                int i = reader.getReader().getCursor();
                String string = reader.getReader().readUnquotedString();
                reader.setSuggestions((builder, consumer) -> SharedSuggestionProvider.suggest(Arrays.asList("true",
                    "false"), builder));
                switch (string) {
                case "true":
                    ((EntitySelectorParserAccess) reader).monumenta$setWorldLimited(false);
                    break;
                case "false":
                    break;
                default:
                    reader.getReader().setCursor(i);
                    throw EntitySelectorOptions.ERROR_UNKNOWN_OPTION.createWithContext(reader.getReader(), string);
                }
                ((EntitySelectorParserAccess) reader).monumenta$setWorldLimitedSet(true);
            },
            (reader) -> !((EntitySelectorParserAccess) reader).monumenta$getWorldLimitedSet(),
            Component.literal("Select entities in all worlds")
        );
    }
}