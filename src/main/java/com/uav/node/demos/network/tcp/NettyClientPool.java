package com.example.uav.network.tcp;


import com.example.uav.model.Message;
import com.example.uav.network.coder.InternalServerMsgCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class NettyClientPool {
    Logger logger = LoggerFactory.getLogger(NettyClientPool.class);
    public final static Map<Integer, CompletableFuture<Message>> RESPONSE_MAP = new ConcurrentHashMap<>();
    private final static int MAX_CONNECTION =10;
    private final ChannelPoolMap<InetSocketAddress, FixedChannelPool> CHANNEL_POOL_MAP ;
    private final Bootstrap bootstrap = new Bootstrap();


    public void SendMessage(InetSocketAddress inetSocketAddress, Message message) {
        FixedChannelPool channelPool = CHANNEL_POOL_MAP.get(inetSocketAddress);
        channelPool.acquire().addListener(future -> {
            if (future.isSuccess()) {
                NioSocketChannel channel = (NioSocketChannel) future.get();
                channel.writeAndFlush(message).addListener(writeFuture -> {
                    if (writeFuture.isSuccess()) {
                        // 处理成功发送消息后的逻辑（如记录日志或处理响应）
                        logger.info("To "+inetSocketAddress.getAddress()+" with command "+message.getCommand());
                    } else {
                        // 处理发送失败的情况
                        writeFuture.cause().printStackTrace();
                    }
                });
            } else {
                // 处理获取连接失败的情况
                future.cause().printStackTrace();
            }
        });
    }



    public static NettyClientPool getInstance(){
        return LazyHolder.INSTANCE;
    }
    private final static class LazyHolder{
        private final static NettyClientPool INSTANCE = new NettyClientPool();
    }
    private NettyClientPool(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                ChannelPipeline channelPipeline = nioSocketChannel.pipeline();
                channelPipeline
                        .addLast("common/codec",new InternalServerMsgCodec(Message.class,Message.class))//入站（解码）+出站（编码）
                        .addLast("handler",new NettyClientHandler());//入站（存入数据response）
            }
        });
        CHANNEL_POOL_MAP = new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
            @Override
            protected FixedChannelPool newPool(InetSocketAddress inetSocketAddress) {
                return new FixedChannelPool(bootstrap.remoteAddress(inetSocketAddress), new AbstractChannelPoolHandler() {
                    @Override
                    public void channelCreated(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast("common/codec",new InternalServerMsgCodec(Message.class,Message.class))//入站（解码）+出站（编码）
                                .addLast("handler",new NettyClientHandler());//入站（存入数据response）
                    }

                },MAX_CONNECTION);
            }
        };


    }

    public CompletableFuture<Message> getResponse(InetSocketAddress inetSocketAddress, Message message) throws ExecutionException, InterruptedException {
        FixedChannelPool channelPool = CHANNEL_POOL_MAP.get(inetSocketAddress);
        CompletableFuture<Message> completableFuture = new CompletableFuture<>();
        channelPool.acquire().sync().addListener(future -> {
            if(future.isSuccess())
            {
                NioSocketChannel channel = (NioSocketChannel) future.get();
                channel.writeAndFlush(message).sync();
            }
        });
        RESPONSE_MAP.put(message.getMemberId(), completableFuture);
        return completableFuture;
    }

}
