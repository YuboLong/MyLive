package com.longyb.mylive.server.handlers;

import com.longyb.mylive.server.rtmp.Constants;
import com.longyb.mylive.server.rtmp.messages.AudioMessage;
import com.longyb.mylive.server.rtmp.messages.RtmpMessage;
import com.longyb.mylive.server.rtmp.messages.SetChunkSize;
import com.longyb.mylive.server.rtmp.messages.UserControlMessageEvent;
import com.longyb.mylive.server.rtmp.messages.VideoMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author longyubo 2019年12月19日 下午4:11:04
 **/
@Slf4j
public class ChunkEncoder extends MessageToByteEncoder<RtmpMessage> {

	int chunkSize = 128;
	long timestampBegin = System.currentTimeMillis();

	boolean firstVideo = true;
	boolean firstAudio = true;

	@Override
	protected void encode(ChannelHandlerContext ctx, RtmpMessage msg, ByteBuf out) throws Exception {

		if (msg instanceof SetChunkSize) {
			chunkSize = ((SetChunkSize) msg).getChunkSize();
			log.info("chunksize in encoder changed to:{}", chunkSize);
		}

		if (msg instanceof AudioMessage) {
			encodeAudio((AudioMessage) msg, out);
		} else if (msg instanceof VideoMessage) {
			encodeVideo((VideoMessage) msg, out);
		} else {
//			 we encode all msg as fmt_0 and fmt_3 except video and audio
			encodeWithFmt0And3(msg, out);
		}

	}

	private void encodeVideo(VideoMessage msg, ByteBuf out) {
		if (firstVideo) {
			encodeWithFmt0And3(msg, out);
			firstVideo = false;

		} else {
			encodeWithFmt1(msg, out, msg.getTimestampDelta());

		}

	}

	private void encodeAudio(AudioMessage msg, ByteBuf out) {
		if (firstAudio) {
			encodeWithFmt0And3(msg, out);
			firstAudio = false;

		} else {
			encodeWithFmt1(msg, out, msg.getTimestampDelta());

		}

	}

	private void encodeWithFmt1(RtmpMessage msg, ByteBuf out, int timestampDelta) {

		int outboundCsid = msg.getOutboundCsid();
		ByteBuf buffer = Unpooled.buffer();

		buffer.writeBytes(encodeFmtAndCsid(1, outboundCsid));

		ByteBuf payload = msg.encodePayload();
		buffer.writeMedium(timestampDelta);
		buffer.writeMedium(payload.readableBytes());
		buffer.writeByte(msg.getMsgType());

		boolean fmt1Part = true;
		while (payload.isReadable()) {
			int min = Math.min(chunkSize, payload.readableBytes());

			if (fmt1Part) {
				buffer.writeBytes(payload, min);
				fmt1Part = false;
			} else {
				byte[] fmt3BasicHeader = encodeFmtAndCsid(Constants.CHUNK_FMT_3, outboundCsid);
				buffer.writeBytes(fmt3BasicHeader);
				buffer.writeBytes(payload, min);

			}
			out.writeBytes(buffer);
			buffer = Unpooled.buffer();
		}

	}

	private void encodeWithFmt0And3(RtmpMessage msg, ByteBuf out) {
		int csid = msg.getOutboundCsid();

		byte[] basicHeader = encodeFmtAndCsid(0, csid);

		// as for control msg ,we always use 0 timestamp

		ByteBuf payload = msg.encodePayload();
		int messageLength = payload.readableBytes();
		ByteBuf buffer = Unpooled.buffer();

		buffer.writeBytes(basicHeader);

		long timestamp = getRelativeTime();
		boolean needExtraTime = false;
		if (timestamp >= Constants.MAX_TIMESTAMP) {
			needExtraTime = true;
			buffer.writeMedium(Constants.MAX_TIMESTAMP);
		} else {
			buffer.writeMedium((int) timestamp);
		}
		// message length
		buffer.writeMedium(messageLength);

		buffer.writeByte(msg.getMsgType());
		if (msg instanceof UserControlMessageEvent) {
			// message stream id in UserControlMessageEvent is always 0
			buffer.writeIntLE(0);
		} else {
			buffer.writeIntLE(Constants.DEFAULT_STREAM_ID);
		}

		if (needExtraTime) {
			buffer.writeInt((int) (timestamp));
		}
		// split by chunk size

		boolean fmt0Part = true;
		while (payload.isReadable()) {
			int min = Math.min(chunkSize, payload.readableBytes());
			if (fmt0Part) {
				buffer.writeBytes(payload, min);
				fmt0Part = false;
			} else {
				byte[] fmt3BasicHeader = encodeFmtAndCsid(Constants.CHUNK_FMT_3, csid);
				buffer.writeBytes(fmt3BasicHeader);
				buffer.writeBytes(payload, min);

			}
			out.writeBytes(buffer);
			buffer = Unpooled.buffer();
		}
	}

	public long getRelativeTime() {
		return System.currentTimeMillis() - timestampBegin;
	}

	private static byte[] encodeFmtAndCsid(final int fmt, final int csid) {
		if (csid <= 63) {
			return new byte[] { (byte) ((fmt << 6) + csid) };
		} else if (csid <= 320) {
			return new byte[] { (byte) (fmt << 6), (byte) (csid - 64) };
		} else {
			return new byte[] { (byte) ((fmt << 6) | 1), (byte) ((csid - 64) & 0xff), (byte) ((csid - 64) >> 8) };
		}
	}

}
