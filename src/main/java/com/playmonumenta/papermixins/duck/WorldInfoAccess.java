package com.playmonumenta.papermixins.duck;

import net.minecraft.server.level.ServerLevel;

public interface WorldInfoAccess {
    ServerLevel.EntityRegionFileStorage monumenta$getRegion();
    void monumenta$setRegion(ServerLevel.EntityRegionFileStorage region);
}
