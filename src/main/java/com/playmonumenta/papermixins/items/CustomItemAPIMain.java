package com.playmonumenta.papermixins.items;

import com.playmonumenta.mixinapi.item.CustomItemType;
import com.playmonumenta.papermixins.MonumentaMod;
import com.playmonumenta.papermixins.impl.item.CustomItemRegistryImpl;
import com.playmonumenta.papermixins.util.nbt.CompoundTagBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;

public class CustomItemAPIMain {
    public static final Logger LOGGER = MonumentaMod.getLogger("CustomItemAPI");

    private static Map<Item, Material> ITEM_MATERIAL = null;

    // Stupid 1.19.4 hack
    private static void initMap() {
        if (ITEM_MATERIAL != null) {
            return;
        }

        ITEM_MATERIAL = new HashMap<>();
        for (final var e : BuiltInRegistries.ITEM.entrySet()) {
            final var resource = e.getKey().location();
            final var item = e.getValue();
            ITEM_MATERIAL.put(item, Material.getMaterial(resource.getPath().toUpperCase(Locale.ROOT)));
        }
    }

    public static Material materialFromItem(Item item) {
        initMap();
        return ITEM_MATERIAL.get(item);
    }
    // end hack

    public static ItemStack makeItem(CustomItemType type, int count, Optional<CompoundTag> dataTag) {
        CompoundTag itemTag = CompoundTagBuilder.of()
            .put("id", BuiltInRegistries.ITEM.getKey(CraftMagicNumbers.getItem(type.baseItem())).toString())
            .put("Count", (byte) count)
            .put("tag", CompoundTagBuilder.of(
                ItemStackStateManager.ROOT_TAG_KEY, CompoundTagBuilder.of()
                    .put(ItemStackStateManager.KEY_ID, type.variantSet().key().toString())
                    .put(ItemStackStateManager.KEY_VARIANT, type.variantId())
                    .put(ItemStackStateManager.KEY_SAVE_DATA, dataTag.orElseGet(CompoundTag::new))
                    .get()
            )).get();

        return ItemStack.of(itemTag);
    }

    public static ItemStack makeItem(ResourceLocation key, String variantId, int count, Optional<CompoundTag> dataTag) {
        final var variantSet = CustomItemRegistryImpl.getInstance().get(CraftNamespacedKey.fromMinecraft(key));
        if (variantSet == null) {
            throw new IllegalArgumentException("unknown item id");
        }

        final var variant = variantSet.variants().get(variantId);

        if (variant == null) {
            throw new IllegalArgumentException("unknown variant '" + variantId + "'");
        }

        return makeItem(variant, count, dataTag);
    }

    public static ItemStack makeItem(ResourceLocation key, int count, Optional<CompoundTag> dataTag) {
        final var variantSet = CustomItemRegistryImpl.getInstance().get(CraftNamespacedKey.fromMinecraft(key));
        if (variantSet == null) {
            throw new IllegalArgumentException("unknown item id");
        }

        return makeItem(variantSet.defaultVariant(), count, dataTag);
    }
}