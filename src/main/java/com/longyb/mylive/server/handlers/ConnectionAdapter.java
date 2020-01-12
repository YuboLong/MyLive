package com.longyb.mylive.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author longyubo 2019年12月10日 上午10:28:04
 **/
@Slf4j
public class ConnectionAdapter extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		log.info("channel active:" + ctx.channel().remoteAddress());
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		log.info("channel inactive:" + ctx.channel().remoteAddress());
		ctx.fireChannelInactive();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		log.error("channel exceptionCaught:" + ctx.channel().remoteAddress(), cause);

	}

}
