package com.xinyan.service;

import com.xinyan.Encoder.JSONSerializer;
import com.xinyan.Encoder.RpcDecoder;
import com.xinyan.Encoder.RpcEncoder;
import com.xinyan.UserService;
import com.xinyan.handler.ApplicationContextProvider;
import com.xinyan.handler.ServerHandler;
import com.xinyan.handler.ZdyHandler;
import com.xinyan.pojo.RpcRequest;
import com.xinyan.pojo.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Slf4j
@Service
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;
    @Override
    public String sayHello(String word) {
        System.out.println("调用成功--参数 "+word);
        return "调用成功--参数 "+word;
    }
    private static NioEventLoopGroup bossGroup = null;
    private static NioEventLoopGroup workerGroup = null;

    //hostName:ip地址  port:端口号
    public static void startServer(String hostName, int port) throws InterruptedException {

         bossGroup = new NioEventLoopGroup();
         workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(new StringEncoder());
//                        pipeline.addLast(new JsonObjectDecoder());
//                        pipeline.addLast(new UserServerHandler());
                        //添加编码器
                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
                        //添加解码器
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        //添加请求处理器
                        pipeline.addLast(new ZdyHandler());


                    }
                });
            serverBootstrap.bind(hostName,port).sync();


    }

    public static void bind(final ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("端口[ {} ] 绑定成功",port);
            } else {
                log.error("端口[ {} ] 绑定失败", port);
                bind(serverBootstrap, port + 1);
            }
        });
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        log.info("关闭Netty");
    }
}
