package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.ConfigManager;
import com.playmonumenta.papermixins.paperapi.v1.event.IronGolemHealEvent;
import com.playmonumenta.papermixins.util.Util;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0035-Monumenta-Remove-randomness-from-iron-golem-attacks.patch
 * <p>
 * Iron golem damage should be consistent.
 */
@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends LivingEntity {
	protected IronGolemMixin(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
	}

	@ModifyArg(
		method = "doHurtTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
		),
		index = 1
	)
	private float modifyAttackDamage(float amount, @Local(ordinal = 0) float f) {
		return ConfigManager.getConfig().behavior.disableGolemAttackRandomness ? f : amount;
	}

	@Inject(
			method = "mobInteract",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/IronGolem;heal(F)V"),
			cancellable = true
	)
	private void onHeal(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {

		CraftPlayer craftPlayer = (CraftPlayer) (player.getBukkitEntity());
		IronGolemHealEvent event = new IronGolemHealEvent(craftPlayer, Util.c(Util.<IronGolem>c(this).getBukkitEntity()));

		event.callEvent();

		if (event.isCancelled()) {
			cir.setReturnValue(InteractionResult.FAIL);
			ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(getId(), List.of(SynchedEntityData.DataValue.create(LivingEntity.DATA_HEALTH_ID, getHealth())));
			craftPlayer.getHandle().connection.send(packet);
			int itemSlot = craftPlayer.getInventory().getHeldItemSlot();
			ClientboundContainerSetSlotPacket packet1 = new ClientboundContainerSetSlotPacket(0, 0, itemSlot + 36, CraftItemStack.asNMSCopy(craftPlayer.getInventory().getItem(itemSlot)));
			craftPlayer.getHandle().connection.send(packet1);
		}
	}

}
