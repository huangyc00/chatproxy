package com.hyc.chatproxy.tcp.server.service;


import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.hyc.chatproxy.ChatproxyApplication;
import com.hyc.chatproxy.tcp.exception.BusinessException;
import com.hyc.chatproxy.tcp.proto.ChatMessageProto;
import com.hyc.chatproxy.tcp.proto.CmdType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceManager {


    public static ChatMessageProto.MessageResp serviceHandle(ChatMessageProto.MessageReq request) {
        Message respmessage = Any.newBuilder().build();
        String command = request.getCmd();
        Any body = request.getBody();
        try {
            if (command.equals(CmdType.HEARTBEAT)) {
                respmessage = ChatproxyApplication.applicationContext.getBean(HeartBeatService.class).handleWithBody(body);
            }

            if (command.equals(CmdType.BODYSIZE)) {
                respmessage = ChatproxyApplication.applicationContext.getBean(BodySizeService.class).handleWithBody(body);
            }

            if (command.equals(CmdType.LOGIN)) {
                respmessage = ChatproxyApplication.applicationContext.getBean(LoginService.class).handleWithBody(body);
            }

            return ChatMessageProto.MessageResp.newBuilder().setCmd(command)
                    .setData(Any.pack(respmessage)).setCode(0).build();

        } catch (BusinessException businessException) {
            log.warn("business exception:{}", businessException.getMessage());
            return ChatMessageProto.MessageResp.newBuilder().setCmd(command).setErroMsg(businessException.getMessage())
                    .setData(Any.pack(respmessage)).setCode(-1).build();
        } catch (Exception e) {
            log.error("Exception exception:{}", e);
            return ChatMessageProto.MessageResp.newBuilder().setCmd(command).setErroMsg("系统错误")
                    .setData(Any.pack(respmessage)).setCode(500).build();
        }
    }


    enum CommandEnum {
        ping, echo
    }
}
