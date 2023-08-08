package com.hyc.chatproxy.tcp.server.service;

import org.springframework.stereotype.Service;

/**
 * 处理心跳的service
 */
@Service
public class HeartBeatService extends AbstractHandlerService{

    @Override
    public String handle(String command) {
        return "pong";
    }
}
