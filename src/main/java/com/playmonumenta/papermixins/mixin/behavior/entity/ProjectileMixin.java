package com.playmonumenta.papermixins.mixin.behavior.entity;

import com.playmonumenta.papermixins.duck.SnowballAccess;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {
	@Inject(
        method = "canHitEntity",
        at = @At("RETURN"),
        cancellable = true
    )
	private void skipPiercedSnowballEntities(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			return;
		}

		if (!((Object) this instanceof Snowball snowball)) {
			return;
		}

		IntOpenHashSet piercedEntityIds = ((SnowballAccess) snowball).monumenta$getPiercedEntityIds();
		if (piercedEntityIds != null && piercedEntityIds.contains(entity.getId())) {
			cir.setReturnValue(false);
		}
	}
}
