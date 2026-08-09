package com.playmonumenta.papermixins.mixin.api;

import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.paperapi.v1.event.PacketEvent;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PacketUtils.class)
public class PacketUtilsMixin {
	@ModifyVariable(
		method = "lambda$ensureRunningOnSameThread$0",
		at = @At(
			value = "INVOKE",
			target = "Lco/aikar/timings/Timing;startTiming()Lco/aikar/timings/Timing;"
		),
		argsOnly = true
	)
	// For some god knows what reason ServerGamePacketListener does this to make stuff run on main thread...
	private static Packet<?> onHandle(Packet<?> original, @Local(argsOnly = true) PacketListener listener) {
		if (!(listener instanceof ServerGamePacketListenerImpl serverListener)) {
			return original;
		}
		@Nullable
		ServerPlayer player = serverListener.player;
		PacketEvent event = new PacketEvent(player.getBukkitEntity(), PacketEvent.Type.INBOUND, original, null);
		event.callEvent();
		if (event.isCancelled()) {
			return original;
		}
		if (event.packetChanged() && event.getPacket() instanceof Packet<?> newPacket) {
			return newPacket;
		}
		return original;
	}
}
