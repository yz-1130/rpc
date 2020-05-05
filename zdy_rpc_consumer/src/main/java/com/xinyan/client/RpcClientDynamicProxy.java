package com.xinyan.client;

import com.xinyan.pojo.RpcRequest;
import com.xinyan.pojo.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

/**
 * @author xinyan.xie
 * @description
 * @date 2020/5/5
 */
public class RpcClientDynamicProxy<T> implements InvocationHandler {
    private Class<T> clazz;
    public RpcClientDynamicProxy(Class<T> clazz) throws Exception {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        String requestId = UUID.randomUUID().toString();

        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        Class<?>[] parameterTypes = method.getParameterTypes();

        request.setRequestId(requestId);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);
        System.out.println("请求内容: {}"+request);
        //log.info("请求内容: {}",request);

        //开启Netty 客户端，直连
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8888);
        System.out.println("开始连接服务端："+new Date());
        nettyClient.connect();
        RpcResponse send = nettyClient.send(request);
        System.out.println("请求调用返回结果："+ send.getResult());
        return send.getResult();
    }
}

