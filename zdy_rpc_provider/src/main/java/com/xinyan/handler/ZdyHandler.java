package com.xinyan.handler;
import com.xinyan.pojo.RpcRequest;
import com.xinyan.pojo.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

/**
 * @author xinyan.xie
 * @description
 * @date 2020/5/6
 */
@Component
public class ZdyHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

   // private  ApplicationContextProvider applicationContextProvider;
    private RpcRequest rpcRequest;

    private static ApplicationContext applicationContext = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof RpcRequest) {
            rpcRequest = (RpcRequest) msg;
        }
        RpcResponse rpcResponse = new RpcResponse();
       // rpcResponse.setRequestId();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            Object handler = handler(rpcRequest);
            // log.info("获取返回结果: {} ", handler);
            rpcResponse.setResult(handler);
        } catch (Throwable throwable) {
            rpcResponse.setError(throwable.toString());
            throwable.printStackTrace();
        }
        ctx.writeAndFlush(rpcResponse);
    }


    /**
     * 服务端使用代理处理请求
     *
     * @param request
     * @return
     */
    private Object handler(RpcRequest request) throws ClassNotFoundException, InvocationTargetException {
        //使用Class.forName进行加载Class文件
        Class<?> clazz = Class.forName(request.getClassName());
        Object serviceBean = applicationContext.getBean(clazz);
        System.out.println("serviceBean:"+serviceBean);
        Class<?> serviceClass = serviceBean.getClass();
        System.out.println("serverClass"+serviceClass);
        String methodName = request.getMethodName();

        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        //使用CGLIB Reflect
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
        System.out.println("开始调用CGLIB动态代理执行服务端方法...");
        return fastMethod.invoke(serviceBean, parameters);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ZdyHandler.applicationContext = applicationContext;
        System.out.println("初始化applicationContext成功================"+applicationContext);
    }
}
