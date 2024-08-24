package com.playmonumenta.papermixins.mixin.api;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @mm-patch 0027-Monumenta-Handle-iframes-after-damage-event.patch
 * <p>
 * Wanna know a "fun" secret?
 * Java enums aren't actually real, which means we get to do *really* cursed things like this.
 * TODO: Implement static injection processing, for referencing a "patched" api and to export for use in plugin.
 * TODO: https://github.com/SpongePowered/Mixin/issues/387
 */
@SuppressWarnings("deprecation")
@Mixin(EntityDamageEvent.DamageModifier.class)
public class DamageModifierMixin {
	@Shadow
	@Final
	@Mutable
	private static DamageModifier[] $VALUES;

	static {
		monumenta$addVariant("IFRAMES");
	}

	@Invoker("<init>")
	public static DamageModifier invokeInit(String internalName, int ord) {
		throw new AssertionError();
	}

	@Unique
	private static DamageModifier monumenta$addVariant(String internalName) {
		if ($VALUES == null)
			throw new AssertionError();

		ArrayList<DamageModifier> variants = new ArrayList<>(Arrays.asList($VALUES));
		var nextOrdinal = variants.get(variants.size() - 1).ordinal() + 1;
		EntityDamageEvent.DamageModifier instance = invokeInit(internalName, nextOrdinal);
		variants.add(instance);
		$VALUES = variants.toArray(new DamageModifier[0]);
		return instance;
	}
}
