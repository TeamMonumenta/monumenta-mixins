package com.playmonumenta.papermixins.mixin.bugfix.upgrade;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.playmonumenta.papermixins.Config;
import com.playmonumenta.papermixins.MonumentaMod;
import com.playmonumenta.papermixins.duck.WorldInfoAccess;
import io.papermc.paper.world.ThreadedWorldUpgrader;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedWorldUpgrader.ConvertTask.class)
public class ConvertTaskMixin {
    @Shadow
    @Final
    private ThreadedWorldUpgrader.WorldInfo worldInfo;

    @Inject(
        method = "run",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private void upgradeEntity(CallbackInfo ci, @Local ChunkPos pos) throws IOException {
        if (!MonumentaMod.getConfig().behavior.fixWorldUpgrader) {
            return;
        }

        final var entityRegion = ((WorldInfoAccess) (Object) worldInfo).monumenta$getRegion();

        final var data = entityRegion.read(pos);

        if (data == null) {
            return;
        }

        final var dataVersion = data.getInt("DataVersion");
        final var targetVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

        if (dataVersion >= targetVersion) {
            return;
        }

        ThreadedWorldUpgrader.LOGGER.info("[monumenta] upgrading entity chunk @({}, {}) {} -> {}", pos.x, pos.z, dataVersion, targetVersion);
        final var update = DataFixTypes.ENTITY_CHUNK.update(DataFixers.getDataFixer(), data, dataVersion, targetVersion);
        update.putInt("DataVersion", targetVersion);
        entityRegion.write(pos, update);
    }
}