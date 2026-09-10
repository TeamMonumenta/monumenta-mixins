package com.playmonumenta.papermixins.mixin.behavior.bugfix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
	@Shadow
	public abstract boolean isSaddled();

	@Shadow
	public abstract boolean isEating();

	@Shadow
	public abstract boolean isStanding();

	protected AbstractHorseMixin(EntityType<? extends Animal> type, Level world) {
		super(type, world);
	}

	@WrapMethod(method = "isImmobile")
	public boolean meow(Operation<Boolean> original) {
		// If dead, it's always immobile!
		if (super.isImmobile()) {
			return true;
		}
		return this.isVehicle() && (this.isSaddled() || this.isEating() || this.isStanding());
	}
}
