package com.uav.node.demos.network.tcp;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
public class NettyTCPServer {

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    private static final Logger logger = LoggerFactory.getLogger(NettyTCPServer.class);

    @PostConstruct
    public void init()
    {
        int id = config.getOwnerId();

        String hostName = config.getPeerIps().get(id);
        int port = config.getPeerServerPorts().get(id);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(hostName, port))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    .addLast("common/codec",new InternalServerMsgCodec(Message.class,Message.class))//入站+出站
                                    .addLast("handler",new NettyServerHandler());//入站
                        }


                    });
            try {

                ChannelFuture bindFuture = serverBootstrap.bind().sync();
                logger.info("node: "+config.getOwnerId()+" TCPServer startes on port: {}...", port);

                // 3. 添加异步关闭监听（不阻塞主线程）
                bindFuture.channel().closeFuture().addListener(future -> {
                    logger.info("TCPServer channel closed");
                    // Shutdown the event loop groups after the server channel is closed
                    bossGroup.shutdownGracefully();
                    workGroup.shutdownGracefully();
                });
            } catch (InterruptedException e) {
                // 4. 正确处理线程中断
                Thread.currentThread().interrupt();
                throw new IllegalStateException("TCPServer interrupted during startup", e);
            }



    }
}
