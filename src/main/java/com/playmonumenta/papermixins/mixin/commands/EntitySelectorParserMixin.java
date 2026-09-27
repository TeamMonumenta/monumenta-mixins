package com.playmonumenta.papermixins.mixin.commands;

import com.mojang.brigadier.StringReader;
import com.playmonumenta.papermixins.duck.EntitySelectorParserAccess;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Flowey
 * @mm-patch 0011-Monumenta-Selectors-require-targets-to-be-alive.patch
 * @mm-patch 0019-Monumenta-Added-all_worlds-selector-argument.patch
 * <p>
 * Ensure that entity selectors requires entity is alive by default
 * Also implement entity selector stuff
 */
@Mixin(EntitySelectorParser.class)
public class EntitySelectorParserMixin implements EntitySelectorParserAccess {
	@Shadow
	private boolean worldLimited;

	@Unique
	private boolean monumenta$worldLimitedSet = false;

	@Unique
	private boolean monumenta$dontAddOne = false;

	@Unique
	private boolean monumenta$dontAddOneSet = false;

	// TODO: validate this guy
	@ModifyConstant(
		method = "lambda$new$8",
		constant = @Constant(
			intValue = 1
		)
	)
	private static int modifyDefaultPredicate(int constant, Entity e) {
		return e.isAlive() ? 1 : 0;
	}

	@Inject(
		method = "<init>(Lcom/mojang/brigadier/StringReader;ZZ)V",
		at = @At("RETURN")
	)
	private void setWorldLimited(StringReader reader, boolean atAllowed,
								boolean parsingEntityArgumentSuggestions, CallbackInfo ci) {
		this.worldLimited = true;
	}

	@ModifyConstant(
		method = "createAabb(DDD)Lnet/minecraft/world/phys/AABB;",
		constant = @Constant(doubleValue = 1.0)
	)
    private double disableAddingOne(double constant) {
		return monumenta$dontAddOne ? 0.0 : 1.0;
	}

	// impl the I-face
	@Override
	public boolean monumenta$getWorldLimited() {
		return worldLimited;
	}

	@Override
	public void monumenta$setWorldLimited(boolean b) {
		worldLimited = b;
	}

	@Override
	public boolean monumenta$getWorldLimitedSet() {
		return monumenta$worldLimitedSet;
	}

	@Override
	public void monumenta$setWorldLimitedSet(boolean b) {
		monumenta$worldLimitedSet = b;
	}

	@Override
	public boolean monumenta$getDontAddOne() {
		return monumenta$dontAddOne;
	}

	@Override
	public void monumenta$setDontAddOne(boolean b) {
		monumenta$dontAddOne = b;
	}

	@Override
	public boolean monumenta$getDontAddOneSet() {
		return monumenta$dontAddOneSet;
	}

	@Override
	public void monumenta$setDontAddOneSet(boolean b) {
		monumenta$dontAddOneSet = b;
	}

	/**
	 * @author Flowey
	 * @reason Prevent the use of the world limited setter for default MC behaviour
	 */
	@Overwrite
	public void setWorldLimited() {
	}
}
