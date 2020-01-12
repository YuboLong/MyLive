package com.longyb.mylive.server.rtmp.messages;
/**
@author longyubo
2019年12月16日 下午5:28:17
**/

import com.longyb.mylive.server.rtmp.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserControlMessageEvent extends RtmpMessage {

	short eventType;
	int data;

	@Override
	public ByteBuf encodePayload() {
		ByteBuf buffer = Unpooled.buffer(6);
		buffer.writeShort(eventType);
		buffer.writeInt(data);
		return buffer;
	}

	@Override
	public int getOutboundCsid() {
		return 2;
	}

	@Override
	public int getMsgType() {
		return Constants.MSG_USER_CONTROL_MESSAGE_EVENTS;
	}

	public static UserControlMessageEvent streamBegin(int streamId) {
		UserControlMessageEvent e = new UserControlMessageEvent((short) 0,streamId );
		return e;
	}

	public static UserControlMessageEvent streamEOF(int streamId) {
		UserControlMessageEvent e = new UserControlMessageEvent((short) 1,streamId);
		return e;
	}

	public static UserControlMessageEvent streamDry(int streamId) {
		UserControlMessageEvent e = new UserControlMessageEvent((short) 2,streamId);
		return e;
	}
	
	public static UserControlMessageEvent setBufferLength(int bufferLengthInms) {
		UserControlMessageEvent e = new UserControlMessageEvent((short) 3,bufferLengthInms);
		return e;
	}
	
	public boolean isBufferLength() {
		return eventType==3;
	}

 

}
