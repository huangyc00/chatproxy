package com.hyc.chatproxy.esl.entity;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@ToString
public class CallSession {

    private String sessionid;

    //被叫
    private String called;

    //主叫
    private String caller;

    //呼叫方向
    private String direction;

    //caller的context
    private String callerContext ;

    private Long ctime;

    //开始录音时间
    private Long recordStartTime;

    //录音结束时间
    private Long recordEndTime;

    //录制的文件路口经
    private String recorUrl;

    //主叫创建时间
    private String callerCreatetime;

    private Map<String,CallChannel> channelMap = new ConcurrentHashMap<>();

}
