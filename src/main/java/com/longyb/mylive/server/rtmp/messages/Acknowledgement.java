package com.longyb.mylive.server.rtmp.messages;

import com.longyb.mylive.server.rtmp.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author longyubo 2019年12月16日 下午3:41:15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Acknowledgement extends RtmpControlMessage {
	int sequnceNumber;

	@Override
	public ByteBuf encodePayload() {
		return Unpooled.buffer(4).writeInt(sequnceNumber);
	}

	@Override
	public int getMsgType() {
		return Constants.MSG_ACKNOWLEDGEMENT;
	}
}
