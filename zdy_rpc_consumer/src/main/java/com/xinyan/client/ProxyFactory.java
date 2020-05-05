package com.xinyan.client;

import java.lang.reflect.Proxy;

/**
 * @author xinyan.xie
 * @description
 * @date 2020/5/5
 */
public class ProxyFactory {
    public static <T> T create(Class<T> interfaceClass) throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[] {interfaceClass}, new RpcClientDynamicProxy<T>(interfaceClass));
    }
}
