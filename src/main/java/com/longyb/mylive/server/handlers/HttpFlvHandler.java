package com.longyb.mylive.server.handlers;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

import java.util.List;

import com.google.common.base.Splitter;
import com.longyb.mylive.server.entities.Stream;
import com.longyb.mylive.server.entities.StreamName;
import com.longyb.mylive.server.manager.StreamManager;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * @author longyubo 2020年1月7日 下午3:19:43
 **/
public class HttpFlvHandler extends SimpleChannelInboundHandler<HttpObject> {

	StreamManager streamManager;

	public HttpFlvHandler(StreamManager streamManager) {
		this.streamManager = streamManager;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;

			String uri = req.uri();
			List<String> appAndStreamName = Splitter.on("/").omitEmptyStrings().splitToList(uri);
			if (appAndStreamName.size() != 2) {
				httpResponseStreamNotExist(ctx, uri);
				return;
			}

			StreamName sn = new StreamName(appAndStreamName.get(0), appAndStreamName.get(1));
			Stream stream = streamManager.getStream(sn);

			if (stream == null) {
				httpResponseStreamNotExist(ctx, uri);
				return;
			}
			DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			response.headers().set(CONTENT_TYPE, "video/x-flv");
			response.headers().set(TRANSFER_ENCODING, "chunked");
			ctx.writeAndFlush(response);

			stream.addHttpFlvSubscriber(ctx.channel());

		}

		if (msg instanceof HttpContent) {

		}

	}

	private void httpResponseStreamNotExist(ChannelHandlerContext ctx, String uri) {
		ByteBuf body = Unpooled.wrappedBuffer(("stream [" + uri + "] not exist").getBytes());
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.NOT_FOUND, body);
		response.headers().set(CONTENT_TYPE, "text/plain");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

}
