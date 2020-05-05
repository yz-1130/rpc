package com.xinyan;

import com.xinyan.handler.ApplicationContextProvider;
import com.xinyan.service.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.xinyan.service","com.xinyan.handler"})
@SpringBootApplication
public class ServerBootstrap {

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(ServerBootstrap.class, args);
        UserServiceImpl.startServer("127.0.0.1",8888);


    }



}
