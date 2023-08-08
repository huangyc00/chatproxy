package com.hyc.chatproxy.tcp.server.service;


import com.hyc.chatproxy.ChatproxyApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServiceManager {


    public static String serviceHandle(String command) {

        if(command.equals(CommandEnum.ping.name())) {
            return ChatproxyApplication.applicationContext.getBean(HeartBeatService.class).handle(command);
        }

        if(command.equals(CommandEnum.echo.name())){
            return ChatproxyApplication.applicationContext.getBean(EchoService.class).handle(command);
        }

        return "ok";
    }



    enum CommandEnum {
        ping,echo
    }
}
