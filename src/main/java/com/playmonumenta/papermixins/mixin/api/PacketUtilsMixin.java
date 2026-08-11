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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketUtils.class)
public class PacketUtilsMixin {
	@Unique
	@Nullable
	private static Packet<?> replacePacket = null;

	@Inject(
		method = "lambda$ensureRunningOnSameThread$0",
		at = @At(
			value = "INVOKE",
			target = "Lco/aikar/timings/Timing;startTiming()Lco/aikar/timings/Timing;"
		),
		cancellable = true
	)
	// For some god knows what reason ServerGamePacketListener does this to make stuff run on main thread...
	private static void onHandle(PacketListener listener, Packet<?> packet, CallbackInfo ci) {
		if (!(listener instanceof ServerGamePacketListenerImpl serverListener)) {
			return;
		}
		@Nullable
		ServerPlayer player = serverListener.player;
		PacketEvent event = new PacketEvent(player.getBukkitEntity(), PacketEvent.Type.INBOUND, packet, false);
		event.callEvent();
		if (event.isCancelled()) {
			ci.cancel();
			return;
		}
		if (event.packetChanged() && event.getPacket() instanceof Packet<?> newPacket) {
			replacePacket = newPacket;
		}
	}

	@ModifyVariable(
		method = "lambda$ensureRunningOnSameThread$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"
		),
		argsOnly = true
	)
	// Modifies variable right after startTiming()
	private static Packet<?> modifyPacket(Packet<?> original) {
		if (replacePacket != null) {
			Packet<?> returnedPacket = replacePacket;
			replacePacket = null;
			return returnedPacket;
		}
		return original;
	}
}
