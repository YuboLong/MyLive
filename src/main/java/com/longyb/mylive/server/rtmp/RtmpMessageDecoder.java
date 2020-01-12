package com.longyb.mylive.server.rtmp;
/**
@author longyubo
2019年12月16日 下午3:44:48
**/

import static com.longyb.mylive.server.rtmp.Constants.*;

import java.util.List;

import com.longyb.mylive.amf.AMF0;
import com.longyb.mylive.server.handlers.RtmpHeader;
import com.longyb.mylive.server.rtmp.messages.Abort;
import com.longyb.mylive.server.rtmp.messages.Acknowledgement;
import com.longyb.mylive.server.rtmp.messages.AudioMessage;
import com.longyb.mylive.server.rtmp.messages.RtmpCommandMessage;
import com.longyb.mylive.server.rtmp.messages.RtmpDataMessage;
import com.longyb.mylive.server.rtmp.messages.RtmpMessage;
import com.longyb.mylive.server.rtmp.messages.SetChunkSize;
import com.longyb.mylive.server.rtmp.messages.SetPeerBandwidth;
import com.longyb.mylive.server.rtmp.messages.UserControlMessageEvent;
import com.longyb.mylive.server.rtmp.messages.VideoMessage;
import com.longyb.mylive.server.rtmp.messages.WindowAcknowledgementSize;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RtmpMessageDecoder {

	private RtmpMessageDecoder() {

	}

	public static RtmpMessage decode(RtmpHeader header, ByteBuf payload) {

		RtmpMessage result = null;
		short messageTypeId = header.getMessageTypeId();

		switch (messageTypeId) {
		case MSG_SET_CHUNK_SIZE: {
			int readInt = payload.readInt();
			SetChunkSize setChunkSize = new SetChunkSize();
			setChunkSize.setChunkSize(readInt);
			result = setChunkSize;

		}
			break;
		case MSG_ABORT_MESSAGE: {
			int csid = payload.readInt();
			Abort abort = new Abort(csid);
			result = abort;
		}

			break;
		case MSG_ACKNOWLEDGEMENT: {
			int ack = payload.readInt();
			result = new Acknowledgement(ack);
		}
			break;

		case MSG_WINDOW_ACKNOWLEDGEMENT_SIZE: {
			int size = payload.readInt();
			result = new WindowAcknowledgementSize(size);

		}
			break;

		case MSG_SET_PEER_BANDWIDTH: {
			int ackSize = payload.readInt();
			int type = payload.readByte();
			result = new SetPeerBandwidth(ackSize, type);
		}
			break;

		case MSG_TYPE_COMMAND_AMF0: {
			List<Object> decode = AMF0.decodeAll(payload);
			result = new RtmpCommandMessage(decode);

		}
			break;

		case MSG_USER_CONTROL_MESSAGE_EVENTS: {
			short readShort = payload.readShort();
			int data = payload.readInt();
			result = new UserControlMessageEvent(readShort, data);
		}
			break;

		case MSG_TYPE_AUDIO_MESSAGE: {
			AudioMessage am = new AudioMessage( );
			
			byte[] data = readAll(payload);
			am.setAudioData(data);
			
			if (header.getFmt() == Constants.CHUNK_FMT_0) {
				am.setTimestamp(header.getTimestamp());
			} else if (header.getFmt() == Constants.CHUNK_FMT_1 || header.getFmt() == Constants.CHUNK_FMT_2) {
				am.setTimestampDelta(header.getTimestampDelta());
			}
			result = am;
		}
			break;
		case MSG_TYPE_VIDEO_MESSAGE: {
			VideoMessage vm = new VideoMessage();
			byte[] data = readAll(payload);
			
			vm.setVideoData(data);

			if (header.getFmt() == Constants.CHUNK_FMT_0) {
				vm.setTimestamp(header.getTimestamp());
			} else if (header.getFmt() == Constants.CHUNK_FMT_1 || header.getFmt() == Constants.CHUNK_FMT_2) {
				vm.setTimestampDelta(header.getTimestampDelta());
			}
			result = vm;
		}
			break;
		case MSG_TYPE_DATA_MESSAGE_AMF0: {
			result = new RtmpDataMessage(AMF0.decodeAll(payload));
		}
			break;

		default:
			log.debug("message type id {} payload {}", messageTypeId, payload);
			break;
		}
		if (result != null) {
			result.setInboundBodyLength(header.getMessageLength());
			result.setInboundHeaderLength(header.getHeaderLength());

			log.debug("decode >>{}", result);
			return result;
		} else {
			return null;
		}
	}

	private static byte[] readAll(ByteBuf payload) {
		byte[] all=new byte[payload.readableBytes()];
		payload.readBytes(all);
		return all;
	}
}
