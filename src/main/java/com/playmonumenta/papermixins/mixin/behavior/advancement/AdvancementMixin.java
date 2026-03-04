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

    @Unique
    public int monumenta$getPriority() {
        return monumenta$priority;
    }

    @Unique
    public void monumenta$setPriority(int priority) {
        monumenta$priority = priority;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyCodec(CallbackInfo ci) {
        System.out.println("beginning modifyCodec");
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
                                    System.out.println("attempting apply");
                                    AdvancementRequirements advancementRequirements = requirements.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
                                    Advancement adv = new Advancement(parent, display, rewards, criteria, advancementRequirements, sendsTelemetryEvent);
                                    Util.<AdvancementAccess>c(adv).monumenta$setPriority(priority);
                                    System.out.println("attempting apply: set priority");
                                    return adv;
                                })
                ),
                Advancement::validate
        );
        System.out.println("ending modifyCodec");
    }

    // this probably isnt necessary as we're not reading registry info sent over the wire

//    @Inject(method = "read(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/advancements/Advancement;", at = @At("HEAD"))
//    private static void modifyRead(FriendlyByteBuf buf, CallbackInfoReturnable<Advancement> cir) {
//        Advancement a = new Advancement(
//                buf.readOptional(FriendlyByteBuf::readResourceLocation),
//                buf.readOptional(DisplayInfo::fromNetwork),
//                AdvancementRewards.EMPTY,
//                Map.of(),
//                new AdvancementRequirements(buf),
//                buf.readBoolean()
//        );
//        Util.<AdvancementAccess>c(a).monumenta$setPriority(buf.readInt());
//        cir.setReturnValue(a);
//    }
}
