package com.xinyan.client;

import com.xinyan.UserService;

/**
 * @author xinyan.xie
 * @description
 * @date 2020/5/5
 */
public class ClientApplication {
    public static void main(String[] args) throws Exception {
        UserService helloService = ProxyFactory.create(UserService.class);
        System.out.println("响应结果“:"+helloService.sayHello("你大爷"));
    }
}
