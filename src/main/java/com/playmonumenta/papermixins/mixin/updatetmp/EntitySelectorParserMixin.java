package com.playmonumenta.papermixins.mixin.updatetmp;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySelectorParser.class)
public abstract class EntitySelectorParserMixin {
	@Shadow
	private int maxResults;

	@Shadow
	private boolean includesEntities;

	@Shadow
	private boolean currentEntity;

	@Shadow
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;

	@Shadow
	protected abstract CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

	@Inject(
		method = "parseSelector",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/brigadier/StringReader;setCursor(I)V"
		),
		cancellable = true
	)
	private void here(boolean overridePermissions, CallbackInfo ci, @Local char ch0) {
		if (Character.isUpperCase(ch0)) {
			this.maxResults = 1;
			this.includesEntities = true;
			this.currentEntity = true;
			this.suggestions = this::suggestOpenOptions;
			ci.cancel();
		}
	}
}
