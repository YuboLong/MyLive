package com.longyb.mylive.server.handlers;

import lombok.Data;

@Data
public class RtmpHeader {
	int csid;
	int fmt;
	int timestamp;

	int messageLength;
	short messageTypeId;
	int messageStreamId;

	int timestampDelta;

	long extendedTimestamp;
	
	//used when response an ack
	int headerLength;
	
	public boolean mayHaveExtendedTimestamp() {
		return  (fmt==0 && timestamp ==  0xFFFFFF) || ( (fmt==1 || fmt==2) && timestampDelta ==  0xFFFFFF); 
	}
}
