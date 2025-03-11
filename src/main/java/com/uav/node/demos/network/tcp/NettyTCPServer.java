package com.example.uav.network.tcp;

import com.example.uav.model.Message;
import com.example.uav.network.coder.InternalServerMsgCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

@Component
public class NettyTCPServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyTCPServer.class);

    @Value("${TCPServer.port}")
    private int port;

    @Value("${tcpServer.hostname}")
    private String hostName;

//    @PostConstruct
//    public void init()
//    {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workGroup = new NioEventLoopGroup();
//        try{
//            ServerBootstrap serverBootstrap = new ServerBootstrap();
//            serverBootstrap
//                    .group(bossGroup,workGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .localAddress(new InetSocketAddress(hostName, port))
//                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
//                        @Override
//                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
//                            nioSocketChannel.pipeline()
//                                    .addLast("common/codec",new InternalServerMsgCodec(Message.class,Message.class))//入站+出站
//                                    .addLast("handler",new NettyServerHandler());//入站
//                        }
//
//
//                    });
//
//            ChannelFuture channelFuture = serverBootstrap.bind().sync();
//            logger.info("server starts...");
//            channelFuture.channel().closeFuture().sync();
//
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            bossGroup.shutdownGracefully();
//            workGroup.shutdownGracefully();
//        }
//
//    }
}
