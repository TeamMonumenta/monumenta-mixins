package com.playmonumenta.papermixins.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.MonumentaMod;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.function.Consumer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScores;
import net.minecraft.world.scores.Score;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerScores.class)
public class PlayerScoresMixin {
    @Shadow @Final private Reference2ObjectOpenHashMap<Objective, Score> scores;

    @ModifyReturnValue(
        method = "get",
        at = @At("RETURN")
    )
    private Score hookReturnValue(Score original, @Local(argsOnly = true) Objective objective) {
        if (original != null && original.value() == 0 && objective.getName().startsWith("PP")) {
            MonumentaMod.LOGGER.info("score load returned 0: {}", objective.getName(), new Exception());
        }

        return original;
    }

    @Inject(
        method = "setScore",
        at = @At("HEAD")
    )
    private void hookSetScore(Objective objective, Score score, CallbackInfo ci) {
        if (score != null && score.value() == 0 && objective.getName().startsWith("PP")) {
            MonumentaMod.LOGGER.info("score store 0: {}", objective.getName(), new Exception());
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Score getOrCreate(Objective objective, Consumer<Score> scoreConsumer) {
        return scores.computeIfAbsent(objective, object -> {
            Score score = new Score();
            scoreConsumer.accept(score);
            if(objective.getName().startsWith("PP") && score.value() == 0) {
                MonumentaMod.LOGGER.info("score store 0: {}", objective.getName(), new Exception());
            }
            return score;
        });
    }
}