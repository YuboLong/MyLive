package com.longyb.mylive.server;

import com.longyb.mylive.server.handlers.HttpFlvHandler;
import com.longyb.mylive.server.manager.StreamManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

/**
@author longyubo
2020年1月7日 下午2:55:47
**/
@Slf4j
public class HttpFlvServer {

	private int port;

	ChannelFuture channelFuture;

	EventLoopGroup eventLoopGroup;
	StreamManager streamManager;

	public HttpFlvServer(int port, StreamManager sm) {
		this.port = port;
		this.streamManager = sm;
	}
	
	
	public void run() throws Exception {
		eventLoopGroup = new NioEventLoopGroup();

		ServerBootstrap b = new ServerBootstrap();
		DefaultEventExecutorGroup executor = new DefaultEventExecutorGroup(8);// TODO: USE A CONFIG VALUE
		b.group(eventLoopGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new HttpRequestDecoder());
					 
						ch.pipeline().addLast(new HttpResponseEncoder());
					 
						ch.pipeline().addLast(new HttpFlvHandler(streamManager));
					}
				}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

		channelFuture = b.bind(port).sync();
		log.info("HttpFlv server start , listen at :{}",port);

	}
}

