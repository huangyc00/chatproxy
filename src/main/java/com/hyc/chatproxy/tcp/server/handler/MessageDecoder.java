//package com.hyc.chatproxy.tcp.server.handler;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.ByteToMessageDecoder;
//import lombok.extern.slf4j.Slf4j;
//
//import java.nio.charset.Charset;
//import java.util.List;
//
//
//@Slf4j
//public class MessageDecoder extends ByteToMessageDecoder {
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        log.info("bytebuf size:{}",in.readableBytes());
//        int i = in.readableBytes();
//        //如果字节数小于11，则直接返回
//        if(i < 11){
//            return;
//        }
//
//        //做标记
//        in.markReaderIndex();;
//
//
//        //版本
//        byte version = in.readByte();
//        //序列化方式
//        byte serizeType = in.readByte();
//        //请求方式
//        byte direction = in.readByte();
//        //方法类型
//        int method = in.readInt();
//        //请求体长度
//        int dataLength = in.readInt();
//
//        //判读缓冲区的可读是否满足，如果不满足，则回到的mark处
//        if(in.readableBytes() < dataLength){
//            in.resetReaderIndex();
//            return;
//        }
//
//        ByteBuf data = in.readBytes(dataLength);
//        byte[] byteArray = new byte[dataLength];
//        data.readBytes(byteArray);
//        String s = new String(byteArray);
//        log.info("data:{}",s);
//        Message message = new Message();
//        message.setVersion(version);
//        message.setSerizeType(serizeType);
//        message.setDirection(direction);
//        message.setMethod(method);
//        message.setDataLength(dataLength);
//        message.setData(byteArray);
//        out.add(message);
//    }
//}
//
//
//
////    //方法,4个字节
////    private int method;
////
////    //数据字节长度,int长度的整数，4个字节，取值范围为：[2^32 -1,2^32]
////    private int dataLength;
////
////    //数据
////    private byte[] data;
//
