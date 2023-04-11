package com.hyc.chatproxy.openai;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChatGptService {



    public String chatGPTResult(String model, String content, Double temperature, Integer maxTokens) throws IOException {
        if(null == model){
            model = "gpt-3.5-turbo";
        }
        if(null == content){
            content = "max_tokens是什么意思";
        }

        if(null == temperature){
            temperature = 0.5;
        }

        if(null == maxTokens){
            maxTokens = 4000;
        }
        String token = "xxxx";
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 9910));
        OkHttpClient client = new OkHttpClient().newBuilder().proxy(proxy).readTimeout(10*60, TimeUnit.SECONDS).build();
        MediaType mediaType = MediaType.parse("application/json");
//        "messages": [{"role": "user", "content": "Hello!"}]
        JSONObject message = new JSONObject();
        message.put("role","user");
        message.put("content",content);

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("model", model);
        contentMap.put("messages", Arrays.asList(message));
        contentMap.put("temperature", temperature);
        contentMap.put("max_tokens", maxTokens);
        RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(contentMap));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Response response = client.newCall(request).execute();

        String responseString = response.body().string();
        log.info("responseString:{}", responseString);
        JSONObject jsonObject = JSONObject.parseObject(responseString);
        JSONArray jsonArray = jsonObject.getJSONArray("choices");
        String generatedText = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
        log.info("generatedText:{}", generatedText);
        return generatedText;
    }
}

