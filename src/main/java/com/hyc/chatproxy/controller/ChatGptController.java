package com.hyc.chatproxy.controller;

import com.hyc.chatproxy.openai.ChatGptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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


}
