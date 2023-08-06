package com.hyc.chatproxy.tcp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel channel = channelHandlerContext.channel();
        log.info("channel:{} receive msg:{}", channel, s);
        String result = "echo" + s;
        channelHandlerContext.pipeline().writeAndFlush(result);
    }


}
