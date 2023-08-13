package com.hyc.chatproxy.tcp.server.service;

import com.hyc.chatproxy.tcp.exception.BusinessException;
import com.hyc.chatproxy.tcp.proto.LoginProto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理登录
 */
@Service
public class LoginService extends AbstractHandlerService<LoginProto.LoginReq, LoginProto.LoginResp>{

    Map<String,String> users = new HashMap<>();
    {
        users.put("1001","123456");
        users.put("1002","123456");
        users.put("1003","123456");
        users.put("1004","123456");
    }

    @Override
    public LoginProto.LoginResp handle(LoginProto.LoginReq body) {
        String username = body.getUsername();
        String password = body.getPassword();
        if(!users.containsKey(username)){
            throw new BusinessException("不存在的用户");
        }
        if(!password.equals(users.get(username))){
            throw new BusinessException("密码错误");
        }
        Integer userid = Integer.valueOf(username);
        String token = username + "|" + "password";
        return LoginProto.LoginResp.newBuilder().setId(userid).setToken(token).build();
    }
}
