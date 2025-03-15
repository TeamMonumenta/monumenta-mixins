package com.playmonumenta.papermixins;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class VersionInfo {
	public static final String IDENTIFIER = "MonumentaMixins";
	public static final String MOD_ID = "monumenta";
	public static final ModContainer MOD = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
	public static final ModMetadata METADATA = MOD.getMetadata();
	public static final SemanticVersion VERSION = (SemanticVersion) METADATA.getVersion();
}
