package com.hyc.chatproxy.tcp.server.service;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import org.springframework.stereotype.Service;

/**
 * 处理心跳的service
 */
@Service
public class HeartBeatService extends AbstractHandlerService<StringValue,StringValue>{


    @Override
    public StringValue handle(StringValue body) {
        return StringValue.newBuilder().setValue("pong").build();
    }
}
