package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.playmonumenta.papermixins.duck.AdvancementAccess;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Advancement.class)
public class AdvancementMixin implements AdvancementAccess {
	@Unique
	private int monumenta$priority = 0;

	@Mutable
	@Shadow
	@Final
	public static Codec<Advancement> CODEC;

	@Shadow
	@Final
	private static Codec<Map<String, Criterion<?>>> CRITERIA_CODEC;

	@Shadow
	public static DataResult<Advancement> validate(Advancement advancement) {
		return null;
	}

	@Unique
	@Override
	public int monumenta$getPriority() {
		return monumenta$priority;
	}

	@Unique
	@Override
	public void monumenta$setPriority(int priority) {
		monumenta$priority = priority;
	}

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create" +
			"(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;")
	)
	private static Codec<Advancement> modifyCodec(Codec<Advancement> original) {
		return RecordCodecBuilder.<Advancement>create(i -> i.group(
					Identifier.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent),
					DisplayInfo.CODEC.optionalFieldOf("display").forGetter(Advancement::display),
					AdvancementRewards.CODEC.optionalFieldOf("rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards),
					CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria),
					AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter(a -> Optional.of(a.requirements())),
					Codec.BOOL.optionalFieldOf("sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent)
				)
				.apply(i, (parent, display, rewards, criteria, requirementsOpt, sendsTelemetryEvent) -> {
					AdvancementRequirements requirements =
						requirementsOpt.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
					return new Advancement(parent, display, rewards, criteria, requirements, sendsTelemetryEvent);
				})
		).validate(Advancement::validate);
	}
}
