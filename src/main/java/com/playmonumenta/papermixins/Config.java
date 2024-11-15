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
		public boolean normalizeArmorUnbreaking = false;
		public boolean disableArrowBouncing = false;
		public boolean disableAnimalPathfindingWeights = false;
		public boolean keepBeeAgroAfterSting = false;
		public boolean disableChargedCreeperHeads = false;
		public boolean disableGuardianMiningFatigue = false;
		public boolean disableGolemAttackRandomness = false;
		public boolean disableControllingPassenger = false;
		public boolean requireMobBowMainhand = false;
		public boolean closedShulkerHurtByArrows = false;
		public boolean disableWitherArrowInvuln = false;
		public boolean disableWitherStarDrop = false;
		public boolean disableDrownConversion = false;
		public boolean disableEndermanPersistence = false;
		public boolean disableEndermanTeleport = false;
		public boolean addGolemAi = false;
		public boolean fixShulkerNoAi = false;
		public boolean crashOnScoreboardLoadFail = false;
		public boolean forceUpgradeIncludeEntities = false;
		public boolean forceUpgradeEagerBlockStates = false;

		public int giantTrackingRange = 96;

		// NOTE: this will be removed when we update to a version with data-driven enchants
		public int curseOfVanishingMaxLevel = 1;
	}

	@ConfigSerializable
	public static class MCFunction {
		public int diagnosticContext = 2;
	}

	public int flyingTime = 80;
	public int serverShutdownTime = 1000;
	public Behavior behavior = new Behavior();
	public MCFunction mcFunction = new MCFunction();
	public boolean disableSymlinkValidation = false;
	public boolean noPlugins = false;
}
