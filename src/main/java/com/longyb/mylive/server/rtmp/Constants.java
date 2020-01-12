package com.longyb.mylive.server.rtmp;

public class Constants {
	
	public static final int MAX_TIMESTAMP=0XFFFFFF;
	public static final int CHUNK_FMT_0 = 0;
	public static final int CHUNK_FMT_1 = 1;
	public static final int CHUNK_FMT_2 = 2;
	public static final int CHUNK_FMT_3 = 3;

	// control message types
	public static final int MSG_SET_CHUNK_SIZE = 1;
	public static final int MSG_ABORT_MESSAGE = 2;
	public static final int MSG_ACKNOWLEDGEMENT = 3;
	public static final int MSG_WINDOW_ACKNOWLEDGEMENT_SIZE = 5;
	public static final int MSG_SET_PEER_BANDWIDTH = 6;
	
	public static final byte SET_PEER_BANDWIDTH_TYPE_HARD=1;
	public static final byte SET_PEER_BANDWIDTH_TYPE_SOFT=2;
	public static final byte SET_PEER_BANDWIDTH_TYPE_DYNAMIC=3;

	// commands
	public static final int MSG_TYPE_COMMAND_AMF0 = 20;
	public static final int MSG_TYPE_COMMAND_AMF3 = 17;

	// Data Message
	public static final int MSG_TYPE_DATA_MESSAGE_AMF0 = 18;
	public static final int MSG_TYPE_DATA_MESSAGE_AMF3 = 15;

	// Shared Object Message
	public static final int MSG_TYPE_SHARED_OBJECT_MESSAGE_AMF0 = 19;
	public static final int MSG_TYPE_SHARED_OBJECT_MESSAGE_AMF3 = 16;

	public static final int MSG_TYPE_AUDIO_MESSAGE = 8;
	public static final int MSG_TYPE_VIDEO_MESSAGE = 9;

	public static final int MSG_TYPE_AGGREGATE_MESSAGE = 22;

	// User Control Message Events
	public static final int MSG_USER_CONTROL_MESSAGE_EVENTS = 4;
	
	
	public static final byte RTMP_VERSION = 3;
	
	public static final int DEFAULT_STREAM_ID=5;
}
