package com.playmonumenta.papermixins.impl.v1.item;

import com.google.common.base.Preconditions;
import com.playmonumenta.mixinapi.v1.item.CustomItem;
import com.playmonumenta.mixinapi.v1.item.CustomItemType;
import com.playmonumenta.mixinapi.v1.item.ItemVariantSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class ItemVariantSetImpl implements ItemVariantSet {
	private final Map<String, CustomItemType> variants = new HashMap<>(1);
	private final NamespacedKey key;
	@Nullable
	private CustomItemType defaultVariant;

	public ItemVariantSetImpl(NamespacedKey key) {
		this.key = key;
	}

	@Override
	public @NotNull CustomItemType defaultVariant() {
		if (defaultVariant == null) {
			throw new IllegalStateException("ItemVariantSet without default variant");
		}

		return defaultVariant;
	}

	@Override
	public @NotNull CustomItemType register(@NotNull String variantId, @NotNull Supplier<CustomItem> factory,
											@NotNull Material baseItem, boolean isStateless) {
		CustomItemRegistryImpl.onMutate();
		Preconditions.checkNotNull(variantId);
		Preconditions.checkNotNull(factory);
		Preconditions.checkNotNull(baseItem);

		if (variants.containsKey(variantId)) {
			throw new IllegalArgumentException("Duplicate key '" + variantId + "'");
		}

		final var type = new CustomItemTypeImpl(factory, key, variantId, baseItem, isStateless, this);
		variants.put(variantId, type);
		return type;
	}

	@Override
	public void setDefaultVariant(@NotNull CustomItemType type) {
		CustomItemRegistryImpl.onMutate();
		Preconditions.checkNotNull(type);
		Preconditions.checkArgument(type.variantSet() == this, "variant does not belong to this variant set");

		this.defaultVariant = type;
	}

	@Override
	public @NotNull NamespacedKey key() {
		return key;
	}

	@Override
	public @NotNull Map<String, CustomItemType> variants() {
		return Collections.unmodifiableMap(variants);
	}

	public boolean isInvalid() {
		return defaultVariant == null;
	}
}
