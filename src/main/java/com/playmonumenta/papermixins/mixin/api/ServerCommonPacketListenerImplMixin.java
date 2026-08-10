package com.playmonumenta.papermixins.mixin.api;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
	@Shadow
	@Final
	protected ServerPlayer player;

	@WrapMethod(method = "handlePong")
	// the packet doesn't get handled anyway
	private void handlePong(ServerboundPongPacket packet, Operation<Void> original) {
		@Nullable
		ServerPlayer player = this.player;
		if (player == null) {
			return;
		}
		PacketUtils.ensureRunningOnSameThread(packet, (ServerCommonPacketListener) this, player.level().getMinecraftWorld());
	}
}
