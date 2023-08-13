package com.hyc.chatproxy.tcp.server.handler;

import com.google.protobuf.Any;
import com.google.protobuf.StringValue;
import com.hyc.chatproxy.tcp.proto.ChatMessageProto;
import com.hyc.chatproxy.tcp.proto.CmdType;
import com.hyc.chatproxy.tcp.server.service.ServiceManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BusinessHandler extends SimpleChannelInboundHandler<ChatMessageProto.MessageReq> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessageProto.MessageReq messageReq) throws Exception {
        ChatMessageProto.MessageResp resp = new ServiceManager().serviceHandle(messageReq);
        ctx.writeAndFlush(resp);
    }
}
