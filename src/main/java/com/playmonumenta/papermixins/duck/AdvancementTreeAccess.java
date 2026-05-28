package com.playmonumenta.papermixins.duck;

import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;

public interface AdvancementTreeAccess {
	void monumenta$addAllFast(Map<Identifier, AdvancementHolder> advancements);
}
