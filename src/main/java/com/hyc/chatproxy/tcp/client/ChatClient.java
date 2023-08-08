package com.hyc.chatproxy.tcp.client;

import com.hyc.chatproxy.tcp.client.handler.HeartbeatClientHandler;
import com.hyc.chatproxy.tcp.client.handler.TcpClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            ChannelFuture channelFuture = doConnect();
            channelFuture.addListener(listener -> {
                boolean success = listener.isSuccess();
                if(success){
                    startSendHeartBeat(channelFuture.channel());
                    startScanf(channelFuture.channel());
                }
            });

            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.addListener(result -> {
                log.error("channel close,reconnect");
                run();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public ChannelFuture doConnect() throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class) // 使用NIO SocketChannel
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.INFO)); // 添加LoggingHandler
                        pipeline.addLast(new LineBasedFrameDecoder(1024)); // 使用LineBasedFrameDecoder
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new LineEncoder());
//                             添加IdleStateHandler，设置写空闲时间为指定的心跳间隔时间
                        pipeline.addLast(new IdleStateHandler(0, 3, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new HeartbeatClientHandler()); // 添加自定义的ChannelHandler
                        pipeline.addLast(new TcpClientHandler()); // 添加自定义的ChannelHandler
                    }
                });
        ChannelFuture connectFuture = bootstrap.connect(this.getHost(), this.getPort());

        return connectFuture;

    }

    public void startSendHeartBeat(Channel channel) {
        // 启动心跳定时任务
        channel.eventLoop().parent().scheduleAtFixedRate(() -> {
            if (channel.isActive()) {
                log.info("Sending heartbeat...");
                String ping = "ping";
                channel.pipeline().writeAndFlush(ping);
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
