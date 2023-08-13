package com.hyc.chatproxy.tcp.proto;

public class CmdType {

    //心跳检测
    public final static String HEARTBEAT = "heartbeat";

    //统计请求体的字节长度
    public final static String BODYSIZE = "bodysize";

    //登录
    public final static String LOGIN = "login";
}
