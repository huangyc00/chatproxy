package com.hyc.chatproxy.controller;

import com.google.protobuf.Any;
import com.hyc.chatproxy.openai.ChatGptService;
import com.hyc.chatproxy.tcp.client.ChatClient;
import com.hyc.chatproxy.tcp.proto.ChatMessageProto;
import com.hyc.chatproxy.tcp.proto.CmdType;
import com.hyc.chatproxy.tcp.proto.LoginProto;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/chatgpt")
public class ChatGptController {

    @Autowired
    ChatGptService chatGptService;

    @GetMapping(value = "/getGptResult")
    public HttpEntity<String> getGptResult(String message) throws IOException {
        String s = chatGptService.chatGPTResult(null, message, null, null);
        return new HttpEntity<>(s);
    }

    @GetMapping(value = "/chatLogin")
    public void chatLogin(String username,String password) throws InterruptedException {
        LoginProto.LoginReq loginReq = LoginProto.LoginReq.newBuilder().setUsername(username).setPassword(password).build();
        ChatMessageProto.MessageReq loginMessage = ChatMessageProto.MessageReq.newBuilder().setCmd(CmdType.LOGIN).setBody(Any.pack(loginReq)).build();
        ChannelFuture channelFuture = ChatClient.channel.writeAndFlush(loginMessage).sync();
        channelFuture.addListener(result -> {
            Object now = result.getNow();
            log.info("now:{}",now);
        });
    }

}
