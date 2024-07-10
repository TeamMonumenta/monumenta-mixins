package com.floweytf.customitemapi.impl;

import com.floweytf.customitemapi.CustomItemAPIMain;
import com.floweytf.customitemapi.access.ItemStackAccess;
import com.floweytf.customitemapi.api.CustomItems;
import com.floweytf.customitemapi.api.item.CustomItem;
import com.floweytf.customitemapi.api.item.CustomItemType;
import com.floweytf.customitemapi.helpers.CustomItemInstance;
import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

import static com.floweytf.customitemapi.Utils.mapNull;

public class CustomItemsImpl implements CustomItems {
    private static final CustomItemsImpl INSTANCE = new CustomItemsImpl();

    private CustomItemsImpl() {

    }

    public static CustomItemsImpl getInstance() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public ItemStack create(@NotNull CustomItemType type, int count) {
        Preconditions.checkNotNull(type);

        // TODO: need a way of ensuring metadata is never messed up
        // this is easier with 1.20.5+
        return CraftItemStack.asCraftMirror(
            CustomItemAPIMain.makeItem(CraftNamespacedKey.toMinecraft(type.variantSet().key()), type.variantId(), count,
                Optional.empty())
        );
    }

    @Nullable
    private <T> T getItemProperty(ItemStack stack, Function<CustomItemInstance, T> getter) {
        Preconditions.checkNotNull(stack);

        if (stack instanceof CraftItemStack craftStack) {
            final var mc = craftStack.handle;
            return mapNull(((ItemStackAccess) (Object) mc).custom_item_api$getStateManager().getCustomState(), getter);
        } else {
            throw new IllegalArgumentException("stack must be from paper");
        }
    }

    @Override
    @Nullable
    public CustomItem getCustomItem(@NotNull ItemStack stack) {
        return getItemProperty(stack, CustomItemInstance::item);
    }

    @Override
    @Nullable
    public NamespacedKey getKey(@NotNull ItemStack stack) {
        return getItemProperty(stack, CustomItemInstance::key);
    }

    @Override
    @Nullable
    public String getVariantId(@NotNull ItemStack stack) {
        return getItemProperty(stack, CustomItemInstance::variant);
    }

    @SuppressWarnings("UnreachableCode")
    @Override
    public void forceUpdate(@NotNull ItemStack stack) {
        Preconditions.checkNotNull(stack);

        if (stack instanceof CraftItemStack craftStack) {
            final var mc = craftStack.handle;
            final var state = ((ItemStackAccess) (Object) mc).custom_item_api$getStateManager();
            if (state.getCustomState() != null) {
                state.recomputeDisplay(craftStack.handle);
            }
        } else {
            throw new IllegalArgumentException("stack must be from paper");
        }
    }
}