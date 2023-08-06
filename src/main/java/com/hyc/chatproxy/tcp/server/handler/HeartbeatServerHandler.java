package com.hyc.chatproxy.tcp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    AtomicInteger count = new AtomicInteger(1);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            Channel channel = ctx.channel();
            if (e.state() == IdleState.READER_IDLE) {
                log.info("can not receive from client,channel:{},count:{}", channel,count);
                if(count.get() > 3){
                    log.warn("channel:{} close because of more than count：{}", channel,count);
                    ctx.close(); //清除后count会被gc
                }else {
                    count.incrementAndGet();
                }
            }
        }
    }

}
