package com.hyc.chatproxy.sip;


import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SdpProducter {

    public static String getLocalIPV4() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String ipv4Address = null;

            // 获取所有的 IP 地址，包括 IPv4 和 IPv6
            InetAddress[] addresses = InetAddress.getAllByName(localhost.getHostName());

            // 查找并输出 IPv4 地址
            for (InetAddress address : addresses) {
                if (address.getHostAddress().contains(".")) {
                    ipv4Address = address.getHostAddress();
                    break;
                }
            }

            return ipv4Address;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String createSdpSession(){
        return "v=0";
    }

    public static String createSdpOwner(String ownid,Long sessionid,Integer sessionVersion){
        if(null == sessionid){
            Random random = new Random();
            // 生成一个10位整数的随机数
            long minLimit = 1000000000L; // 最小值为 10^9
            long maxLimit = 9999999999L; // 最大值为 10^10 - 1
            sessionid = minLimit + ((long) (random.nextDouble() * (maxLimit - minLimit)));
        }

        if(null == sessionVersion){
            sessionVersion = 1;
        }

        String ipv4 = getLocalIPV4();

        return "0=" + ownid + " " + sessionid + " " + sessionVersion + " IN " + ipv4;
    }


    public static String createSdpSessionname(String sessionname){
        return "s=" + sessionname;
    }


    public static String createSdpIPV4Connection(){
        return "c=" + "IN" + " " + "IPV4" + " " + getLocalIPV4();
    }


    public static String createSdpTimeDescription(Long startTimestamp,Long endTimestamp){
        if(null == startTimestamp){
            startTimestamp = 0L;
        }
        if(null == endTimestamp){
            endTimestamp = 0L;
        }
        return "t=" + startTimestamp + " " + endTimestamp;
    }

    public static String createSdpAudioDescription(int port){
        String command = "m=" + "audio" + " " + port + "RTP/AVP 106";
        return command;
    }

    public static String createSdpAudioCodecAttr(){
        String command = "a=rtpmap:106 opus/48000/2\r\n" +
                "a=fmtp:106 sprop-maxcapturerate=16000; minptime=20; useinbandfec=1\r\n" +
                "a=sendrecv";
        return command;
    }


    public static String createSdp(){
        String ownid = "chatproxy";
        List<String> list = Arrays.asList(createSdpSession(),
                createSdpOwner(ownid,null,null),
                createSdpSessionname(ownid),
                createSdpIPV4Connection(),
                createSdpTimeDescription(0L,0L),
                createSdpAudioDescription(59999),
                createSdpAudioCodecAttr());
        String sdp = list.stream().collect(Collectors.joining("\r\n"));
        return sdp;
    }

    public static void main(String[] args) {
        String sdp = createSdp();
        System.out.println(sdp);
    }
}
