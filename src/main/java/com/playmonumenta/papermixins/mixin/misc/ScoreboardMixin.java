package com.playmonumenta.papermixins.mixin.misc;

import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.scores.Scoreboard$1")
public class ScoreboardMixin {
    @Final
    @Shadow
    Objective val$objective;

    @Inject(method = "set", at = @At("HEAD"))
    public void hookSet(int score, CallbackInfo ci) {
        if (val$objective.getName().startsWith("PP") && score == 0) {
            MonumentaMod.LOGGER.info("score store (objective={}) 0: ", val$objective.getName(), new Exception());
        }
    }
}
