package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.playmonumenta.papermixins.duck.AdvancementAccess;
import com.playmonumenta.papermixins.util.Util;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Advancement.class)
public class AdvancementMixin implements AdvancementAccess {
	@Unique
	private int monumenta$priority = 0;

	@Mutable
	@Shadow @Final public static Codec<Advancement> CODEC;

	@Shadow @Final private static Codec<Map<String, Criterion<?>>> CRITERIA_CODEC;

	@Shadow
	public static DataResult<Advancement> validate(Advancement advancement) { return null; }

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

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void modifyCodec(CallbackInfo ci) {
		CODEC = ExtraCodecs.validate(
				RecordCodecBuilder.create(
						instance -> instance.group(
										ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "parent").forGetter(Advancement::parent),
										ExtraCodecs.strictOptionalField(DisplayInfo.CODEC, "display").forGetter(Advancement::display),
										ExtraCodecs.strictOptionalField(AdvancementRewards.CODEC, "rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards),
										CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria),
										ExtraCodecs.strictOptionalField(AdvancementRequirements.CODEC, "requirements")
												.forGetter(a -> Optional.of(a.requirements())),
										ExtraCodecs.strictOptionalField(Codec.BOOL, "sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent),
										ExtraCodecs.strictOptionalField(Codec.INT, "priority", 0).forGetter(a -> Util.<AdvancementAccess>c(a).monumenta$getPriority())
								)
								.apply(instance, (parent, display, rewards, criteria, requirements, sendsTelemetryEvent, priority) -> { // priority
									AdvancementRequirements advancementRequirements = requirements.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
									Advancement adv = new Advancement(parent, display, rewards, criteria, advancementRequirements, sendsTelemetryEvent);
									Util.<AdvancementAccess>c(adv).monumenta$setPriority(priority);
									return adv;
								})
				),
				AdvancementMixin::validate
		);
	}

	// Advancement.read(FriendlyByteBuf) also creates advancements, but we don't need to modify it
	// because it's intended for clients parsing advancements from registry packets
}
