package com.hyc.chatproxy.controller;

import com.hyc.chatproxy.esl.EslClient;
import org.freeswitch.esl.client.transport.CommandResponse;
import org.freeswitch.esl.client.transport.SendMsg;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eslClient")
public class EslClientController {


    @GetMapping(value = "/sendSyncApiCommand")
    public EslMessage sendSyncApiCommand(String command, String arg ) throws InterruptedException {
        EslMessage eslMessage = EslClient.inboudClient.sendSyncApiCommand(command,arg);

        return eslMessage;
    }


    @GetMapping(value = "/sendAsyncApiCommand")
    public String sendAsyncApiCommand(String command, String arg) throws InterruptedException {
        String eslMessage = EslClient.inboudClient.sendAsyncApiCommand(command,arg);
        return eslMessage;
    }


    @GetMapping(value = "/sendMessage")
    public CommandResponse sendMessage(SendMsg sendMsg ) throws InterruptedException {
        CommandResponse response = EslClient.inboudClient.sendMessage(sendMsg);
        return response;
    }

}
