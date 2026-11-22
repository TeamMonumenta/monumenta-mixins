package com.playmonumenta.papermixins.mixin.commands;

import com.google.common.base.Strings;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.commands.SharedSuggestionProvider.matchesSubStr;

@Mixin(SharedSuggestionProvider.class)
public interface SharedSuggestionProviderMixin {

    // to replace instances of filterResources where T instanceof ResourceLocation
    // and action is (ResourceLocation) -> builder.suggest(something)
    @Unique
    private static void filterResourcesOneReplacement(Iterable<ResourceLocation> candidates, String remaining,
                                                      SuggestionsBuilder builder, Function<ResourceLocation, String> stringify) {
        boolean bl = remaining.indexOf(58) > -1;
        Set<String> suggestions = new HashSet<>();
        for(ResourceLocation resourceLocation : candidates) {
            if (bl) {
                String string = resourceLocation.toString();
                if (matchesSubStr(remaining, string)) {
                    suggestions.add(stringify.apply(resourceLocation));
                }
            } else if (matchesSubStr(remaining, resourceLocation.getNamespace()) || resourceLocation.getNamespace().equals("minecraft") && matchesSubStr(remaining, resourceLocation.getPath())) {
                suggestions.add(stringify.apply(resourceLocation));
            }
        }
        Set<String> shortenedSuggestions = shortenSuggestions(suggestions, remaining);
        shortenedSuggestions.forEach(builder::suggest);
    }

    // to replace instances of filterResources second overload where T instanceof ResourceLocation
    // and action is (ResourceLocation) -> builder.suggest(something)
    @Unique
    private static void filterResourcesTwoReplacement(Iterable<ResourceLocation> candidates, String remaining,
                                                      String prefix, SuggestionsBuilder builder,
                                                      Function<ResourceLocation, String> stringify) {
        if (remaining.isEmpty()) {
            Set<String> suggestions = new HashSet<>();
            candidates.forEach(rl -> suggestions.add(stringify.apply(rl)));
            Set<String> shortenedSuggestions = shortenSuggestions(suggestions, remaining);
            shortenedSuggestions.forEach(builder::suggest);
        } else {
            String string = Strings.commonPrefix(remaining, prefix);
            if (!string.isEmpty()) {
                String string2 = remaining.substring(string.length());
                filterResourcesOneReplacement(candidates, string2, builder, stringify);
            }
        }
    }

    @Unique
    private static Set<String> shortenSuggestions(Set<String> suggestions, String remaining) {
        return suggestions.stream()
                .map(s -> {
                    String substr = s.substring(remaining.length());
                    int i = substr.indexOf(":");
                    int j = substr.indexOf("/");
                    // if at least one doesn't appear, return the other one, else return whichever comes first
                    int index = Math.min(i, j) < 0 ? Math.max(i, j) : Math.min(i, j);
                    return index < 0 ? s : s.substring(0, remaining.length() + index + 1);
                })
                .collect(Collectors.toSet());
    }

    // redirect filterResources call on first overload
    @Redirect(method = "suggestResource(Ljava/lang/Iterable;Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;Ljava/lang/String;)Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/SharedSuggestionProvider;filterResources(Ljava/lang/Iterable;Ljava/lang/String;Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Consumer;)V"))
    private static void redirectFilterResources(Iterable<ResourceLocation> candidates, String remaining,
                                                    String prefix, Function<ResourceLocation, ResourceLocation> identifier,
                                                    Consumer<ResourceLocation> action, Iterable<ResourceLocation> candidates2,
                                                    SuggestionsBuilder builder, String prefix2) {
        // replaces: filterResources(candidates, string, prefix, (id) -> id, (id) -> builder.suggest(prefix + id));
        filterResourcesTwoReplacement(candidates, remaining, prefix, builder, (id) -> prefix + id);
    }

    // second overload of suggestResource does not need mixins, just turns Stream into Iterable and calls first overload

    // redirect filterResources call on third overload
    @Redirect(method = "suggestResource(Ljava/lang/Iterable;Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;)Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/SharedSuggestionProvider;filterResources(Ljava/lang/Iterable;Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Consumer;)V"))
    private static void redirectFilterResources2(Iterable<ResourceLocation> candidates, String remaining,
                                                 Function<ResourceLocation, ResourceLocation> identifier,
                                                 Consumer<ResourceLocation> action, Iterable<ResourceLocation> candidates2,
                                                 SuggestionsBuilder builder) {
        // replaces: filterResources(candidates, string, (id) -> id, (id) -> builder.suggest(id.toString()));
        filterResourcesOneReplacement(candidates, remaining, builder, ResourceLocation::toString);
    }

    // fourth overload of suggestResource we don't need to mess with, only used for entity types which don't need to be compacted
    // fifth overload just turns Stream into Iterable and calls second overload
    // sixth overload just calls fourth overload, similar to above
}
