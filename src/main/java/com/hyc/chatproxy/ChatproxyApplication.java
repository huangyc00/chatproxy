package com.hyc.chatproxy;

import com.hyc.chatproxy.sip.MySipServer;
import com.hyc.chatproxy.tcp.server.ChatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatproxyApplication {

    public static void main(String[] args) throws Exception {
        new MySipServer().init();
        new ChatServer().startserver(19999);
        SpringApplication.run(ChatproxyApplication.class, args);
    }

}
