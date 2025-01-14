package com.playmonumenta.papermixins.mixin.behavior.net;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.playmonumenta.papermixins.MonumentaMod;
import net.minecraft.network.Varint21LengthFieldPrepender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Varint21LengthFieldPrepender.class)
public class Varint21LengthFieldPrependerMixin {
	@ModifyExpressionValue(
		method = "encode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lio/netty/buffer/ByteBuf;)V",
		at = @At(value = "CONSTANT", args = "intValue=3")
	)
	public int encode0(int constant) {
		return MonumentaMod.getConfig().behavior.packetLengthVarIntSize;
	}

	@ModifyArg(
		method = "encode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lio/netty/buffer/ByteBuf;)V",
		at = @At(
			value = "INVOKE",
			target = "Lio/netty/handler/codec/EncoderException;<init>(Ljava/lang/String;)V"
		),
		index = 0
	)
	public String modifyExceptionMessage(String msg, @Local(ordinal = 0) int i) {
		return "unable to encode %s as a %s byte varint".formatted(
			i,
			MonumentaMod.getConfig().behavior.packetLengthVarIntSize
		);
	}
}
