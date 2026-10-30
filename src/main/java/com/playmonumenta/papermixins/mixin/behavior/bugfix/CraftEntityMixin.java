package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.papermc.paper.entity.TeleportFlag;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftEntity.class)
public class CraftEntityMixin {
	@Unique
	private List<Entity> oldPassengers;

	@WrapOperation(
		method = "teleport(Lorg/bukkit/Location;Lorg/bukkit/event/player/PlayerTeleportEvent$TeleportCause;[Lio/papermc/paper/entity/TeleportFlag;)Z",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
		)
	)
	public boolean retainPassengersAlways(Set instance, Object o, Operation<Boolean> original,
										  Location location, PlayerTeleportEvent.TeleportCause cause, TeleportFlag[] flags) {
		if (o == TeleportFlag.EntityState.RETAIN_PASSENGERS) {
			if (location.getWorld() != ((CraftEntity) (Object) this).getWorld() && !((CraftEntity) (Object) this).getPassengers().isEmpty()) {
				oldPassengers = ((CraftEntity) (Object) this).getPassengers();
				((CraftEntity) (Object) this).eject();
			}
			return true;
		}
		return original.call(instance, o);
	}

	@Inject(method = "teleport(Lorg/bukkit/Location;Lorg/bukkit/event/player/PlayerTeleportEvent$TeleportCause;" +
			"[Lio/papermc/paper/entity/TeleportFlag;)Z",
			at = @At("RETURN"))
	public void restorePassengers(Location location, PlayerTeleportEvent.TeleportCause cause, TeleportFlag[] flags, CallbackInfoReturnable<Boolean> cir) {
		if (oldPassengers != null) {
			for (Entity entity : oldPassengers) {
				entity.teleport(location);
				((CraftEntity) (Object) this).addPassenger(entity);
			}
			oldPassengers = null;
		}
	}
}
