package com.hyc.chatproxy.tcp.server.service;

import org.springframework.stereotype.Service;

@Service
public class EchoService extends AbstractHandlerService{

    @Override
    public String handle(String command) {
        return command;
    }


}
