package com.playmonumenta.papermixins.mixin.behavior.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//RP side integration for CustomName based tile entities
@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin {

	@Inject(method = "getUpdateTag", at = @At("RETURN"), cancellable = true)
	private void monumenta$includeCustomName(CallbackInfoReturnable<CompoundTag> cir) {
		CompoundTag tag = cir.getReturnValue();

		RandomizableContainerBlockEntity self = (RandomizableContainerBlockEntity) (Object) this;

		if (self.hasCustomName()) {
			Component name = self.getCustomName();
			if (name != null) {
				tag.putString("CustomName", Component.Serializer.toJson(name));
			}
		}

		// Feed back to MC
		cir.setReturnValue(tag);
	}
}
