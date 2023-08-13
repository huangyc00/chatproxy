package com.hyc.chatproxy.tcp.server.service;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractHandlerService<Req extends GeneratedMessageV3, Resp extends Message> {

    /**
     * 获取第一个泛型的类型
     *
     * @return
     */
    public Class getRequestBodyType() {
        Class clazz = null;
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                clazz = (Class) typeArguments[0];
            }
        }
        return clazz;
    }


    public Resp handleWithBody(Any body) throws Exception {
        Class<Req> btype = getRequestBodyType();
        Req unpack = body.unpack(btype);
        Resp resp = handle(unpack);
        return resp;
    }


    public abstract Resp handle(Req body);


    public static void main(String[] args) {
        AbstractHandlerService service = new BodySizeService();
        Class genericType = service.getRequestBodyType();
        System.out.println(genericType);
    }


}
