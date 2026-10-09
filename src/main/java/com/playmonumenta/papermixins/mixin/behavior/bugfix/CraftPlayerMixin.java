package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.papermc.paper.entity.TeleportFlag;
import java.util.Set;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftPlayer.class)
public class CraftPlayerMixin {
	@WrapOperation(
		method = "teleport(Lorg/bukkit/Location;Lorg/bukkit/event/player/PlayerTeleportEvent$TeleportCause;[Lio/papermc/paper/entity/TeleportFlag;)Z",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
		)
	)
	public boolean retainPassengersAlways(Set instance, Object o, Operation<Boolean> original) {
		if (o == TeleportFlag.EntityState.RETAIN_PASSENGERS) {
			return true;
		}
		return original.call(instance, o);
	}
}
