package com.playmonumenta.papermixins.mixin.accessor;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
	@Accessor("piercingIgnoreEntityIds")
	@Nullable IntOpenHashSet monumenta$getPiercingIgnoreEntityIds();

	@Accessor("piercingIgnoreEntityIds")
	void monumenta$setPiercingIgnoreEntityIds(@Nullable IntOpenHashSet piercingIgnoreEntityIds);
}
