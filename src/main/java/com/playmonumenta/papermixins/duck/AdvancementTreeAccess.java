package com.playmonumenta.papermixins.duck;

import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;

public interface AdvancementTreeAccess {
	void monumenta$addAllFast(Map<ResourceLocation, AdvancementHolder> advancements);
}
