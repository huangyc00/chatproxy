package com.hyc.chatproxy;

import com.hyc.chatproxy.sip.MySipServer;
import com.hyc.chatproxy.tcp.server.ChatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChatproxyApplication {

    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ChatproxyApplication.class, args);
        ChatproxyApplication.applicationContext = applicationContext;
        new MySipServer().init();
        new ChatServer().startserver(19999);
    }

}
