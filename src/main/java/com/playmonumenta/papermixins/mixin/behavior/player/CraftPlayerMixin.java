package com.playmonumenta.papermixins.mixin.behavior.player;

import com.playmonumenta.papermixins.duck.CraftPlayerAccess;
import de.tr7zw.nbtapi.NBT;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftPlayer.class)
public abstract class CraftPlayerMixin implements CraftPlayerAccess {
    @Shadow
    public abstract ServerPlayer getHandle();

    @Redirect(method = "setSpectatorTarget", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Preconditions;checkArgument(ZLjava/lang/Object;)V"))
    private static void removeSpectatorCheck(boolean expression, Object errorMessage) {

    }

    @Override
    public void monumenta_mixins$hideInventory() {
        NonNullList<ItemStack> list = NonNullList.createWithCapacity(46);
        for (int i=0; i < 46; i++) {
            if (i == 22) {
                org.bukkit.inventory.ItemStack note = new org.bukkit.inventory.ItemStack(Material.PAPER);
                ItemMeta meta = note.getItemMeta();
                meta.displayName(Component.text("Note", NamedTextColor.WHITE, TextDecoration.BOLD));
                meta.lore(List.of(Component.text("You are in Cutscene Mode, your inventory"),
                        Component.text("is hidden, not deleted")));
                NBT.modify(note, nbt -> {
                    nbt.getOrCreateCompound("plain").getOrCreateCompound("display").setString("Name", "Note");
                });
                 list.add(ItemStack.fromBukkitCopy(note));
            } else {
                list.add(ItemStack.EMPTY);
            }
        }
        var packet = new ClientboundContainerSetContentPacket(0, this.getHandle().inventoryMenu.incrementStateId(), list, ItemStack.EMPTY);
        this.getHandle().connection.send(packet);
    }

    @Override
    public void monumenta_mixins$resyncInventory() {
        this.getHandle().inventoryMenu.sendAllDataToRemote();
    }
}
