package com.longyb.mylive.server.handlers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.longyb.mylive.server.handlers.ConnectionAdapter;
import com.longyb.mylive.server.rtmp.Tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * @author longyubo 2019年12月10日 下午8:38:45
 **/

public class RtmpHandShakeTest {

	@Test
	public void testHandShake() {
		EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ConnectionAdapter(), new HandShakeDecoder());

		ByteBuf buffer = Unpooled.buffer(HandShakeDecoder.VERSION_LENGTH + HandShakeDecoder.HANDSHAKE_LENGTH);

		buffer.writeByte(3);

		int handshakeSize = 1536;

		buffer.writeInt(0);
		buffer.writeInt(0);

		byte[] generateRandomData = Tools.generateRandomData(handshakeSize - 8);
		buffer.writeBytes(generateRandomData);

		embeddedChannel.writeInbound(buffer);

		ByteBuf readOutbound = embeddedChannel.readOutbound();

		int serverVersion = readOutbound.readByte();
		int s1Time = readOutbound.readInt();
		int s1Zero = readOutbound.readInt();
		assertEquals(3, serverVersion);
		assertEquals(0, s1Time);
		assertEquals(0, s1Zero);

		byte[] randomBytes = new byte[handshakeSize - 8];
		readOutbound.readBytes(randomBytes);

		int s2Time = readOutbound.readInt();
		int s2Time2 = readOutbound.readInt();
		assertEquals(0, s2Time);
		assertEquals(0, s2Time2);
		readOutbound.readBytes(randomBytes);

		System.out.println(readOutbound);

	}
}
