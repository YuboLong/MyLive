package com.longyb.mylive.server.handlers;

import java.util.List;

import com.longyb.mylive.server.rtmp.Tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author longyubo 2019年12月30日 下午6:09:11
 **/
@Slf4j
public class HandShakeDecoder extends ByteToMessageDecoder {

	boolean c0c1done;

	boolean c2done;

	static int HANDSHAKE_LENGTH = 1536;
	static int VERSION_LENGTH = 1;

	// server rtmp version
	static byte S0 = 3;

	byte[] CLIENT_HANDSHAKE = new byte[HANDSHAKE_LENGTH];
	boolean handshakeDone;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		if (handshakeDone) {
			ctx.fireChannelRead(in);
			return;
		}

		ByteBuf buf = in;
		if (!c0c1done) {
			// read c0 and c1
			if (buf.readableBytes() < VERSION_LENGTH + HANDSHAKE_LENGTH) {
				return;
			}

			buf.readByte();

			buf.readBytes(CLIENT_HANDSHAKE);

			writeS0S1S2(ctx);
			c0c1done = true;

		} else {
			// read c2
			if (buf.readableBytes() < HANDSHAKE_LENGTH) {
				return;
			}

			buf.readBytes(CLIENT_HANDSHAKE);

			// handshake done
			CLIENT_HANDSHAKE = null;
			handshakeDone = true;
			ctx.channel().pipeline().remove(this);
		}

	}

	private void writeS0S1S2(ChannelHandlerContext ctx) {
		// S0+S1+S2
		ByteBuf responseBuf = Unpooled.buffer(VERSION_LENGTH + HANDSHAKE_LENGTH + HANDSHAKE_LENGTH);
		// version = 3
		responseBuf.writeByte(S0);
		// s1 time
		responseBuf.writeInt(0);
		// s1 zero
		responseBuf.writeInt(0);
		// s1 random bytes
		responseBuf.writeBytes(Tools.generateRandomData(HANDSHAKE_LENGTH - 8));
		// s2 time
		responseBuf.writeInt(0);
		// s2 time2
		responseBuf.writeInt(0);
		// s2 random bytes
		responseBuf.writeBytes(Tools.generateRandomData(HANDSHAKE_LENGTH - 8));

		ctx.writeAndFlush(responseBuf);
	}

}
