package com.longyb.mylive.server.rtmp.messages;

import com.longyb.mylive.server.rtmp.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author longyubo 2019年12月16日 下午3:43:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPeerBandwidth extends RtmpControlMessage {
	int acknowledgementWindowSize;
	int limitType;

	@Override
	public ByteBuf encodePayload() {
		return Unpooled.buffer(5).writeInt(acknowledgementWindowSize).writeByte(limitType);

	}

	@Override
	public int getMsgType() {
		return Constants.MSG_SET_PEER_BANDWIDTH;
	}
}
