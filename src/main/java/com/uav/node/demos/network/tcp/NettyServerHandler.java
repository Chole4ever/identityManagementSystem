package com.example.uav.network.tcp;

import com.example.uav.model.Message;
import com.example.uav.service.GDIDService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Autowired
    private GDIDService gdidService;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        logger.info("This is channel read by command on server" + message.getCommand());
        //gdidService.processMessage(message);
    }
}
