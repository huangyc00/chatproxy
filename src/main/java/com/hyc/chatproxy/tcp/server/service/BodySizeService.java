package com.hyc.chatproxy.tcp.server.service;

import com.google.protobuf.Any;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.StringValue;
import org.springframework.stereotype.Service;

/**
 * 处理心跳的service
 */
@Service
public class BodySizeService extends AbstractHandlerService<StringValue,DoubleValue>{


    @Override
    public DoubleValue handle(StringValue body) {
        String value = body.getValue();
        return DoubleValue.newBuilder().setValue(value.length()).build();
    }


}
