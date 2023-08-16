package com.hyc.chatproxy.esl;


import com.alibaba.fastjson.JSONObject;
import com.hyc.chatproxy.ChatproxyApplication;
import com.hyc.chatproxy.esl.entity.CallChannel;
import com.hyc.chatproxy.esl.entity.CallDevicesMap;
import com.hyc.chatproxy.esl.entity.CallSession;
import com.hyc.chatproxy.tcp.client.ChatClient;
import lombok.extern.slf4j.Slf4j;
import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class EslClient {

    public static Client inboudClient;

    private String serverIp = "192.168.3.144";

    private Integer port = 8021;

    private String password = "ClueCon";

    private Integer timeoutSeconds = 10;


    public EslClient(String serverIp, Integer port, String password, Integer timeoutSeconds) {
        this.serverIp = serverIp;
        this.port = port;
        this.password = password;
        this.timeoutSeconds = timeoutSeconds;
    }


    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ChatproxyApplication.class, args);
        ChatproxyApplication.applicationContext = applicationContext;
        String serverIp = "192.168.3.144";
        Integer port = 8021;
        String password = "ClueCon";
        Integer timeoutSeconds = 10;
        Client inboudClient = new EslClient(serverIp, port, password, timeoutSeconds).start();
        EslClient.inboudClient = inboudClient;
    }

    //fs_cli -H 192.168.3.144 -P 8021 -p ClueCon
    public Client start() {
        Client inboudClient = new Client();
        try {
            inboudClient.connect(this.serverIp, this.port, this.password, this.timeoutSeconds);
            inboudClient.addEventListener(new IEslEventListener() {
                @Override
                public void eventReceived(EslEvent event) {
                    String eventName = event.getEventName();
                    String headers = JSONObject.toJSONString(event.getEventHeaders());
                    String body = JSONObject.toJSONString(event.getEventBodyLines());
                    log.info("eventName:{}\r\n headers:{} \r\n body:{}", eventName, headers, body);
                    if (eventName.equals("CHANNEL_CREATE")) {
                        handleChannelCreate(JSONObject.parseObject(headers,Map.class));
                    }
                }

                @Override



                public void backgroundJobResultReceived(EslEvent event) {
                    String eventName = event.getEventName();
                    String headers = JSONObject.toJSONString(event.getEventHeaders());
                    String body = JSONObject.toJSONString(event.getEventBodyLines());
                    log.info("eventName:{}\r\n headers:{} \r\n body:{}", eventName, headers, body);
                }

            });
            //CHANNEL_CREATE：当通道被创建时触发，表示有呼叫进入系统。
            //CHANNEL_ANSWER：当通道被应答时触发，表示呼叫已被接听。
            //CHANNEL_HANGUP：当通道挂断时触发，表示呼叫结束。
            //CHANNEL_BRIDGE：当两个通道被桥接在一起时触发，表示呼叫被连接。
            //CHANNEL_UNBRIDGE：当两个通道被解除桥接时触发，表示呼叫的桥接关系被解除。
            //DTMF：当收到 DTMF（双音多频）信号时触发，表示收到按键信号。
            //CUSTOM：自定义事件，您可以在 FreeSWITCH 中触发并定义自己的事件。
            //HEARTBEAT：心跳事件，用于保持与 FreeSWITCH 的连接。
            //PLAYBACK_STOP：当播放停止时触发，表示音频播放结束。
            //RECORD_STOP：当录音结束时触发，表示录音完成。
            //CHANNEL_EXECUTE：在通道上执行某个操作时触发，表示执行了某个应用程序。
            //CHANNEL_EXECUTE_COMPLETE：通道上的应用程序执行完成时触发。
            String events = "CHANNEL_CREATE CHANNEL_ANSWER CHANNEL_HANGUP CHANNEL_BRIDGE CHANNEL_UNBRIDGE RECORD_START RECORD_STOP";
            inboudClient.setEventSubscriptions("plain", events);
            return inboudClient;
        } catch (InboundConnectionFailure inboundConnectionFailure) {
            inboundConnectionFailure.printStackTrace();
            return null;
        }
    }


    public static void handleChannelCreate(Map<String, String> headers) {
        //判断是否已经存在sessionid,如果没有则创建
        String sessionid = headers.get("Core-UUID");
        String called = headers.get("Caller-Destination-Number");
        String caller = headers.get("Caller-ANI");
        String direction = headers.get("Call-Direction");
        String callContext = headers.get("Caller-Context");
        String callerCreatetime = headers.get("Caller-Channel-Created-Time");


        String uniqueUuid = headers.get("Unique-ID");
        String sipname = headers.get("variable_presence_id");


        //设置session的值
        CallSession callSession = CallDevicesMap.callSessionMap.get(sessionid);
        if (null == callSession) {
            callSession = new CallSession();
            callSession.setSessionid(sessionid);
            callSession.setCtime(System.currentTimeMillis());
        }
        callSession.setCallerCreatetime(callerCreatetime);
        callSession.setCaller(caller);
        callSession.setCalled(called);
        callSession.setDirection(direction);
        callSession.setCallerContext(callContext);

        //设置channel的值
        CallChannel callChannel = callSession.getChannelMap().get(uniqueUuid);
        if (null == callChannel) {
            callChannel = new CallChannel();
            callChannel.setUniqueUuid(uniqueUuid);
            callChannel.setSessionid(sessionid);
            callChannel.setCreatetime(System.currentTimeMillis());
        }
        callChannel.setSipname(sipname);
        callChannel.setState("CHANNEL_CREATE");
        callSession.getChannelMap().put(uniqueUuid, callChannel);

        log.info("callsession:{}",callSession);

    }
}
