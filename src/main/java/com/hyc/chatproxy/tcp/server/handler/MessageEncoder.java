//package com.hyc.chatproxy.tcp.server.handler;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToByteEncoder;
//
//
//////序列化方式，1个字节，0表示json，1表示其他
////private byte serizeType;
////
//////方向，分为请求和返回，1个字节
////private byte direction;
////
//////方法,4个字节
////private int method;
////
//////数据字节长度,int长度的整数，4个字节，取值范围为：[2^32 -1,2^32]
////private int dataLength;
////
//////数据
////private byte[] data;
//
//
//public class MessageEncoder extends MessageToByteEncoder<Message> {
//    @Override
//    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
//        out.writeByte(message.getVersion());
//        out.writeByte(message.getSerizeType());
//        out.writeByte(message.getDirection());
//        out.writeInt(message.getMethod());
//        out.writeInt(message.getDataLength());
//        out.writeBytes(message.getData());
//    }
//}
