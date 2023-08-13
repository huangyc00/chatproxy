package com.hyc.chatproxy.tcp.server;

import com.hyc.chatproxy.tcp.proto.ChatMessageProto;
import com.hyc.chatproxy.tcp.server.handler.BusinessHandler;
import com.hyc.chatproxy.tcp.server.handler.ExceptionCatchHandler;
import com.hyc.chatproxy.tcp.server.handler.HeartbeatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatServer {

    public void startserver(int port) {
        EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workEventLoopGroup = new NioEventLoopGroup();

        // 创建一个工作线程池
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(10);


        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossEventLoopGroup, workEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG)); // 添加LoggingHandler
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new ProtobufDecoder(ChatMessageProto.MessageReq.getDefaultInstance()));
                            pipeline.addLast(new ProtobufDecoder(ChatMessageProto.MessageResp.getDefaultInstance()));
                            pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS)); // 添加IdleStateHandler，设置读空闲时间为5
                            pipeline.addLast(new HeartbeatServerHandler()); // 添加IdleStateHandler，设置读空闲时间为5
                            pipeline.addLast(serviceHandlerGroup,new BusinessHandler());
                            pipeline.addLast(new ExceptionCatchHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("ChatServer start at port:" + port);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("ChatServer error:", e);
        } finally {
            bossEventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }
    }
}
