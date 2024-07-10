package com.floweytf.monumentapaper.mixin.core.bugfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * @author Flowey
 * Remove minecraft's buggy and stupid symlink validation
 */
@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {
    @ModifyExpressionValue(
        method = "validateAndCreateAccess",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/validation/DirectoryValidator;validateDirectory(Ljava/nio/file/Path;" +
                "Z)Ljava/util/List;"
        )
    )
    private List<ForbiddenSymlinkInfo> monumenta$removeSymlinkValidation(List<ForbiddenSymlinkInfo> original) {
        return List.of();
    }
}
