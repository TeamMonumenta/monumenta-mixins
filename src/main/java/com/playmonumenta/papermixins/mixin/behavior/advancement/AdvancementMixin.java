package com.playmonumenta.papermixins.mixin.behavior.advancement;

import com.mojang.serialization.Codec;
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
import net.minecraft.network.chat.Component;
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
	@Unique private int monumenta$priority = 0;
	@Unique private String monumenta$treePositionType = "relative";
	@Unique private float monumenta$treePositionX = 0;
	@Unique private float monumenta$treePositionY = 0;

	@Mutable
	@Shadow @Final public static Codec<Advancement> CODEC;

	@Shadow @Final private static Codec<Map<String, Criterion<?>>> CRITERIA_CODEC;

	@Shadow
	@Final
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType") // it's shadowed
	private Optional<DisplayInfo> display;

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
										ExtraCodecs.strictOptionalField(Codec.INT, "priority", 0).forGetter(a -> Util.<AdvancementAccess>c(a).monumenta$getPriority()),
										ExtraCodecs.strictOptionalField(Codec.STRING, "positionType", "relative").forGetter(a -> Util.<AdvancementAccess>c(a).monumenta$getTreePositionType()),
										ExtraCodecs.strictOptionalField(Codec.FLOAT, "positionX", 0f).forGetter(a -> Util.<AdvancementAccess>c(a).monumenta$getTreePositionX()),
										ExtraCodecs.strictOptionalField(Codec.FLOAT, "positionY", 0f).forGetter(a -> Util.<AdvancementAccess>c(a).monumenta$getTreePositionY())
								)
								.apply(instance, (parent, display, rewards, criteria, requirements, sendsTelemetryEvent, priority, positionType, positionX, positionY) -> {
									AdvancementRequirements advancementRequirements = requirements.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
									Advancement adv = new Advancement(parent, display, rewards, criteria, advancementRequirements, sendsTelemetryEvent);
									Util.<AdvancementAccess>c(adv).monumenta$setPriority(priority);
									Util.<AdvancementAccess>c(adv).monumenta$setTreePositionType(positionType);
									Util.<AdvancementAccess>c(adv).monumenta$setTreePositionX(positionX);
									Util.<AdvancementAccess>c(adv).monumenta$setTreePositionY(positionY);
									return adv;
								})
				),
				Advancement::validate
		);
	}

	// Advancement.read(FriendlyByteBuf) also creates advancements, but we don't need to modify it
	// because it's intended for clients parsing advancements from registry packets

	@Unique
	public int monumenta$getPriority() {
		return monumenta$priority;
	}

	@Unique
	public void monumenta$setPriority(int priority) {
		monumenta$priority = priority;
	}

	@Unique
	public String monumenta$getTreePositionType() {
		return monumenta$treePositionType;
	}

	@Unique
	public void monumenta$setTreePositionType(String positionType) {
		if (!positionType.equals("absolute") && !positionType.equals("relative")) {
			Component title = this.display.orElseThrow(() -> new IllegalArgumentException("advancement missing name!")).getTitle();
			String title1 = String.join("", title.toFlatList().stream().map(Component::getString).toList());
			throw new IllegalArgumentException("positionType must be either absolute or relative, got \"%s\" for advancement \"%s\""
					.formatted(positionType, title1));
		}
		this.monumenta$treePositionType = positionType;
	}

	@Unique
	public float monumenta$getTreePositionX() {
		return monumenta$treePositionX;
	}

	@Unique
	public void monumenta$setTreePositionX(float monumenta$treePositionX) {
		this.monumenta$treePositionX = monumenta$treePositionX;
	}

	@Unique
	public float monumenta$getTreePositionY() {
		return monumenta$treePositionY;
	}

	@Unique
	public void monumenta$setTreePositionY(float monumenta$treePositionY) {
		this.monumenta$treePositionY = monumenta$treePositionY;
	}
}
