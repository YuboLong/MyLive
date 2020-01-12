package com.longyb.mylive.server.entities;
/**
@author longyubo
2020年1月2日 下午3:36:21
**/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.longyb.mylive.amf.AMF0;
import com.longyb.mylive.server.cfg.MyLiveConfig;
import com.longyb.mylive.server.rtmp.Constants;
import com.longyb.mylive.server.rtmp.messages.AudioMessage;
import com.longyb.mylive.server.rtmp.messages.RtmpMediaMessage;
import com.longyb.mylive.server.rtmp.messages.RtmpMessage;
import com.longyb.mylive.server.rtmp.messages.UserControlMessageEvent;
import com.longyb.mylive.server.rtmp.messages.VideoMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Stream {

	static byte[] flvHeader = new byte[] { 0x46, 0x4C, 0x56, 0x01, 0x05, 00, 00, 00, 0x09 };

	Map<String, Object> metadata;

	Channel publisher;

	VideoMessage avcDecoderConfigurationRecord;

	Set<Channel> subscribers;

	List<RtmpMediaMessage> content;

	StreamName streamName;

	int videoTimestamp;
	int audioTimestamp;

 
	FileOutputStream flvout;
	boolean flvHeadAndMetadataWritten = false;

	Set<Channel> httpFLvSubscribers;

	public Stream(StreamName streamName) {
		subscribers = new LinkedHashSet<>();
		httpFLvSubscribers = new LinkedHashSet<>();
		content = new ArrayList<>();
		this.streamName = streamName;
		if (MyLiveConfig.INSTANCE.isSaveFlvFile()) {
			createFileStream();
		}
	}

	public synchronized void addContent(RtmpMediaMessage msg) {

		if (msg instanceof VideoMessage) {
			VideoMessage vm = (VideoMessage) msg;
			if (vm.getTimestamp() != null) {
				// we may encode as FMT1 ,so we need timestamp delta
				vm.setTimestampDelta(vm.getTimestamp() - videoTimestamp);
				videoTimestamp = vm.getTimestamp();
			} else if (vm.getTimestampDelta() != null) {
				videoTimestamp += vm.getTimestampDelta();
				vm.setTimestamp(videoTimestamp);
			}

			if (vm.isAVCDecoderConfigurationRecord()) {
				log.info("avcDecoderConfigurationRecord  ok");
				avcDecoderConfigurationRecord = vm;
			}

			if (vm.isH264KeyFrame()) {
				log.info("video key frame in stream :{}", streamName);
				content.clear();
			}
		}

		if (msg instanceof AudioMessage) {

			AudioMessage am = (AudioMessage) msg;
			if (am.getTimestamp() != null) {
				am.setTimestampDelta(am.getTimestamp() - audioTimestamp);
				audioTimestamp = am.getTimestamp();
			} else if (am.getTimestampDelta() != null) {
				audioTimestamp += am.getTimestampDelta();
				am.setTimestamp(audioTimestamp);
			}
		}

		content.add(msg);
		if (MyLiveConfig.INSTANCE.isSaveFlvFile()) {
			writeFlv(msg);
		}
		broadCastToSubscribers(msg);
	}

	private byte[] encodeMediaAsFlvTagAndPrevTagSize(RtmpMediaMessage msg) {
		int tagType = msg.getMsgType();
		byte[] data = msg.raw();
		int dataSize = data.length;
		int timestamp = msg.getTimestamp() & 0xffffff;
		int timestampExtended = ((msg.getTimestamp() & 0xff000000) >> 24);

		ByteBuf buffer = Unpooled.buffer();

		buffer.writeByte(tagType);
		buffer.writeMedium(dataSize);
		buffer.writeMedium(timestamp);
		buffer.writeByte(timestampExtended);// timestampExtended
		buffer.writeMedium(0);// streamid
		buffer.writeBytes(data);
		buffer.writeInt(data.length + 11); // prevousTagSize

		byte[] r = new byte[buffer.readableBytes()];
		buffer.readBytes(r);

		return r;
	}

	private void writeFlv(RtmpMediaMessage msg) {
		if (flvout == null) {
			log.error("no flv file existed for stream : {}", streamName);
			return;
		}
		try {
			if (!flvHeadAndMetadataWritten) {
				writeFlvHeaderAndMetadata();
				flvHeadAndMetadataWritten = true;
			}
			byte[] encodeMediaAsFlv = encodeMediaAsFlvTagAndPrevTagSize(msg);
			flvout.write(encodeMediaAsFlv);
			flvout.flush();

		} catch (IOException e) {
			log.error("writting flv file failed , stream is :{}", streamName, e);
		}
	}

	private byte[] encodeFlvHeaderAndMetadata() {
		ByteBuf encodeMetaData = encodeMetaData();
		ByteBuf buf = Unpooled.buffer();

		buf.writeBytes(flvHeader);
		buf.writeInt(0); // previousTagSize0

		int readableBytes = encodeMetaData.readableBytes();
		buf.writeByte(0x12); // script
		buf.writeMedium(readableBytes);
		buf.writeInt(0); // timestamp + timestampExtended
		buf.writeMedium(0);// streamid
		buf.writeBytes(encodeMetaData);
		buf.writeInt(readableBytes + 11);

		byte[] result = new byte[buf.readableBytes()];
		buf.readBytes(result);

		return result;

	}

	private void writeFlvHeaderAndMetadata() throws IOException {
		byte[] encodeFlvHeaderAndMetadata = encodeFlvHeaderAndMetadata();
		flvout.write(encodeFlvHeaderAndMetadata);
		flvout.flush();

	}

	private ByteBuf encodeMetaData() {
		ByteBuf buffer = Unpooled.buffer();
		List<Object> meta = new ArrayList<>();
		meta.add("onMetaData");
		meta.add(metadata);
		AMF0.encode(buffer, meta);

		return buffer;
	}

	private void createFileStream() {
		File f = new File(MyLiveConfig.INSTANCE.getSaveFlVFilePath() + "/" + streamName.getApp() + "_" + streamName.getName());
		try {
			FileOutputStream fos = new FileOutputStream(f);
			 
			flvout = fos;

		} catch (IOException e) {
			log.error("create file : {} failed", e);

		}

	}

	public synchronized void addSubscriber(Channel channel) {
		subscribers.add(channel);
		log.info("subscriber : {} is added to stream :{}", channel, streamName);
		avcDecoderConfigurationRecord.setTimestamp(content.get(0).getTimestamp());
		log.info("avcDecoderConfigurationRecord:{}", avcDecoderConfigurationRecord);
		channel.writeAndFlush(avcDecoderConfigurationRecord);
		for (RtmpMessage msg : content) {
			channel.writeAndFlush(msg);
		}

	}

	public synchronized void addHttpFlvSubscriber(Channel channel) {
		httpFLvSubscribers.add(channel);
		log.info("http flv subscriber : {} is added to stream :{}", channel, streamName);

		// 1. write flv header and metaData
		byte[] meta = encodeFlvHeaderAndMetadata();
		channel.writeAndFlush(Unpooled.wrappedBuffer(meta));

		// 2. write avcDecoderConfigurationRecord
		avcDecoderConfigurationRecord.setTimestamp(content.get(0).getTimestamp());
		byte[] config = encodeMediaAsFlvTagAndPrevTagSize(avcDecoderConfigurationRecord);
		channel.writeAndFlush(Unpooled.wrappedBuffer(config));
		// 3. write content

		for (RtmpMediaMessage msg : content) {
			channel.writeAndFlush(Unpooled.wrappedBuffer(encodeMediaAsFlvTagAndPrevTagSize(msg)));
		}

	}

	private synchronized void broadCastToSubscribers(RtmpMediaMessage msg) {
		Iterator<Channel> iterator = subscribers.iterator();
		while (iterator.hasNext()) {
			Channel next = iterator.next();
			if (next.isActive()) {
				next.writeAndFlush(msg);
			} else {
				iterator.remove();
			}
		}

		if (!httpFLvSubscribers.isEmpty()) {
			byte[] encoded = encodeMediaAsFlvTagAndPrevTagSize(msg);
			ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(encoded);
			Iterator<Channel> httpIte = httpFLvSubscribers.iterator();

			while (httpIte.hasNext()) {
				Channel next = httpIte.next();
				if (next.isActive()) {
					next.writeAndFlush(wrappedBuffer);
				} else {
					iterator.remove();
				}
			}
		}

	}

	public synchronized void sendEofToAllSubscriberAndClose() {
		if (MyLiveConfig.INSTANCE.isSaveFlvFile() && flvout != null) {
			try {
				flvout.close();
			} catch (IOException e) {
				log.error("close file:{} failed", flvout);
			}
		}
		for (Channel sc : subscribers) {
			sc.writeAndFlush(UserControlMessageEvent.streamEOF(Constants.DEFAULT_STREAM_ID))
					.addListener(ChannelFutureListener.CLOSE);

		}

		for (Channel sc : httpFLvSubscribers) {
			sc.writeAndFlush(DefaultLastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);

		}

	}

}
