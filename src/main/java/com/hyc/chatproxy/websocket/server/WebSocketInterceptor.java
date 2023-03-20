package com.hyc.chatproxy.websocket.server;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler,  Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            // 模拟用户（通常利用JWT令牌解析用户信息）
            return true;
        }
        return false;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,  WebSocketHandler wsHandler, Exception exception) {

    }
}

