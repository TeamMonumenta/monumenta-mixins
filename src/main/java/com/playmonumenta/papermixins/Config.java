package com.playmonumenta.papermixins;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Config {
	@ConfigSerializable
	public static class Behavior {
		public boolean disableCoralDeath = false;
		public boolean disableConcreteHardening = false;
		public boolean disableIceBreakBehavior = false;
		public boolean disableIceMelting = false;
		public boolean normalizeArmourUnbreaking = false;
		// NOTE: this will be removed when we update to a version with data-driven enchants
		public int curseOfVanishingMaxLevel = 1;
	}

	@ConfigSerializable
	public static class MCFunction {
		public boolean enableCustomParser = false;
		public int diagnosticContext = 2;
	}

	public int flyingTime = 80;
	public int serverShutdownTime = 1000;
	public Behavior behavior = new Behavior();
	public MCFunction mcFunction = new MCFunction();
}
