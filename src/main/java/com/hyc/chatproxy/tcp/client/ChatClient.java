package com.hyc.chatproxy.tcp.client;

import com.google.protobuf.Any;
import com.google.protobuf.StringValue;
import com.hyc.chatproxy.ChatproxyApplication;
import com.hyc.chatproxy.sip.MySipServer;
import com.hyc.chatproxy.tcp.client.handler.HeartbeatClientHandler;
import com.hyc.chatproxy.tcp.client.handler.TcpClientHandler;
import com.hyc.chatproxy.tcp.proto.ChatMessageProto;
import com.hyc.chatproxy.tcp.proto.CmdType;
import com.hyc.chatproxy.tcp.server.ChatServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class ChatClient {

    public final static int RECONNECT_INTERVAL_SECONDS = 3;
    public  static Channel channel;
    private String host;
    private int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ChatproxyApplication.class, args);
        ChatproxyApplication.applicationContext = applicationContext;
        new ChatClient("127.0.0.1", 19999).run();
    }



    public void run() {
        try {
            ChannelFuture channelFuture = doConnect();
            channelFuture.addListener(listener -> {
                boolean success = listener.isSuccess();
                if(success){
                    startScanf(channelFuture.channel());
                    startSendHeartBeat(channelFuture.channel());
                }
            });

            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.addListener(result -> {
                log.error("channel close,reconnect");
                run();
            });
            ChatClient.channel = channelFuture.channel();
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
                        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG)); // 添加LoggingHandler
                        pipeline.addLast(new ProtobufEncoder());
                        pipeline.addLast(new ProtobufDecoder(ChatMessageProto.MessageReq.getDefaultInstance()));
                        pipeline.addLast(new ProtobufDecoder(ChatMessageProto.MessageResp.getDefaultInstance()));
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
                StringValue ping = StringValue.newBuilder().setValue(CmdType.HEARTBEAT).build();
                ChatMessageProto.MessageReq pingMessage = ChatMessageProto.MessageReq.newBuilder().setCmd(CmdType.HEARTBEAT).setBody(Any.pack(ping)).build();
                channel.pipeline().writeAndFlush(pingMessage);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }


    public void startScanf(Channel channel) {
        channel.eventLoop().parent().submit(() -> {
            // 从控制台输入消息并发送给服务器
            Scanner scanner = new Scanner(System.in);
            while (channel.isActive()) {
                String message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }
                if(message.equals(CmdType.BODYSIZE)){
                    StringValue bodysize = StringValue.newBuilder().setValue("nicaiciaciai").build();
                    ChatMessageProto.MessageReq bodysizeMessage = ChatMessageProto.MessageReq.newBuilder().setCmd(CmdType.BODYSIZE)
                            .setBody(Any.pack(bodysize)).build();
                    // 请求服务器
                    channel.writeAndFlush(bodysizeMessage);
                }

            }
        });
    }


}
