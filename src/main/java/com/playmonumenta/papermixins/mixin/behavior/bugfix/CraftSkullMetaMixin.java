package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
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
 * Additionally, synchronizes all writes to profile and operations that perform multiple reads
 * from profile
 */

@Mixin(targets = "org.bukkit.craftbukkit.v1_20_R3.inventory.CraftMetaSkull")
public abstract class CraftSkullMetaMixin {
	@Shadow
	private GameProfile profile;
	@Shadow
	private CompoundTag serializedProfile;
	@Shadow
	private ResourceLocation noteBlockSound;

	@Unique
	private static final int GETFIELD = 180;

	@Inject(
		method = "applyToItem",
		at = @At(
			value = "FIELD",
			opcode = GETFIELD,
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/inventory/CraftMetaSkull;profile:Lcom/mojang/authlib/GameProfile;",
			ordinal = 0
		),
		cancellable = true
	)
	void fix(CompoundTag tag, CallbackInfo ci) {
		synchronized (this) {
			if (this.profile != null) {
				this.checkForInconsistency();

				// SPIGOT-6558: Set initial textures
				tag.put("SkullOwner", this.serializedProfile);
				// Fill in textures
				PlayerProfile ownerProfile = new CraftPlayerProfile(this.profile); // getOwnerProfile may return null
				if (ownerProfile.getTextures().isEmpty()) {
					// Prepare for future use; note that this makes a clone
					ownerProfile.update().thenAccept(this::setOwnerProfile);
					// While update() does its work, try to get textures from cache.
					// Although the interface says it "does not account for textures",
					// it does indeed copy the profile properties from cache.
					ownerProfile.completeFromCache();
					if (ownerProfile.hasTextures()) {
						this.setOwnerProfile(ownerProfile);
					}
					tag.put("SkullOwner", this.serializedProfile);
				}
			}
		}

		if (this.noteBlockSound != null) {
			CompoundTag nbtTagCompound = new CompoundTag();
			nbtTagCompound.putString("note_block_sound", this.noteBlockSound.toString());
			tag.put("BlockEntityTag", nbtTagCompound);
		}

		ci.cancel();
	}

	/**
	 * @author karpandsmeargle
	 * @reason make the simultaneous setting of profiles and serialized profile atomic
	 */
	@Overwrite
	private void setProfile(GameProfile profile) {
		synchronized (this) {
			this.profile = profile;
			this.serializedProfile = (profile == null) ? null : NbtUtils.writeGameProfile(new CompoundTag(), profile);
		}
	}

	/**
	 * @author karpandsmeargle
	 * @reason synchronize profile access
	 */
	@Overwrite
	public boolean hasOwner() {
		synchronized (this) {
			return this.profile != null && !this.profile.getName().isEmpty();
		}
	}

	/**
	 * @author karpandsmeargle
	 * @reason synchronize profile access
	 */
	@Overwrite
	public String getOwner() {
		synchronized (this) {
			return this.hasOwner() ? this.profile.getName() : null;
		}
	}

	/**
	 * @author karpandsmeargle
	 * @reason synchronize profile access
	 */
	@Overwrite
	public OfflinePlayer getOwningPlayer() {
		synchronized (this) {
			if (this.hasOwner()) {
				if (!this.profile.getId().equals(Util.NIL_UUID)) {
					return Bukkit.getOfflinePlayer(this.profile.getId());
				}

				if (!this.profile.getName().isEmpty()) {
					return Bukkit.getOfflinePlayer(this.profile.getName());
				}
			}

			return null;
		}
	}

	/**
	 * @author karpandsmeargle
	 * @reason synchronize profile access
	 */
	@Overwrite
	@Deprecated
	public org.bukkit.profile.PlayerProfile getOwnerProfile() {
		synchronized (this) {
			if (!this.hasOwner()) {
				return null;
			}

			return new org.bukkit.craftbukkit.v1_20_R3.profile.CraftPlayerProfile(this.profile);
		}
	}

	@SuppressWarnings("deprecation")
	@Shadow
	public abstract void setOwnerProfile(org.bukkit.profile.PlayerProfile profile);

	@Redirect(
		method = "applyHash",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/inventory/CraftMetaSkull;hasOwner()Z"
		)
	)
	boolean cancelHasOwner(@Coerce SkullMeta instance) {
		return false;
	}

	@ModifyVariable(
		method = "applyHash",
		at = @At(
			value = "INVOKE",
			target = "Lorg/bukkit/craftbukkit/v1_20_R3/inventory/CraftMetaSkull;hasOwner()Z"
		),
		name = "hash"
	)
	int fixApplyHash(int hash) {
		synchronized (this) {
			if (this.hasOwner()) {
				return 61 * hash + this.profile.hashCode();
			} else {
				return hash;
			}
		}
	}

	@WrapMethod(
		method = "equalsCommon"
	)
	boolean syncEqualsCommon(@Coerce ItemMeta meta, Operation<Boolean> original) {
		synchronized (this) {
			return original.call(meta);
		}
	}

	/**
	 * @author karpandsmeargle
	 * @reason synchronize profile access
	 */
	@SuppressWarnings("UnstableApiUsage")
	@Overwrite
	private void checkForInconsistency() {
		synchronized (this) {
			if (this.profile != null && this.serializedProfile == null) {
				// SPIGOT-7510: Fix broken reflection usage from plugins
				Bukkit.getLogger().warning("""
						Found inconsistent skull meta, this should normally not happen and is not a Bukkit / Spigot issue, but one from a plugin you are using.
						Bukkit will attempt to fix it this time for you, but may not be able to do this every time.
						If you see this message after typing a command from a plugin, please report this to the plugin developer, they should use the api instead of relying on reflection (and doing it the wrong way).""");
				this.serializedProfile = NbtUtils.writeGameProfile(new CompoundTag(), this.profile);
			}
		}
	}
}
