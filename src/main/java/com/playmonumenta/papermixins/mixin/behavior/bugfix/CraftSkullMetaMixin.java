package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.profile.PlayerTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Section of interest:
 * {@snippet :
 * // Fill in textures
 * PlayerProfile ownerProfile = new CraftPlayerProfile(this.profile); // getOwnerProfile may return null
 * if (ownerProfile.getTextures().isEmpty()) {
 *     ownerProfile.update().thenAccept((filledProfile) -> {
 *         this.setOwnerProfile(filledProfile);
 *         tag.put(CraftMetaSkull.SKULL_OWNER.NBT, this.serializedProfile);
 *     });
 * }
 * }
 * These constructs are all from Bukkit, not paper. This mixin aims to
 * replace everything here by its corresponding paper construct.
 */

@Mixin(targets = "org.bukkit.craftbukkit.v1_20_R3.inventory.CraftMetaSkull")
public abstract class CraftSkullMetaMixin {
	@Shadow
	private GameProfile profile;
	@Shadow
	private CompoundTag serializedProfile;
	@SuppressWarnings("deprecation")
	@Shadow
	public abstract void setOwnerProfile(org.bukkit.profile.PlayerProfile profile);

	@Redirect(
		method = "applyToItem",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/profile/PlayerTextures;isEmpty()Z"
		)
	)
	boolean isEmpty(PlayerTextures instance) {
		return false;
	}

	@Inject(
		method = "applyToItem",
		at = @At(
			value = "NEW",
			target = "org/bukkit/craftbukkit/v1_20_R3/profile/CraftPlayerProfile"
		)
	)
	void fix(CompoundTag tag, CallbackInfo ci) {
		// Fill in textures
		PlayerProfile ownerProfile = new CraftPlayerProfile(this.profile); // getOwnerProfile may return null
		if (ownerProfile.getTextures().isEmpty()) {
			ownerProfile.update().thenAccept((filledProfile) -> {
				this.setOwnerProfile(filledProfile);
				// SKULL_OWNER.NBT constant
				tag.put("SkullOwner", this.serializedProfile);
			});
		}
	}
}
