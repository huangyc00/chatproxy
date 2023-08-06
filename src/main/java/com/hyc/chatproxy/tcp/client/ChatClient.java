package com.hyc.chatproxy.tcp.client;

import com.hyc.chatproxy.tcp.client.handler.HeartbeatClientHandler;
import com.hyc.chatproxy.tcp.client.handler.TcpClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class ChatClient {

    public final static int RECONNECT_INTERVAL_SECONDS = 3;

    private String host;
    private int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            new ChatClient("127.0.0.1", 19999).run();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        ChannelFuture connectFuture = doConnect();
        if(connectFuture == null){
//            Thread.sleep(5000L);
//            log.info("reconnect");
//            this.run();
//            return;
        }
        Channel channel = connectFuture.channel();
        connectFuture.addListener(result -> {
            boolean success = result.isSuccess();
            if (success) {
                //开始心跳机检测
//                startSendHeartBeat(channel);
            } else {
                Throwable cause = connectFuture.cause();
                //重新进行连接
//                log.info("channel:{} connect fail eventLoopgroup shutdown , run again,error：",channel.id(),cause);
//                //先关闭事件循环组，然后在进行重连
//                Thread.sleep(5000L);
//                this.run();
            }
        });

        //监听关闭时间，如果关闭了，则直接进行重连
        ChannelFuture closeFuture = null;
        try {
            closeFuture = channel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeFuture.addListener(result -> {
//            //重新进行连接
//            log.info("channel:{} close eventLoopgroup shutdown , run again",channel.id(), connectFuture.cause());
//            Thread.sleep(5000L);
//            this.run();
        });
    }

    public ChannelFuture doConnect() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class) // 使用NIO SocketChannel
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG)); // 添加LoggingHandler
                            pipeline.addLast(new LineBasedFrameDecoder(1024)); // 使用LineBasedFrameDecoder
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            // 添加IdleStateHandler，设置写空闲时间为指定的心跳间隔时间
//                            pipeline.addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
//                            pipeline.addLast(new HeartbeatClientHandler()); // 添加自定义的ChannelHandler
                            pipeline.addLast(new TcpClientHandler()); // 添加自定义的ChannelHandler
                        }
                    });
            ChannelFuture connectFuture = bootstrap.connect(this.getHost(), this.getPort()).sync();
            return connectFuture;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            group.shutdownGracefully();
        }

    }

    public void startSendHeartBeat(Channel channel) {
        // 启动心跳定时任务
        channel.eventLoop().parent().scheduleAtFixedRate(() -> {
            if (channel.isActive()) {
                System.out.println("Sending heartbeat...");
                channel.writeAndFlush("Heartbeat");
            }
        }, 0, 2, TimeUnit.SECONDS);
    }


    public void startScanf(Channel channel) {
        channel.eventLoop().parent().submit(() -> {
            // 从控制台输入消息并发送给服务器
            Scanner scanner = new Scanner(System.in);
            while (channel.isActive()) {
                System.out.print("Enter message (type 'exit' to quit): ");
                String message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }
                // 请求服务器
                channel.writeAndFlush(message);
            }
        });
    }


}
