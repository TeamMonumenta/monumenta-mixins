package com.playmonumenta.papermixins.mixin.impl.itemapi;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftEventFactory.class)
public class CraftEventFactoryMixin {
	@Redirect(
		method = "handleEditBookEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;setItem(Lnet/minecraft/world/item/Item;)V"
		)
	)
	private static void setItem(ItemStack stack, Item item, @Local(argsOnly = true, ordinal = 0) LocalRef<ItemStack> ref) {
		ref.set(new ItemStack(
			BuiltInRegistries.ITEM.wrapAsHolder(Items.WRITTEN_BOOK),
			stack.getCount(),
			Optional.ofNullable(stack.getTag()))
		);
	}
}
