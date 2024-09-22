package com.playmonumenta.papermixins;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.semver4j.Semver;

public class VersionInfo {
	public static final String IDENTIFIER = "Monumenta";
	public static final String MOD_ID = "monumenta";
	public static final ModContainer MOD = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
	public static final ModMetadata METADATA = MOD.getMetadata();
	public static final Semver VERSION = new Semver(METADATA.getVersion().toString());
}
