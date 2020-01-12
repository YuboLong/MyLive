package com.longyb.mylive.server.rtmp.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class RtmpMediaMessage  extends RtmpMessage{
	Integer timestampDelta;
	Integer timestamp;
	
	public abstract byte[] raw() ;
}
