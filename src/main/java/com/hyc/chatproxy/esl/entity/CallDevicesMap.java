package com.hyc.chatproxy.esl.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallDevicesMap {

    public static Map<String,CallSession> callSessionMap = new ConcurrentHashMap<>();


}
