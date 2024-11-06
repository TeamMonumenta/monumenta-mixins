package com.playmonumenta.papermixins.mixin.misc;

import java.util.List;
import joptsimple.OptionParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: for some reason the IDE is stupid and bad, it's $1 instead of $0. Need to submit an issue to developers.
@Mixin(targets = "org.bukkit.craftbukkit.Main$1")
public class MainOptParserMixin extends OptionParser {
	@Inject(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/Main$1;acceptsAll(Ljava/util/List;Ljava/lang/String;)" +
				"Ljoptsimple/OptionSpecBuilder;",
			ordinal = 2
		)
	)
	private void addNoPlugins(CallbackInfo ci) {
		acceptsAll(List.of("no-plugins"), "runs paper with no plugins");
	}
}
