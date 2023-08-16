package com.hyc.chatproxy.esl.entity;

import lombok.Data;


@Data
public class CallChannel {

    //channel的唯一标识
    private String uniqueUuid;

    //绑定session标识
    private String sessionid;

    //CHANNEL_CREATE CHANNEL_ANSWER CHANNEL_HANGUP CHANNEL_BRIDGE CHANNEL_UNBRIDGE RECORD_START RECORD_STOP
    private String state;

    //channel创建的时间
    private Long createtime;

    //channel应答时间
    private Long answertime;

    //挂断时间
    private Long hanguptime;

    //桥接时间
    private Long bridgetime;

    //接触半丁时间
    private Long unbridgetime;

    //构成sipcode@ip
    private String sipname;
}
