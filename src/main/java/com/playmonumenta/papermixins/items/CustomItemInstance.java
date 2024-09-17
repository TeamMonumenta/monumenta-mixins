package com.playmonumenta.papermixins.items;

import com.playmonumenta.mixinapi.v1.item.CustomItem;
import com.playmonumenta.papermixins.impl.v1.resource.ExtraItemDataImpl;
import com.playmonumenta.papermixins.util.nbt.CachedTagApplicator;
import com.playmonumenta.papermixins.util.nbt.DirectTagApplicator;
import com.playmonumenta.papermixins.util.nbt.ListTagBuilder;
import com.playmonumenta.papermixins.util.nbt.TagApplicator;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import javax.annotation.Nullable;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class CustomItemInstance {
    private final CustomItem item;
    private final NamespacedKey key;
    private final String variant;
    private final Material baseItem;
    private final boolean isStateless;

    @Nullable
    private CachedTagApplicator applicator;

    public CustomItemInstance(CustomItem item, NamespacedKey key, String variant, Material baseItem,
                              boolean isStateless) {
        this.item = item;
        this.key = key;
        this.variant = variant;
        this.baseItem = baseItem;
        this.isStateless = isStateless;
    }

    private void computeTags(TagApplicator applicator) {
        final var displayTag = new CompoundTag();

        item.getTitle()
            .map(x -> x.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
            .ifPresent(component -> displayTag.putString(
                "Name", GsonComponentSerializer.gson().serialize(component)
            ));

        item.getLore()
            .ifPresent(component -> displayTag.put("Lore",
                ListTagBuilder.of(component.stream().map(x -> StringTag.valueOf(GsonComponentSerializer.gson().serialize(x))))));

        if (!displayTag.isEmpty()) {
            applicator.put("display", displayTag);
        }

        final var state = new ExtraItemDataImpl();
        item.configureExtra(state);

        if (state.isUnbreakable()) {
            applicator.put("Unbreakable", ByteTag.ONE);
        }

        if (baseItem == Material.WRITTEN_BOOK) {
            if (state.author() != null) {
                applicator.put("author", StringTag.valueOf(state.author()));
            }

            if (state.generation() != null) {
                applicator.put("generation", IntTag.valueOf(state.generation().ordinal()));
            }

            if (state.bookPages() != null) {
                applicator.put("pages",
                    ListTagBuilder.of(state.bookPages().stream().map(u -> GsonComponentSerializer.gson().serialize(u)).map(StringTag::valueOf)));
            }

            if (state.title() != null) {
                applicator.put("title", StringTag.valueOf(GsonComponentSerializer.gson().serialize(state.title())));
            }
        }

        final var hideFlags = item.hideFlags().stream()
            .map(flag -> 1 << flag.ordinal())
            .reduce(0, (a, b) -> a | b);

        applicator.put("HideFlags", IntTag.valueOf(hideFlags));

        if (state.getNBTTag() != null) {
            final var compound = ((NBTContainer) state.getNBTTag()).getCompound();
            if (!(compound instanceof CompoundTag)) {
                throw new IllegalStateException("Not NMS Compound?");
            }

            // copy it into the applicator...
            ((CompoundTag) compound).tags.forEach(applicator::put);
        }
    }

    public void apply(ItemStack stack) {
        if (isStateless) {
            if (applicator == null) {
                applicator = new CachedTagApplicator();
                computeTags(applicator);
            }

            applicator.apply(new DirectTagApplicator(stack.getOrCreateTag()));
        } else {
            computeTags(new DirectTagApplicator(stack.getOrCreateTag()));
        }
    }

    public CustomItem item() {
        return item;
    }

    public NamespacedKey key() {
        return key;
    }

    public String variant() {
        return variant;
    }

    public Material baseItem() {
        return baseItem;
    }

    public boolean isStateless() {
        return isStateless;
    }
}
