package com.playmonumenta.papermixins.mixin.optimize;

import io.netty.channel.ChannelHandlerContext;
import io.papermc.paper.util.IntervalledCounter;
import javax.annotation.Nullable;
import net.minecraft.network.Connection;
import net.minecraft.network.SkipPacketException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
	@Shadow
	private boolean stopReadingPackets;
	@Shadow
	private int receivedPackets;
	@Shadow
	@Final
	@Nullable
	protected IntervalledCounter allPacketCounts;
	@Shadow
	@Final
	protected Object PACKET_LIMIT_LOCK;

	@Shadow
	protected abstract void killForPacketSpam();

	@Inject(
		method = "exceptionCaught",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V",
			ordinal = 1 // instanceof SkipPacketException
		)
	)
	@SuppressWarnings("SynchronizeOnNonFinalField")
	public void skipPacketHandling(ChannelHandlerContext channelhandlercontext, Throwable throwable, CallbackInfo ci) {
		// Paper start - packet limiter
		if (throwable instanceof SkipPacketException && !this.stopReadingPackets) {
			this.receivedPackets++;
			if (this.allPacketCounts != null) {
				synchronized (this.PACKET_LIMIT_LOCK) {
					this.allPacketCounts.updateAndAdd(1, System.nanoTime());
					if (this.allPacketCounts.getRate() >= io.papermc.paper.configuration.GlobalConfiguration.get().packetLimiter.allPackets.maxPacketRate()) {
						this.killForPacketSpam();
					}
				}
			}
		}
		// Paper end - packet limiter
	}
}
