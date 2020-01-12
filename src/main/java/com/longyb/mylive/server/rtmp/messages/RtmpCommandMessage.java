package com.longyb.mylive.server.rtmp.messages;

import java.util.List;

import com.longyb.mylive.amf.AMF0;
import com.longyb.mylive.server.rtmp.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * These messages have been assigned message type value of 20 for AMF0 encoding
 * and message type value of 17 for AMF3 encoding.
 * 
 * @author longyubo 2019年12月16日 下午2:39:49
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RtmpCommandMessage extends RtmpMessage {
	List<Object> command;

	@Override
	public int getOutboundCsid() {
		return 3;

	}

	@Override
	public ByteBuf encodePayload() {
		ByteBuf buffer = Unpooled.buffer();
		AMF0.encode(buffer, command);
		return buffer;
	}

	@Override
	public int getMsgType() {
		return Constants.MSG_TYPE_COMMAND_AMF0;

	}
}
