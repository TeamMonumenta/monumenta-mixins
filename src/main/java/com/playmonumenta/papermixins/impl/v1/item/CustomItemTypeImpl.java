package com.playmonumenta.papermixins.impl.v1.item;

import com.playmonumenta.mixinapi.v1.item.CustomItem;
import com.playmonumenta.mixinapi.v1.item.CustomItemType;
import com.playmonumenta.mixinapi.v1.item.ItemVariantSet;
import com.playmonumenta.papermixins.items.CustomItemInstance;
import com.playmonumenta.papermixins.util.Lazy;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class CustomItemTypeImpl implements CustomItemType {
	private final Supplier<CustomItemInstance> factory;
	private final NamespacedKey key;
	private final String variant;
	private final Material baseItem;
	private final boolean isStateless;
	private final ItemVariantSetImpl variantSet;

	public CustomItemTypeImpl(
		Supplier<CustomItem> factory,
		NamespacedKey key,
		String variant,
		Material baseItem,
		boolean isStateless,
		ItemVariantSetImpl variantSet
	) {
		this.key = key;
		this.variant = variant;
		this.variantSet = variantSet;
		this.baseItem = baseItem;
		this.isStateless = isStateless;
		this.factory = convertSupplier(factory);
	}

	private Supplier<CustomItemInstance> convertSupplier(Supplier<CustomItem> suppler) {
		final Supplier<CustomItemInstance> supplier = () ->
			new CustomItemInstance(suppler.get(), key, variant, baseItem, isStateless);

		if (isStateless) {
			return new Lazy<>(supplier);
		}
		return supplier;
	}

	// API implementation
	@Override
	public @NotNull String variantId() {
		return variant;
	}

	@Override
	public @NotNull Material baseItem() {
		return baseItem;
	}

	@Override
	public boolean isStateless() {
		return isStateless;
	}

	@Override
	public @NotNull ItemVariantSet variantSet() {
		return variantSet;
	}

	// Internal getters
	public Supplier<CustomItemInstance> factory() {
		return factory;
	}
}
