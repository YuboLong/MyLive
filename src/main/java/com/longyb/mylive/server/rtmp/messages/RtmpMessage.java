package com.longyb.mylive.server.rtmp.messages;

import com.longyb.mylive.server.handlers.RtmpHeader;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author longyubo 2019年12月16日 下午2:38:37
 **/
@Data
public abstract class RtmpMessage {

//	RtmpHeader inboundHeader;

	int inboundHeaderLength;
	int inboundBodyLength;
	
//	public RtmpMessage attachInboundHeader(RtmpHeader theHeader) {
//		inboundHeader = theHeader;
//		return this;
//	}
//
//	public RtmpHeader retrieveInboundHeader() {
//		return inboundHeader;
//	}

	public abstract int getOutboundCsid()  ;
	
	public abstract int getMsgType();
	
	public abstract ByteBuf encodePayload();

}
