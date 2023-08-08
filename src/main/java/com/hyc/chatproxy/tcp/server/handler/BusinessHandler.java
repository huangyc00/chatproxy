package com.hyc.chatproxy.tcp.server.handler;

import com.hyc.chatproxy.tcp.server.service.ServiceManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        String result = ServiceManager.serviceHandle(s);
        channelHandlerContext.writeAndFlush(result);
    }


}
