package com.playmonumenta.papermixins.mixin.bugfix.upgrade;

import com.playmonumenta.papermixins.duck.WorldInfoAccess;
import io.papermc.paper.world.ThreadedWorldUpgrader;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ThreadedWorldUpgrader.WorldInfo.class)
public class WorldInfoMixin implements WorldInfoAccess {
    @Unique
    private ServerLevel.EntityRegionFileStorage monumenta$entityRegionFileStorage;

    @Override
    public ServerLevel.EntityRegionFileStorage monumenta$getRegion() {
        return monumenta$entityRegionFileStorage;
    }

    @Override
    public void monumenta$setRegion(ServerLevel.EntityRegionFileStorage region) {
        monumenta$entityRegionFileStorage = region;
    }
}
