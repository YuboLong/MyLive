package com.longyb.mylive.server.rtmp.messages;

/**
 * message stream id must be 0 and csid must be 2
 * 
 * @author longyubo 2019年12月16日 下午2:38:56
 **/
public abstract class RtmpControlMessage extends RtmpMessage {

	@Override
	public int getOutboundCsid() {
		return 2;
	}
}
