package com.longyb.mylive.server.rtmp.messages;
/**
@author longyubo
2019年12月16日 下午5:37:47
**/

import com.longyb.mylive.server.rtmp.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class AudioMessage extends RtmpMediaMessage {
	byte[] audioData;

	@Override
	public int getOutboundCsid() {
		return 10;
	}

	@Override
	public ByteBuf encodePayload() {
		return Unpooled.wrappedBuffer(audioData);
	}

	@Override
	public int getMsgType() {
		return Constants.MSG_TYPE_AUDIO_MESSAGE;
	}

	@Override
	public byte[] raw() {

		return audioData;
	}
}
